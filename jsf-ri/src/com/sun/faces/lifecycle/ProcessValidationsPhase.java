/*
 * $Id: ProcessValidationsPhase.java,v 1.14 2003/08/19 19:31:10 rlubke Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// ProcessValidationsPhase.java

package com.sun.faces.lifecycle;

import org.mozilla.util.Assert;

import javax.faces.FacesException;
import javax.faces.event.PhaseId;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import java.util.Iterator;


/**

 * <B>Lifetime And Scope</B> <P> Same lifetime and scope as
 * DefaultLifecycleImpl.
 *
 * @version $Id: ProcessValidationsPhase.java,v 1.14 2003/08/19 19:31:10 rlubke Exp $
 * 
 */

public class ProcessValidationsPhase extends Phase {
//
// Protected Constants
//

//
// Class Variables
//

//
// Instance Variables
//

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Genericializers    
//

public ProcessValidationsPhase() {
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

public PhaseId getId() {
    return PhaseId.PROCESS_VALIDATIONS;
}

public void execute(FacesContext facesContext) throws FacesException
{
    Iterator messageIter = null;

    UIComponent component = facesContext.getRoot();
    Assert.assert_it(null != component);

    component.processValidators(facesContext);

    messageIter = facesContext.getMessages();
    Assert.assert_it(null != messageIter);

    if (messageIter.hasNext()) {
	// Proceed based on the number of errors present
        facesContext.renderResponse();
    }
}


// The testcase for this class is TestProcessValidationsPhase.java


} // end of class ProcessValidationsPhase
