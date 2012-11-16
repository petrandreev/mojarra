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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.util.Util;

import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.CacheResourceModificationTimestamp;
import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.EnableMissingResourceLibraryDetection;


/**
 * <p>
 * A {@link ResourceHelper} implementation for finding/serving resources
 * found on the classpath within the <code>META-INF/resources directory.
 * </p>
 *
 * @since 2.0
 */
class ClasspathResourceHelper extends ResourceHelper {


    private static final String BASE_RESOURCE_PATH = "META-INF/resources";
    private static final String BASE_CONTRACTS_PATH = "META-INF/contracts";
    private boolean cacheTimestamp;
    private volatile ZipDirectoryEntryScanner libraryScanner;
    private boolean enableMissingResourceLibraryDetection;



    // ------------------------------------------------------------ Constructors


    public ClasspathResourceHelper() {

        WebConfiguration webconfig = WebConfiguration.getInstance();
        cacheTimestamp = webconfig.isOptionEnabled(CacheResourceModificationTimestamp);
        enableMissingResourceLibraryDetection =
                webconfig.isOptionEnabled(EnableMissingResourceLibraryDetection);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClasspathResourceHelper other = (ClasspathResourceHelper) obj;
        if (this.cacheTimestamp != other.cacheTimestamp) {
            return false;
        }
        if (this.enableMissingResourceLibraryDetection != other.enableMissingResourceLibraryDetection) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.cacheTimestamp ? 1 : 0);
        hash = 67 * hash + (this.enableMissingResourceLibraryDetection ? 1 : 0);
        return hash;
    }

    

    // --------------------------------------------- Methods from ResourceHelper


    /**
     * @see com.sun.faces.application.resource.ResourceHelper#getBaseResourcePath()
     */
    public String getBaseResourcePath() {

        return BASE_RESOURCE_PATH;

    }

    @Override
    public String getBaseContractsPath() {
        return BASE_CONTRACTS_PATH;
    }
    
    /**
     * @see ResourceHelper#getNonCompressedInputStream(com.sun.faces.application.resource.ResourceInfo, javax.faces.context.FacesContext)
     */
    protected InputStream getNonCompressedInputStream(ResourceInfo resource, FacesContext ctx) throws IOException {

        InputStream in = null;
        
        if (ctx.isProjectStage(ProjectStage.Development)) {
            ClassLoader loader = Util.getCurrentLoader(getClass());
            String path = resource.getPath();
            if (loader.getResource(path) != null) {
                in = loader.getResource(path).openStream();
            }
            if (in == null && getClass().getClassLoader().getResource(path) != null) {
                in = getClass().getClassLoader().getResource(path).openStream();
            }
        } else {        
            ClassLoader loader = Util.getCurrentLoader(getClass());
            String path = resource.getPath();
            in = loader.getResourceAsStream(path);
            if (in == null) {
                in = getClass().getClassLoader().getResourceAsStream(path);
            }
        }
        return in;
    }


    /**
     * @see ResourceHelper#getURL(com.sun.faces.application.resource.ResourceInfo, javax.faces.context.FacesContext)
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
                return null;
            }
        }

        return new LibraryInfo(libraryName, null, localePrefix, this);
        
    }

    public LibraryInfo findLibraryWithZipDirectoryEntryScan(String libraryName,
                                   String localePrefix,
                                   FacesContext ctx, boolean forceScan) {

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
                if (null != localePrefix && libraryName.equals("javax.faces")) {
                    return null;
                }
                if (enableMissingResourceLibraryDetection || forceScan) {
                    if (null == libraryScanner) {
                        libraryScanner = new ZipDirectoryEntryScanner();
                    }
                    if (!libraryScanner.libraryExists(libraryName, localePrefix)) {
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

        resourceName = trimLeadingSlash(resourceName);
        ContractInfo [] outContract = new ContractInfo[1];
        outContract[0] = null;
        
        ClassLoader loader = Util.getCurrentLoader(this);
        String basePath;
        if (library != null) {
            basePath = library.getPath(localePrefix) + '/' + resourceName;
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
                // Try it without the localePrefix
                if (library != null) {
                    basePath = library.getPath(null) + '/' + resourceName;
                } else {
                    basePath = getBaseResourcePath() + '/' + resourceName;
                }
                basePathURL = loader.getResource(basePath);
                if (basePathURL == null) {
                    // try using this class' loader (necessary when running in OSGi)
                    basePathURL = this.getClass().getClassLoader().getResource(basePath);
                    if (basePathURL == null) {
                        return null;
                    }
                }

                localePrefix = null;
            }
        }

        ClientResourceInfo value;

        if (library != null) {
            value = new ClientResourceInfo(library,
                                     outContract[0],
                                     resourceName,
                                     null,
                                     compressable,
                                     resourceSupportsEL(resourceName, library.getName(), ctx),
                                     ctx.isProjectStage(ProjectStage.Development),
                                     cacheTimestamp);
        } else {
            value = new ClientResourceInfo(outContract[0],
                                     resourceName,
                                     null,
                                     localePrefix,
                                     this,
                                     compressable,
                                     resourceSupportsEL(resourceName, null, ctx),
                                     ctx.isProjectStage(ProjectStage.Development),
                                     cacheTimestamp);
        }
        
        if (value.isCompressable()) {
            value = handleCompression(value);
        }
        return value;

    }

    
}
