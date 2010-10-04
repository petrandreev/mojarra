/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2010 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.faces.config;


import com.sun.faces.RIConstants;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.bean.ManagedBean;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
import javax.faces.event.ListenerFor;
import javax.faces.event.ListenersFor;
import javax.faces.render.FacesBehaviorRenderer;
import javax.faces.render.Renderer;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.MalformedURLException;

/**
 * Adds mappings <em>/faces</em>, <em>*.jsf</em>, and <em>*.faces</em> for the
 * FacesServlet (if it hasn't already been mapped) if the following conditions
 * are met:
 *
 * <ul>
 *    <li>
 *       The <code>Set</code> of classes passed to this initializer is not
 *       empty, or
 *    </li>
 *    <li>
 *       /WEB-INF/faces-config.xml exists.
 *    </li>
 * </ul>
 */
@SuppressWarnings({"UnusedDeclaration"})
@HandlesTypes({
      ManagedBean.class,
      FacesComponent.class,
      FacesValidator.class,
      FacesConverter.class,
      FacesBehaviorRenderer.class,
      ResourceDependency.class,
      ResourceDependencies.class,
      ListenerFor.class,
      ListenersFor.class,
      UIComponent.class,
      Validator.class,
      Converter.class,
      Renderer.class

})
public class FacesInitializer implements ServletContainerInitializer {

    // NOTE: Loggins should not be used with this class.

    private static final String FACES_SERVLET_CLASS = FacesServlet.class.getName();

    // -------------------------------- Methods from ServletContainerInitializer


    public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
          throws ServletException {

        if (shouldCheckMappings(classes, servletContext)) {

            Map<String,? extends ServletRegistration> existing = servletContext.getServletRegistrations();
            for (ServletRegistration registration : existing.values()) {
                if (FACES_SERVLET_CLASS.equals(registration.getClassName())) {
                    // FacesServlet has already been defined, so we're
                    // not going to add additional mappings;
                    return;
                }
            }
            ServletRegistration reg =
                  servletContext.addServlet("FacesServlet",
                                            "javax.faces.webapp.FacesServlet");
            reg.addMapping("/faces/*", "*.jsf", "*.faces");
            servletContext.setAttribute(RIConstants.FACES_INITIALIZER_MAPPINGS_ADDED, Boolean.TRUE);

            // The following line is temporary until we can solve an ordering
            // issue in V3.  Right now the JSP container looks for a mapping
            // of the FacesServlet in the web.xml.  If it's not present, then
            // it assumes that the application isn't a faces application.  In this
            // case the JSP container will not register the ConfigureListener
            // definition from our TLD nor will it parse cause or JSP TLDs to
            // be parsed.
            servletContext.addListener(com.sun.faces.config.ConfigureListener.class);

        }
    }


    // --------------------------------------------------------- Private Methods


    private boolean shouldCheckMappings(Set<Class<?>> classes,
                                        ServletContext context) {

        if (classes != null && !classes.isEmpty()) {
            return true;
        }

        // no JSF specific parameters found, check for a WEB-INF/faces-config.xml
        try {
            return (context.getResource("/WEB-INF/faces-config.xml") != null);
        } catch (MalformedURLException mue) {

        }
        
        return false;

    }
}
