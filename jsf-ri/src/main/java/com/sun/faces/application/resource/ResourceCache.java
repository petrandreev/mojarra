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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.WebContextInitParameter;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.Util;
import com.sun.faces.util.MultiKeyConcurrentHashMap;

/**
 * <p>
 * This is the caching mechanism for caching ResourceInfo instances to
 * offset the cost of looking up the resource.
 * </p>
 *
 * <p>
 * This cache uses a background thread to check for modifications to the underlying
 * webapp or JAR files containing resources.  This check is periodic, configurable
 * via context init param <code>com.sun.faces.resourceUpdateCheckPeriod</code>.  Through
 * this config option, the cache can also be made static or completely disabled.
 * If the value of of this option is <code>0</code>, then no check will be made
 * making the cache static.  If value of this option is <code>less than 0</code>,
 * then no caching will be perfomed.  Otherwise, the value of the option will
 * be the number of minutes between modification checks.
 * </p>
 */
public class ResourceCache {

    private static final Logger LOGGER = FacesLogger.RESOURCE.getLogger();


    /**
     * The <code>ResourceInfo<code> cache.
     */
    private MultiKeyConcurrentHashMap<String,ResourceInfoCheckPeriodProxy> resourceCache;


    /**
     * Resource check period in minutes.
     */
    private long checkPeriod;


    // ------------------------------------------------------------ Constructors


    /**
     * Constructs a new ResourceCache.
     */
    public ResourceCache() {

        WebConfiguration config = WebConfiguration.getInstance();
        assert (config != null);
        ServletContext sc = config.getServletContext();
        long period = getCheckPeriod(config);
        checkPeriod = ((period != -1) ? period * 1000L * 60L : -1);
        resourceCache = new MultiKeyConcurrentHashMap<String,ResourceInfoCheckPeriodProxy>(30);
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE,
                       "ResourceCache constructed for {0}.  Check period is {1} minutes.",
                       new Object[] { getServletContextIdentifier(sc), checkPeriod });
        }

    }


    // ---------------------------------------------------------- Public Methods


    /**
     * Add the {@link ResourceInfo} to the internal cache.
     *
     * @param info resource metadata
     *
     * @return previous value associated with specified key, or null
     *  if there was no mapping for key
     */
    public ResourceInfo add(ResourceInfo info) {

        Util.notNull("info", info);

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE,
                       "Caching ResourceInfo: {0}",
                       info.toString());
        }
        ResourceInfoCheckPeriodProxy proxy =
              resourceCache.putIfAbsent(info.name,
                                        info.libraryName,
                                        info.localePrefix,
                                        new ResourceInfoCheckPeriodProxy(info, checkPeriod));
        return ((proxy != null) ? proxy.getResourceInfo() : null);

    }


    /**
     * @param name the resource name
     * @param libraryName the library name
     * @param localePrefix the locale prefix
     * @return the {@link ResourceInfo} associated with <code>key<code>
     *  if any.
     */
    public ResourceInfo get(String name, String libraryName, String localePrefix) {

        Util.notNull("name", name);

        ResourceInfoCheckPeriodProxy proxy =
              resourceCache.get(name, libraryName, localePrefix);
        if (proxy != null && proxy.needsRefreshed()) {
            resourceCache.remove(name, libraryName, localePrefix);
            return null;
        } else {
            return ((proxy != null) ? proxy.getResourceInfo() : null);
        }

    }


    /**
     * <p>Empty the cache.</p>
     */
    public void clear() {

        resourceCache.clear();
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Cache Cleared");
        }

    }


    // --------------------------------------------------------- Private Methods


    private Long getCheckPeriod(WebConfiguration webConfig) {

        String val = webConfig.getOptionValue(WebContextInitParameter.ResourceUpdateCheckPeriod);
        try {
            return (Long.parseLong(val));
        } catch (NumberFormatException nfe) {
            return Long.parseLong(WebContextInitParameter.ResourceUpdateCheckPeriod.getDefaultValue());
        }

    }


    private static String getServletContextIdentifier(ServletContext context) {

        if (context.getMajorVersion() == 2 && context.getMinorVersion() < 5) {
            return context.getServletContextName();
        } else {
            return context.getContextPath();
        }

    }


    // ---------------------------------------------------------- Nested Classes


    private static final class ResourceInfoCheckPeriodProxy {

        private ResourceInfo resourceInfo;
        private Long checkTime;


        // -------------------------------------------------------- Constructors


        public ResourceInfoCheckPeriodProxy(ResourceInfo resourceInfo,
                                            long checkPeriod) {

            this.resourceInfo = resourceInfo;
            if (checkPeriod != -1L) {
                if (!(resourceInfo.getHelper() instanceof ClasspathResourceHelper)) {
                    checkTime = System.currentTimeMillis() + checkPeriod;
                }
            }

        }


        private boolean needsRefreshed() {

            return (checkTime != null
                       && (checkTime < System.currentTimeMillis()));

        }


        private ResourceInfo getResourceInfo() {

            return resourceInfo;

        }

    } // END ResourceInfoCheckPeriodProxy


} // END ResourceCache
