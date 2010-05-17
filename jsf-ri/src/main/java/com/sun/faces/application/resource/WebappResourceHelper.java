/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.util.FacesLogger;

import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.CacheResourceModificationTimestamp;

/**
 * <p>
 * A {@link ResourceHelper} implementation for finding/serving resources
 * found within <code>&lt;contextroot&gt;/resources</code> directory of a
 * web application.
 * </p>
 *
 * @since 2.0
 */
public class WebappResourceHelper extends ResourceHelper {

    private static final Logger LOGGER = FacesLogger.RESOURCE.getLogger();

    private static final String BASE_RESOURCE_PATH = "/resources";

    private boolean cacheTimestamp;


    // ------------------------------------------------------------ Constructors


    public WebappResourceHelper() {

        WebConfiguration webconfig = WebConfiguration.getInstance();
        cacheTimestamp = webconfig.isOptionEnabled(CacheResourceModificationTimestamp);

    }


    // --------------------------------------------- Methods from ResourceHelper


    /**
     * @see com.sun.faces.application.resource.ResourceHelper#getBaseResourcePath()
     */
    public String getBaseResourcePath() {

        return BASE_RESOURCE_PATH;

    }


    /**
     * @see ResourceHelper#getInputStream(ResourceInfo,javax.faces.context.FacesContext)
     */
    protected InputStream getNonCompressedInputStream(ResourceInfo resource, FacesContext ctx)
    throws IOException {

        return ctx.getExternalContext().getResourceAsStream(resource.getPath());

    }


    /**
     * @see ResourceHelper#getURL(ResourceInfo, javax.faces.context.FacesContext)
     */
    public URL getURL(ResourceInfo resource, FacesContext ctx) {

        try {
            return ctx.getExternalContext().getResource(resource.getPath());
        } catch (MalformedURLException e) {
            return null;
        }

    }


    /**
     * @see ResourceHelper#findLibrary(String, String, javax.faces.context.FacesContext)
     */
    public LibraryInfo findLibrary(String libraryName,
                                   String localePrefix,
                                   FacesContext ctx) {

        String path;
        if (localePrefix == null) {
            path = getBaseResourcePath() + '/' + libraryName;
        } else {
            path = getBaseResourcePath()
                   + '/'
                   + localePrefix
                   + '/'
                   + libraryName;
        }
        Set<String> resourcePaths =
              ctx.getExternalContext().getResourcePaths(path);
        // it could be possible that there exists an empty directory
        // that is representing the library, but if it's empty, treat it
        // as non-existant and return null.
        if (resourcePaths != null && !resourcePaths.isEmpty()) {
            VersionInfo version = getVersion(resourcePaths, false);
                return new LibraryInfo(libraryName, version, localePrefix, this);
        }

        return null;
    }


    /**
     * @see ResourceHelper#findResource(LibraryInfo, String, String, boolean, javax.faces.context.FacesContext)
     */
    public ResourceInfo findResource(LibraryInfo library,
                                     String resourceName,
                                     String localePrefix,
                                     boolean compressable,
                                     FacesContext ctx) {

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

        // first check to see if the resource exists, if not, return null.  Let
        // the caller decide what to do.
        try {
            if (ctx.getExternalContext().getResource(basePath) == null) {
                return null;
            }
        } catch (MalformedURLException e) {
            throw new FacesException(e);
        }

        // we got to hear, so we know the resource exists (either as a directory
        // or file)
        Set<String> resourcePaths =
              ctx.getExternalContext().getResourcePaths(basePath);
        // if getResourcePaths returns null or an empty set, this means that we have
        // a non-directory resource, therefor, this resource isn't versioned.
        ResourceInfo value;
        if (resourcePaths == null || resourcePaths.size() == 0) {
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
        } else {
            // ok, subdirectories exist, so find the latest 'version' directory
            VersionInfo version = getVersion(resourcePaths, true);
            if (version == null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                               "jsf.application.resource.unable_to_determine_resource_version.",
                               resourceName);
                    return null;
                }
            }
            if (library != null) {
                value = new ResourceInfo(library,
                                         resourceName,
                                         version,
                                         compressable,
                                         resourceSupportsEL(resourceName, ctx),
                                         ctx.isProjectStage(ProjectStage.Development),
                                         cacheTimestamp);
            } else {
                value = new ResourceInfo(resourceName,
                                         version,
                                         localePrefix,
                                         this,
                                         compressable,
                                         resourceSupportsEL(resourceName, ctx),
                                         ctx.isProjectStage(ProjectStage.Development),
                                         cacheTimestamp);
            }
        }

        if (value.isCompressable()) {
            value = handleCompression(value);
        }
        return value;

    }


}
