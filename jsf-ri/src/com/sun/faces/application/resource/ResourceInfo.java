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

import java.io.File;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;

import com.sun.faces.util.FacesLogger;

/**
 * <p/>
 * <code>ResourceInfo</code> is a simple wrapper class for information
 * pertainant to building a complete resource path using a Library.
 * <p/>
 */
public class ResourceInfo {

    private static final Logger LOGGER = FacesLogger.RESOURCE.getLogger();
    private static final String COMPRESSED_CONTENT_DIRECTORY =
          "jsf-compressed";

    String name;
    String libraryName;
    String localePrefix;
    private VersionInfo version;
    private ResourceHelper helper;
    private LibraryInfo library;
    private String path;
    private String compressedPath;
    private boolean compressible;
    private boolean supportsEL;


    /**
     * Constructs a new <code>ResourceInfo</code> using the specified details.
     * The {@link ResourceHelper} of the resource will be the same as the
     * {@link ResourceHelper} of the {@link LibraryInfo}.
     * @param library the library containing this resource
     * @param name the resource name
     * @param version the version of this resource (if any)
     * @param compressible if this resource should be compressed
     * @param supportsEL <code>true</code> if this resource may contain
     *   EL expressions
     * @param isDevStage true if this context is development stage
     */
    public ResourceInfo(LibraryInfo library,
                        String name,
                        VersionInfo version,
                        boolean compressible,
                        boolean supportsEL,
                        boolean isDevStage) {
        this.name = name;
        this.version = version;
        this.helper = library.getHelper();
        this.library = library;
        this.libraryName = library.getName();
        this.localePrefix = library.getLocalePrefix();
        this.compressible = compressible;
        this.supportsEL = supportsEL;
        initPath(isDevStage);
    }

    /**
     * Constructs a new <code>ResourceInfo</code> using the specified details.
     * @param name the resource name
     * @param version the version of the resource
     * @param localePrefix the locale prefix for this resource (if any)
     * @param helper helper the helper class for this resource
     * @param compressible if this resource should be compressed
     * @param supportsEL <code>true</code> if this resource may contain
     *   EL expressions
     */
    ResourceInfo(String name,
                 VersionInfo version,
                 String localePrefix,
                 ResourceHelper helper,
                 boolean compressible,
                 boolean supportsEL,
                 boolean isDevStage) {
        this.name = name;
        this.version = version;
        this.localePrefix = localePrefix;
        this.helper = helper;
        this.compressible = compressible;
        this.supportsEL = supportsEL;
        initPath(isDevStage);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final ResourceInfo other = (ResourceInfo) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.libraryName == null) ? (other.libraryName != null) : !this.libraryName.equals(other.libraryName)) {
            return false;
        }
        if ((this.localePrefix == null) ? (other.localePrefix != null) : !this.localePrefix.equals(other.localePrefix)) {
            return false;
        }
        if (this.version != other.version && (this.version == null || !this.version.equals(other.version))) {
            return false;
        }
        if (this.library != other.library && (this.library == null || !this.library.equals(other.library))) {
            return false;
        }
        if ((this.path == null) ? (other.path != null) : !this.path.equals(other.path)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.libraryName != null ? this.libraryName.hashCode() : 0);
        hash = 97 * hash + (this.localePrefix != null ? this.localePrefix.hashCode() : 0);
        hash = 97 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 97 * hash + (this.library != null ? this.library.hashCode() : 0);
        hash = 97 * hash + (this.path != null ? this.path.hashCode() : 0);
        return hash;
    }

    /**
     * @return return the library name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return return the version of the resource, or <code>null</code> if the
     *         resource isn't versioned.
     */
    public VersionInfo getVersion() {
        return version;
    }

   /**
     * @return return the {@link ResourceHelper} for this resource
     */
    public ResourceHelper getHelper() {
        return helper;
    }

    /**
     * @return the Library associated with this resource, if any.
     */
    public LibraryInfo getLibraryInfo() {
        return library;
    }

    /**
     * @return the Locale prefix, if any.
     */
    public String getLocalePrefix() {
        return localePrefix;   
    }

    /**
     * @return the full path (including the library, if any) of the
     *  resource.
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the path to which the compressed bits for this resource
     *  reside.  If this resource isn't compressible and this method is called,
     *  it will return <code>null</code>
     */
    public String getCompressedPath() {
        return compressedPath;
    }

    /**
     * @return <code>true</code> if this resource should be compressed,
     *  otherwise <code>false</code>
     */
    public boolean isCompressable() {
        return compressible;
    }

    /**
     * @return <code>true</code> if the this resource may contain EL expressions
     *  that should be evaluated, otherwise, return <code>false</code>
     */
    public boolean supportsEL() {
        return supportsEL;
    }

    /**
     * Disables EL evaluation for this resource. 
     */
    public void disableEL() {
        this.supportsEL = false;
    }

    @Override
    public String toString() {
        return "ResourceInfo{" +
               "name='" + name + '\'' +
               ", version=\'" + ((version != null) ? version : "NONE") + '\'' +
               ", libraryName='" + libraryName + '\'' +
               ", libraryVersion='" + ((library != null) ? library.getVersion() : "NONE") + '\'' +
               ", localePrefix='" + ((localePrefix != null) ? localePrefix : "NONE") + '\'' +
               ", path='" + path + '\'' +
               ", compressible='" + compressible + '\'' +
               ", compressedPath=" + compressedPath +
               '}';
    }

    // --------------------------------------------------------- Private Methods


    /**
     * Create the full path to the resource.  If the resource can be compressed,
     * setup the compressedPath ivar so that the path refers to the
     * directory refereneced by the context attribute <code>javax.servlet.context.tempdir</code>.  
     */
    private void initPath(boolean isDevStage) {

        StringBuilder sb = new StringBuilder(32);
        if (library != null) {
            sb.append(library.getPath());
        } else {
            sb.append(helper.getBaseResourcePath());
        }
        if (library == null && localePrefix != null) {
            sb.append('/').append(localePrefix);
        }
        // Specialcasing for handling jsf.js in uncompressed state
        if (isDevStage && "javax.faces".equals(libraryName) && "jsf.js".equals(name)) {
            sb.append('/').append("jsf-uncompressed.js");
        } else {
            sb.append('/').append(name);
        }
        if (version != null) {
            sb.append('/').append(version.getVersion());
            String extension = version.getExtension();
            if (extension != null) {
                sb.append('.').append(extension);    
            }
        }
        path = sb.toString();

        if (compressible && !supportsEL) { // compression for static resources
            FacesContext ctx = FacesContext.getCurrentInstance();
            File servletTmpDir = (File) ctx.getExternalContext()
                  .getApplicationMap().get("javax.servlet.context.tempdir");
            if (servletTmpDir == null || !servletTmpDir.isDirectory()) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               "File ({0}) referenced by javax.servlet.context.tempdir attribute is null, or was is not a directory.  Compression for {1} will be unavailable.",
                               new Object[]{((servletTmpDir == null)
                                             ? "null"
                                             : servletTmpDir.toString()),
                                            path});
                }
                compressible = false;
            } else {
                String tPath = ((path.charAt(0) == '/') ? path : '/' + path);
                File newDir = new File(servletTmpDir, COMPRESSED_CONTENT_DIRECTORY
                                                      + tPath);

                try {
                    if (!newDir.exists()) {
                        if (newDir.mkdirs()) {
                            compressedPath = newDir.getCanonicalPath();
                        } else {
                            compressible = false;
                            if (LOGGER.isLoggable(Level.WARNING)) {
                                LOGGER.log(Level.WARNING,
                                           "jsf.application.resource.unable_to_create_compression_directory",
                                           newDir.getCanonicalPath());
                            }
                        }
                    } else {
                        compressedPath = newDir.getCanonicalPath();
                    }
                } catch (Exception e) {
                	if (LOGGER.isLoggable(Level.SEVERE)) {
	                    LOGGER.log(Level.SEVERE,
	                               e.toString(),
	                               e);
                	}
                    compressible = false;
                }
            }
        }
        
    }

}
