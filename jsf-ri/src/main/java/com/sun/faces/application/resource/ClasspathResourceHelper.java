/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.util.Util;

import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.CacheResourceModificationTimestamp;


/**
 * <p>
 * A {@link ResourceHelper} implementation for finding/serving resources
 * found on the classpath within the <code>META-INF/resources directory.
 * </p>
 *
 * @since 2.0
 */
public class ClasspathResourceHelper extends ResourceHelper {


    private static final String BASE_RESOURCE_PATH = "META-INF/resources";
    private boolean cacheTimestamp;
    private Map<Boolean, ZipDirectoryEntryScanner> libraryScannerHolder;


    // ------------------------------------------------------------ Constructors


    public ClasspathResourceHelper() {

        WebConfiguration webconfig = WebConfiguration.getInstance();
        cacheTimestamp = webconfig.isOptionEnabled(CacheResourceModificationTimestamp);
        libraryScannerHolder = new ConcurrentHashMap<Boolean, ZipDirectoryEntryScanner>(1);
    }


    // --------------------------------------------- Methods from ResourceHelper


    /**
     * @see com.sun.faces.application.resource.ResourceHelper#getBaseResourcePath()
     */
    public String getBaseResourcePath() {

        return BASE_RESOURCE_PATH;

    }


    /**
     * @see ResourceHelper#getNonCompressedInputStream(ResourceInfo, javax.faces.context.FacesContext)
     */
    protected InputStream getNonCompressedInputStream(ResourceInfo resource, FacesContext ctx)
    throws IOException {

        ClassLoader loader = Util.getCurrentLoader(this.getClass());
        String path = resource.getPath();
        InputStream in = loader.getResourceAsStream(path);
        if (in == null) {
            // try using this class' loader (necessary when running in OSGi)
            in = this.getClass().getClassLoader().getResourceAsStream(path);
        }
        return in;

    }


    /**
     * @see com.sun.faces.application.resource.ResourceHelper#getURL(ResourceInfo, javax.faces.context.FacesContext)
     */
    public URL getURL(ResourceInfo resource, FacesContext ctx) {

        ClassLoader loader = Util.getCurrentLoader(this.getClass());
        URL url = loader.getResource(resource.getPath());
        if (url == null) {
            // try using this class' loader (necessary when running in OSGi)
            url = this.getClass().getClassLoader().getResource(resource.getPath());
        }
        return url;

    }


    /**
     * @see ResourceHelper#findLibrary(String, String, javax.faces.context.FacesContext)
     */
    public LibraryInfo findLibrary(String libraryName,
                                   String localePrefix,
                                   FacesContext ctx) {

        ClassLoader loader = Util.getCurrentLoader(this);
        String basePath;
        if (localePrefix == null) {
            basePath = getBaseResourcePath() + '/' + libraryName + '/';
        } else {
            basePath = getBaseResourcePath()
                       + '/'
                       + localePrefix
                       + '/'
                       + libraryName
                       + '/';
        }

        URL basePathURL = loader.getResource(basePath);
        if (basePathURL == null) {
            // try using this class' loader (necessary when running in OSGi)
            basePathURL = this.getClass().getClassLoader().getResource(basePath);
            if (basePathURL == null) {
                // This does not work on GlassFish 3.1 due to GLASSFISH-16229.
                Set<String> resourcePaths = ctx.getExternalContext().getResourcePaths("/" + libraryName + "/");
                if (null != resourcePaths && !resourcePaths.isEmpty()) {
                    boolean foundEntryStartingWithMetaInfResources = false;
                    Iterator<String> iter = resourcePaths.iterator();
                    // Ensure that at least one entry starts with /META-INF/resources.
                    while (!foundEntryStartingWithMetaInfResources && iter.hasNext()) {
                        foundEntryStartingWithMetaInfResources = iter.next().startsWith("/META-INF/resources");
                    }
                    if (!foundEntryStartingWithMetaInfResources) {
                        return null;
                    }
                } else {
                    //
                    if (null != localePrefix && libraryName.equals("javax.faces")) {
                        return null;
                    }

                    // housekeeping for concurrency
                    ZipDirectoryEntryScanner scanner = libraryScannerHolder.get(Boolean.TRUE);
                    if (null == scanner) {
                        scanner = new ZipDirectoryEntryScanner();
                        libraryScannerHolder.put(Boolean.TRUE, scanner);
                    }
                    if (!scanner.libraryExists(libraryName)) {
                        return null;
                    }
                }
            }
        }

        return new LibraryInfo(libraryName, null, localePrefix, this);
        
    }

    /**
     * @see ResourceHelper#findResource(LibraryInfo, String, String, boolean, javax.faces.context.FacesContext)
     */
    public ResourceInfo findResource(LibraryInfo library,
                                     String resourceName,
                                     String localePrefix,
                                     boolean compressable,
                                     FacesContext ctx) {

        ClassLoader loader = Util.getCurrentLoader(this);
        String basePath;
        if (library != null) {
            basePath = library.getPath() + '/' + resourceName;
        } else {
            if (localePrefix == null) {
                basePath = getBaseResourcePath() + '/' + resourceName;
            } else {
                basePath = getBaseResourcePath()
                           + '/'
                           + localePrefix
                           + '/'
                           + resourceName;
            }
        }

        URL basePathURL = loader.getResource(basePath);
        if (basePathURL == null) {
            // try using this class' loader (necessary when running in OSGi)
            basePathURL = this.getClass().getClassLoader().getResource(basePath);
            if (basePathURL == null) {
                return null;
            }
        }

        ResourceInfo value;

        if (library != null) {
            value = new ResourceInfo(library,
                                     resourceName,
                                     null,
                                     compressable,
                                     resourceSupportsEL(resourceName, ctx),
                                     ctx.isProjectStage(ProjectStage.Development),
                                     cacheTimestamp);
        } else {
            value = new ResourceInfo(resourceName,
                                     null,
                                     localePrefix,
                                     this,
                                     compressable,
                                     resourceSupportsEL(resourceName, ctx),
                                     ctx.isProjectStage(ProjectStage.Development),
                                     cacheTimestamp);
        }
        
        if (value.isCompressable()) {
            value = handleCompression(value);
        }
        return value;

    }

    
}
