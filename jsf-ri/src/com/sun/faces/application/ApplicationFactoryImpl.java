/*
 * $Id: ApplicationFactoryImpl.java,v 1.12 2006/03/29 22:38:30 rlubke Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * https://javaserverfaces.dev.java.net/CDDL.html or
 * legal/CDDLv1.0.txt. 
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.    
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.faces.application;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;

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
    private static Logger logger = Util.getLogger(Util.FACES_LOGGER
                                                  + Util.APPLICATION_LOGGER);

    private Application application;

    // ------------------------------------------------------------ Constructors


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

    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Create (if needed) and return an {@link Application} instance
     * for this web application.</p>
     */
    public Application getApplication() {

        if (application == null) {
            application = new ApplicationImpl();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Created Application instance " + application);
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
    public void setApplication(Application application) {

        if (application == null) {
            String message = MessageUtils.getExceptionMessageString
                  (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID);
            message = message + " Application " + application;
            throw new NullPointerException(message);
        }

        this.application = application;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("set Application Instance to " + application);
        }

    }

}
