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

package com.sun.faces.application;

import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.FacesLogger;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.MessageFormat;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

/**
 * <p><strong>ApplicationFactory</strong> is a factory object that creates
 * (if needed) and returns {@link Application} instances.</p>
 * <p/>
 * <p>There must be one {@link ApplicationFactory} instance per web
 * application that is utilizing JavaServer Faces.  This instance can be
 * acquired, in a portable manner, by calling:</p>
 * <pre>
 *   ApplicationFactory factory = (ApplicationFactory)
 *    FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
 * </pre>
 */
public class ApplicationFactoryImpl extends ApplicationFactory {

   // Log instance for this class
    private static final Logger logger = FacesLogger.APPLICATION.getLogger();
    //
    // Protected Constants
    //

    //
    // Class Variables
    //

    // Attribute Instance Variables

    private volatile Application application;

    // Relationship Instance Variables

    //
    // Constructors and Initializers
    //


    /*
     * Constructor
     */
    public ApplicationFactoryImpl() {
        super();
        application = null;
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Created ApplicationFactory ");
        }
    }


    /**
     * <p>Create (if needed) and return an {@link Application} instance
     * for this web application.</p>
     */
    public Application getApplication() {

        if (application == null) {
            application = new ApplicationImpl();
            InjectionApplicationFactory.setApplicationInstance(application);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(MessageFormat.format("Created Application instance ''{0}''",
                                                 application));
            }
        }
        return application;
    }


    /**
     * <p>Replace the {@link Application} instance that will be
     * returned for this web application.</p>
     *
     * @param application The replacement {@link Application} instance
     */
    public synchronized void setApplication(Application application) {
        if (application == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "application");
            throw new NullPointerException(message);
        }

        this.application = application;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(MessageFormat.format("set Application Instance to ''{0}''", 
                                             application.getClass().getName()));
        }
    }
}
