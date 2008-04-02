/*
 * $Id: InvokeApplicationPhase.java,v 1.13 2004/02/04 23:41:37 ofung Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// InvokeApplicationPhase.java

package com.sun.faces.lifecycle;

import com.sun.faces.util.Util;


import javax.faces.FacesException;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import javax.faces.event.FacesEvent;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**

 * <B>Lifetime And Scope</B> <P> Same lifetime and scope as
 * DefaultLifecycleImpl.
 *
 * @version $Id: InvokeApplicationPhase.java,v 1.13 2004/02/04 23:41:37 ofung Exp $
 * 
 */

public class InvokeApplicationPhase extends Phase {
//
// Protected Constants
//

//
// Class Variables
//

    // Log instance for this class
    protected static Log log = LogFactory.getLog(InvokeApplicationPhase.class);

//
// Instance Variables
//

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Genericializers    
//

public InvokeApplicationPhase() {
    super();
}

public PhaseId getId() {
    return PhaseId.INVOKE_APPLICATION;
}

public void execute(FacesContext facesContext) throws FacesException {
    
    if (log.isDebugEnabled()) {
        log.debug("Entering InvokeApplicationsPhase");
    }
    
    UIViewRoot root = facesContext.getViewRoot();
    Util.doAssert(null != root);
    
    try {
	root.processApplication(facesContext);
    } catch (RuntimeException re) {
	String exceptionMessage = re.getMessage();
	if (null != exceptionMessage) {
	    if (log.isErrorEnabled()) {
		log.error(exceptionMessage, re);
	    }
	}
	throw new FacesException(exceptionMessage, re);
    }
    
     if (log.isDebugEnabled()) {
         log.debug("Exiting InvokeApplicationsPhase");
     }
}

//
// Class methods
//

//
// General Methods
//

//
// Methods from Phase
//


// The testcase for this class is TestInvokeApplicationPhase.java


} // end of class InvokeApplicationPhase
