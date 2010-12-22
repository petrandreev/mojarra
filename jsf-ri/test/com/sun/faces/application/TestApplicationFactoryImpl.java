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

// TestApplicationFactoryImpl.java

package com.sun.faces.application;

import com.sun.faces.cactus.JspFacesTestCase;

import com.sun.faces.config.ConfigureListener;
import javax.faces.application.Application;

/**
 * <B>TestApplicationFactoryImpl</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 */

public class TestApplicationFactoryImpl extends JspFacesTestCase {

//
// Protected Constants
//

//
// Class Variables
//

//
// Instance Variables
//
    private ApplicationFactoryImpl applicationFactory = null;

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

    public TestApplicationFactoryImpl() {
        super("TestApplicationFactoryImpl");
    }


    public TestApplicationFactoryImpl(String name) {
        super(name);
    }
//
// Class methods
//

//
// General Methods
//

    public void testFactory() {
        applicationFactory = new ApplicationFactoryImpl();

        ApplicationAssociate.clearInstance(getFacesContext().getExternalContext());


        // 1. Verify "getApplication" returns the same Application instance
        //    if called multiple times.
        //  
        Application application1 = applicationFactory.getApplication();
        Application application2 = applicationFactory.getApplication();
        assertTrue(application1 == application2);

        // 2. Verify "setApplication" adds instances.. /
        //    and "getApplication" returns the same instance
        //
	ApplicationAssociate.clearInstance(getFacesContext().getExternalContext());
        Application application3 = new ApplicationImpl();
        applicationFactory.setApplication(application3);
        Application application4 = applicationFactory.getApplication();
        assertTrue(application3 == application4);
    }


    public void testSpecCompliance() {
        applicationFactory = new ApplicationFactoryImpl();
	ApplicationAssociate.clearInstance(getFacesContext().getExternalContext());

        assertTrue(null != applicationFactory.getApplication());
    }


    public void testExceptions() {
        applicationFactory = new ApplicationFactoryImpl();

        // 1. Verify NullPointer exception which occurs when attempting
        //    to add a null Application
        //
        boolean thrown = false;
        try {
            applicationFactory.setApplication(null);
        } catch (NullPointerException e) {
            thrown = true;
        }
        assertFalse(thrown);
    }


} // end of class TestApplicationFactoryImpl
