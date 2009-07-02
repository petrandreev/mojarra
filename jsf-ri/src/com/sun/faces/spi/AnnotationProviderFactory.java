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

package com.sun.faces.spi;

import com.sun.faces.config.AnnotationScanner;
import com.sun.faces.util.FacesLogger;

import javax.servlet.ServletContext;
import javax.faces.FacesException;
import java.lang.reflect.Constructor;

/**
 * 
 */
public class AnnotationProviderFactory {

    private static final Class<? extends AnnotationProvider> DEFAULT_ANNOTATION_PROVIDER =
       AnnotationScanner.class;

    private static final String ANNOTATION_PROVIDER_SERVICE_KEY =
         "com.sun.faces.spi.annotationprovider";


    // ---------------------------------------------------------- Public Methods


    public static AnnotationProvider createAnnotationProvider(ServletContext sc) {

        String[] services = ServiceFactoryUtils.getServiceEntries(ANNOTATION_PROVIDER_SERVICE_KEY);
        if (services.length > 0) {
            // only use the first entry...
            Object provider = ServiceFactoryUtils.getProviderFromEntry(services[0], new Class[] { ServletContext.class }, new Object[] { sc });
            if (provider == null) {
                return createDefaultProvider(sc);
            }
            if (!(provider instanceof AnnotationProvider)) {
                throw new FacesException("Class " + provider.getClass().getName() + " is not an instance of com.sun.faces.spi.AnnotationProvider");
            }
            return (AnnotationProvider) provider;
        } else {
            return createDefaultProvider(sc);
        }
        
    }


    // --------------------------------------------------------- Private Methods


    private static AnnotationProvider createDefaultProvider(ServletContext sc) {

        try {
            Constructor c = DEFAULT_ANNOTATION_PROVIDER.getDeclaredConstructor(new Class<?>[] { ServletContext.class });
            return (AnnotationProvider) c.newInstance(sc);
        } catch (Exception e) {
            throw new FacesException(e);
        }

    }
}
