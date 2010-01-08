/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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

package javax.faces.component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import javax.faces.convert.IntegerConverter;


public class StateHolderSaverTestCase extends UIComponentBaseTestCase {


    // ------------------------------------------------------ Instance Variables

    // ------------------------------------------------------------ Constructors


    // Construct a new instance of this test case.
    public StateHolderSaverTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods

    // Return the tests included in this test case.
    public static Test suite() {

        return (new TestSuite(StateHolderSaverTestCase.class));

    }

    // ------------------------------------------------- Individual Test Methods

    public void testLifecycleManagement() { }


    public void testChildrenRecursive() {}


    public void testComponentReconnect() {}


    public void testComponentRemoval() {}


    public void testStateHolder() throws Exception {}


    public void testValueBindings() {}


    public void testImplementsStateHolder() throws Exception {
	StateHolderSaver saver = null;
	UIInput 
	    postSave,
	    preSave = new UIInput();
	preSave.setId("id1");
	preSave.setRendererType(null);
	
	saver = new StateHolderSaver(facesContext, preSave);
	postSave = (UIInput) saver.restore(facesContext);
	assertEquals(postSave.getId(), preSave.getId());
    }

    public void testImplementsSerializable() throws Exception {
	StateHolderSaver saver = null;
	String 
	    preSave = "hello",
	    postSave = null;

	saver = new StateHolderSaver(facesContext, preSave);
	postSave = (String) saver.restore(facesContext);
	assertTrue(preSave.equals(postSave));
    }

    public void testImplementsNeither() throws Exception {
	StateHolderSaver saver = null;
	IntegerConverter  
	    preSave = new IntegerConverter(),
	    postSave = null;

	saver = new StateHolderSaver(facesContext, preSave);
	postSave = (IntegerConverter) saver.restore(facesContext);
	assertTrue(true); // lack of ClassCastException
    }


}
