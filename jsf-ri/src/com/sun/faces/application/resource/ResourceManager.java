/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.faces.application.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.faces.application.ProjectStage;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import com.sun.faces.util.Util;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.config.WebConfiguration;

/**
 * This class is used to lookup {@link ResourceInfo} instances
 * and cache any that are successfully looked up to reduce the
 * computational overhead with the scanning/version checking.
 *
 * @since 2.0
 */
public class ResourceManager {

    private static final Logger LOGGER = FacesLogger.RESOURCE.getLogger();

    /**
     * {@link Pattern} for valid mime types to configure compression.
     */
    private static final Pattern CONFIG_MIMETYPE_PATTERN =
          Pattern.compile("[a-z-]*/[a-z0-9.\\*-]*");

    /**
     * {@link ResourceHelper} used for looking up webapp-based resources.
     */
    private ResourceHelper webappHelper = WebappResourceHelper.getInstance();

    /**
     * {@link ResourceHelper} used for looking up classpath-based resources.
     */
    private ResourceHelper classpathHelper = ClasspathResourceHelper.getInstance();

    /**
     * Cache for storing {@link ResourceInfo} instances to reduce the cost
     * of the resource lookups.
     */
    private ResourceCache cache;

    /**
     * Patterns used to find {@link ResourceInfo} instances that may have their
     * content compressed.
     */
    private List<Pattern> compressableTypes;

    /**
     * This lock is used to ensure the lookup of compressable {@link ResourceInfo}
     * instances are atomic to prevent theading issues when writing the compressed
     * content during a lookup.
     */
    private ReentrantLock lock = new ReentrantLock();


    // ------------------------------------------------------------ Constructors


    /**
     * Constructs a new <code>ResourceManager</code>.  Note:  if the current
     * {@link ProjectStage} is {@link ProjectStage#Development} caching or
     * {@link ResourceInfo} instances will not occur.
     */
    public ResourceManager(ResourceCache cache) {

        this.cache = cache;
        initCompressableTypes();

    }


    // ------------------------------------------------------ Public Methods


    /**
     * <p>
     * Attempt to lookup a {@link ResourceInfo} based on the specified
     * <code>libraryName<code> and <code>resourceName</code>
     * </p>
     *
     * <p>
     * Implementation Note:  Synchronization is necessary when looking up
     * compressed resources.  This ensures the atomicity of the content
     * being compressed.  As such, the cost of doing this is low as once
     * the resource is in the cache, the lookup won't be performed again
     * until the cache is cleared.  That said, it's not a good idea
     * to have caching disabled in a production environment if leveraging
     * compression.
     *
     * If the resource isn't compressable, then we don't worry about creating
     * a few extra copies of ResourceInfo until the cache is populated.
     * </p>
     *
     * @param libraryName the name of the library (if any)
     * @param resourceName the name of the resource
     * @param contentType the content type of the resource.  This will be
     *  used to determine if the resource is compressable
     * @param ctx the {@link javax.faces.context.FacesContext} for the current
     *  request
     *  
     * @return a {@link ResourceInfo} if a resource if found matching the
     *  provided arguments, otherwise, return <code>null</code>
     */
    public ResourceInfo findResource(String libraryName,
                                     String resourceName,
                                     String contentType,
                                     FacesContext ctx) {

        String localePrefix = getLocalePrefix(ctx);
        ResourceInfo info =
              getFromCache(resourceName, libraryName, localePrefix);
        if (info == null) {
            boolean compressable = isCompressable(contentType, ctx);
            if (compressable) {
                lock.lock();
                try {
                    info = getFromCache(resourceName, libraryName, localePrefix);
                    if (info == null) {
                        info = doLookup(libraryName,
                                        resourceName,
                                        localePrefix,
                                        compressable,
                                        ctx);
                        if (info != null) {
                            addToCache(info);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                info = doLookup(libraryName,
                                resourceName,
                                localePrefix,
                                compressable,
                                ctx);
                if (info != null) {
                    addToCache(info);
                }
            }

        }

        return info;

    }


    // ----------------------------------------------------- Private Methods


    /**
     * Attempt to look up the Resource based on the provided details.
     *
     * @param libraryName the name of the library (if any)
     * @param resourceName the name of the resource
     * @param localePrefix the locale prefix for this resource (if any)
     * @param compressable if this resource can be compressed
     * @param ctx the {@link javax.faces.context.FacesContext} for the current
     *  request
     *
     * @return a {@link ResourceInfo} if a resource if found matching the
     *  provided arguments, otherwise, return <code>null</code>
     */
    private ResourceInfo doLookup(String libraryName,
                                  String resourceName,
                                  String localePrefix,
                                  boolean compressable,
                                  FacesContext ctx) {
        
        LibraryInfo library = null;
        if (libraryName != null) {
            library = findLibrary(libraryName, localePrefix, ctx);
            if (library == null && localePrefix != null) {
                // no localized library found.  Try to find
                // a library that isn't localized.
                library = findLibrary(libraryName, null, ctx);
            }
            if (library == null) {
                return null;
            }
        }

        String resName = trimLeadingSlash(resourceName);
        ResourceInfo info =
              findResource(library, resName, localePrefix, compressable, ctx);
        if (info == null && localePrefix != null) {
            // no localized resource found, try to find a
            // resource that isn't localized
            info = findResource(library, resName, null, compressable, ctx);
        }
        return info;

    }


    /**
     * @param name the resource name
     * @param library the library name
     * @param localePrefix the Locale prefix
     * @return the {@link ResourceInfo} from the cache or <code>null</code>
     *  if no cached entry is found
     */
    private ResourceInfo getFromCache(String name,
                                      String library,
                                      String localePrefix) {

        if (cache == null) {
            return null;
        }
        return cache.get(name, library, localePrefix);

    }


    /**
     * Adds the the specified {@link ResourceInfo} to the cache.
     * @param info the @{link ResourceInfo} to add.
     */
    private void addToCache(ResourceInfo info) {

        if (cache == null) {
            return;
        }
        cache.add(info);

    }


    /**
     * <p> Attempt to lookup and return a {@link LibraryInfo} based on the
     * specified <code>arguments</code>.
     * <p/>
     * <p> The lookup process will first search the file system of the web
     * application.  If the library is not found, then it processed to
     * searching the classpath.</p>
     * <p/>
     * <p> If a library is found, this method will return a {@link
     * LibraryInfo} instance that contains the name, version, and {@link
     * ResourceHelper}.</p>
     *
     * @param libraryName the library to find
     * @param localePrefix the prefix for the desired locale
     * @param ctx         the {@link FacesContext} for the current request
     *
     * @return the Library instance for the specified library
     */
     LibraryInfo findLibrary(String libraryName,
                             String localePrefix,
                             FacesContext ctx) {

        LibraryInfo library = webappHelper.findLibrary(libraryName,
                                                       localePrefix,
                                                       ctx);
        if (library == null) {
            library = classpathHelper.findLibrary(libraryName,
                                                  localePrefix,
                                                  ctx);
        }

        // if not library is found at this point, let the caller deal with it
        return library;
    }


   /**
     * <p> Attempt to lookup and return a {@link ResourceInfo} based on the
     * specified <code>arguments</code>.
     * <p/>
     * <p> The lookup process will first search the file system of the web
     * application.  If the library is not found, then it processed to
     * searching the classpath.</p>
     * <p/>
     * <p> If a library is found, this method will return a {@link
     * LibraryInfo} instance that contains the name, version, and {@link
     * ResourceHelper}.</p>
     *
     * @param library the library the resource should be found in
     * @param resourceName the name of the resource
     * @param localePrefix the prefix for the desired locale
     * @param compressable <code>true</code> if the resource can be compressed
     * @param ctx the {@link javax.faces.context.FacesContext} for the current request
     *
     * @return the Library instance for the specified library
     */
    private ResourceInfo findResource(LibraryInfo library,
                                      String resourceName,
                                      String localePrefix,
                                      boolean compressable,
                                      FacesContext ctx) {

        if (library != null) {
            return library.getHelper().findResource(library,
                                                    resourceName,
                                                    localePrefix,
                                                    compressable,
                                                    ctx);
        } else {
            ResourceInfo resource = webappHelper.findResource(null,
                                                              resourceName,
                                                              localePrefix,
                                                              compressable,
                                                              ctx);
            if (resource == null) {
                resource = classpathHelper.findResource(null,
                                                        resourceName,
                                                        localePrefix,
                                                        compressable, 
                                                        ctx);
            }
            return resource;
        }

    }


    /**
     * <p>
     * Obtains the application configured message resources for the current
     * request locale.  If a ResourceBundle is found and contains the key
     * <code>javax.faces.resource.localePrefix</code>, use the value associated
     * with that key as the prefix for locale specific resources.
     * </p>
     *
     * <p>
     * For example, say the request locale is en_US, and
     * <code>javax.faces.resourceLocalePrefix</code> is found with a value of
     * <code>en</code>, a resource path within a web application might look like
     * <code>/resources/en/corp/images/greetings.jpg</code>
     * </p>
     *
     * @param context the {@link FacesContext} for the current request
     * @return the localePrefix based on the current request, or <code>null</code>
     *  if no prefix can be determined
     */
    private String getLocalePrefix(FacesContext context) {

        String localePrefix = null;
        String appBundleName = context.getApplication().getMessageBundle();
        if (null != appBundleName) {
            Locale locale = 
                  context.getApplication().getViewHandler().calculateLocale(context);
                try {
                    ResourceBundle appBundle =
                          ResourceBundle.getBundle(appBundleName,
                                                   locale,
                                                   Util.getCurrentLoader(ResourceManager.class));
                    localePrefix =
                          appBundle
                                .getString(ResourceHandler.LOCALE_PREFIX);
                } catch (MissingResourceException ignored) { }
        }
        return localePrefix;

    }


    /**
     * @param s input String
     * @return the String without a leading slash if it has one.
     */
    private String trimLeadingSlash(String s) {

        if (s.charAt(0) == '/') {
            return s.substring(1);
        } else {
            return s;
        }

    }


    /**
     * @param contentType content-type in question
     * @param ctx the @{link FacesContext} for the current request
     * @return <code>true</code> if this resource can be compressed, otherwise
     *  <code>false</code>
     */
    private boolean isCompressable(String contentType, FacesContext ctx) {

        // No compression when developing.
        if (contentType == null || ctx.isProjectStage(ProjectStage.Development)) {
            return false;
        } else {
            if (compressableTypes != null && !compressableTypes.isEmpty()) {
                for (Pattern p : compressableTypes) {
                    boolean matches = p.matcher(contentType).matches();
                    if (matches) {
                        return true;
                    }
                }
            }
        }

        return false;

    }


    /**
     * Init <code>compressableTypes</code> from the configuration.
     */
    private void initCompressableTypes() {

        WebConfiguration config = WebConfiguration.getInstance();
        String value = config.getOptionValue(WebConfiguration.WebContextInitParameter.CompressableMimeTypes);
        if (value != null && value.length() > 0) {
            String[] values = Util.split(value, ",");
            if (values != null) {
                for (String s : values) {
                    String pattern = s.trim();
                    if (!isPatternValid(pattern)) {
                        continue;
                    }
                    if (pattern.endsWith("/*")) {
                        pattern = pattern.substring(0, pattern.indexOf("/*"));
                        pattern += "/[a-z0-9.-]*";
                    }
                    if (compressableTypes == null) {
                        compressableTypes = new ArrayList<Pattern>(values.length);
                    }
                    try {
                        compressableTypes.add(Pattern.compile(pattern));
                    } catch (PatternSyntaxException pse) {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            // PENDING i18n
                            LOGGER.log(Level.WARNING,
                                       "Mime type {0} doesn't match expected pattern {1}: ignoring.",
                                       new Object[] { pattern, pse.getPattern()});
                        }
                    }
                }
            }
        }

    }


    /**
     * @param input input mime-type pattern from the configuration
     * @return <code>true</code> if the input matches the expected pattern,
     *  otherwise <code>false</code>
     */
    private boolean isPatternValid(String input) {

        return (CONFIG_MIMETYPE_PATTERN.matcher(input).matches());

    }


}
