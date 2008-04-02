/*
 * $Id: LifecycleFactoryImpl.java,v 1.4 2006/03/29 22:39:22 rlubke Exp $
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

package com.sun.faces.systest;

import javax.faces.FacesException;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import java.util.Iterator;

public class LifecycleFactoryImpl extends LifecycleFactory {


    private Lifecycle newLifecycle = null;

    private LifecycleFactory previous = null;

    // ------------------------------------------------------------ Constructors


    public LifecycleFactoryImpl(LifecycleFactory previous) {

        this.previous = previous;
        try {
            newLifecycle =
                  new NewLifecycle("com.sun.faces.systest.NewLifecycle");
            this.previous.addLifecycle("com.sun.faces.systest.NewLifecycle",
                                       newLifecycle);
            newLifecycle =
                  new NewLifecycle("com.sun.faces.systest.AlternateLifecycle");
            this.previous
                  .addLifecycle("com.sun.faces.systest.AlternateLifecycle",
                                newLifecycle);
        }
        catch (Throwable e) {
            throw new FacesException(e);
        }

    }

    // ---------------------------------------------------------- Public Methods


    public void addLifecycle(String lifecycleId,
                             Lifecycle lifecycle) {

        previous.addLifecycle(lifecycleId, lifecycle);

    }


    public Lifecycle getLifecycle(String lifecycleId) {

        return previous.getLifecycle(lifecycleId);

    }


    public Iterator getLifecycleIds() {

        return previous.getLifecycleIds();

    }

}
