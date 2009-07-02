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

import javax.servlet.ServletContext;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.Map;

/**
 * <p>
 * An integration point for integrators to provide custom annotation scanning.
 * </p>
 *
 * <p>
 * <em>All</em> <code>AnnotationProvider</code> implementations <em>must</em>
 * scan for the following annotations:
 * </p>
 * <ul>
 *  <li>FacesComponent</li>
 *  <li>FacesConverter</li>
 *  <li>FacesRenderer</li>
 *  <li>ManagedBean</li>
 *  <li>NamedEvent</li>
 *  <li>FacesBehavior</li>
 *  <li>FacesBehaviorRenderer</li>
 * </ul>
 *
 * <p>
 * The <code>AnnotationProvider</code> instance will be wrapped as a {@link java.util.concurrent.Future} and
 * executed during the environment initialization.  The result of the future can be obtained
 * by calling {@link com.sun.faces.config.ConfigManager#getAnnotatedClasses(javax.faces.context.FacesContext)}.
 * </p>
 *
 * <p>
 * The {@link java.util.concurrent.Future} itself can be obtained from the
 * application map using the key <code>com.sun.faces.config.ConfigManager__ANNOTATION_SCAN_TASK</code>.
 * </p>
 *
 * <p>
 * It's important to note that the value returned by either method described above
 * is only available while the application is being initialized and will be removed
 * before the application is put into service.
 * </p>
 *
 * <p>
 * To register a custom AnnotationProvider with the runtime, place a file named
 * com.sun.faces.spi.annotationprovider within META-INF/services of a JAR file,
 * with a single line referencing the fully qualified class name of the AnnotationProvider
 * implementation.
 * </p>
 * 
 */
public abstract class AnnotationProvider {


    protected ServletContext sc;


    // ------------------------------------------------------------ Constructors


    public AnnotationProvider(ServletContext sc) {

        this.sc = sc;

    }


    // ---------------------------------------------------------- Public Methods


    /**
     * @return a <code>Map</code> of classes mapped to a specific annotation type.
     *  If no annotations are present, this method returns an empty <code>Map</code>.
     */
    public abstract Map<Class<? extends Annotation>,Set<Class<?>>> getAnnotatedClasses();

} // END AnnotationProvider
