/*
 * $Id: TestProcessEvents.java,v 1.21 2005/10/19 19:51:35 edburns Exp $
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

// TestProcessEvents.java

package com.sun.faces.lifecycle;

import com.sun.faces.cactus.ServletFacesTestCase;
import com.sun.faces.util.Util;
import org.apache.cactus.WebRequest;

import javax.faces.component.UICommand;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import java.util.HashMap;

/**
 * <B>TestProcessEvents</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: TestProcessEvents.java,v 1.21 2005/10/19 19:51:35 edburns Exp $
 */

public class TestProcessEvents extends ServletFacesTestCase {

//
// Protected Constants
//

    public static final String HANDLED_VALUEEVENT1 = "handledValueEvent1";
    public static final String HANDLED_VALUEEVENT2 = "handledValueEvent2";
    public static final String HANDLED_ACTIONEVENT1 = "handledActionEvent1";

//
// Class Variables
//

//
// Instance Variables
//

// keeps track of total number of events processed
// per event source component

    public HashMap eventsProcessed = null;
    public String limit = null;
    public int eventLimit = 100; // some default;

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

    public TestProcessEvents() {
        super("TestProcessEvents");
    }


    public TestProcessEvents(String name) {
        super(name);
    }

//
// Class methods
//

//
// General Methods
//

    public void setUp() {
        super.setUp();
        UIViewRoot root = Util.getViewHandler(getFacesContext()).createView(getFacesContext(), null);
        getFacesContext().setViewRoot(root);
    }


    public void tearDown() {        
        super.tearDown();
    }

// tests one component - one value change listener

    public void testSingleValueChange() {
        // for keeping track of events processed limit..
        //
        eventsProcessed = new HashMap();

        UIInput userName = new UIInput();
        getFacesContext().getViewRoot().getChildren().add(userName);

        // clear the property
        System.setProperty(HANDLED_VALUEEVENT1, EMPTY);

        // add valueChangeListener to the component

        ValueChange1 valueChange1 = new ValueChange1();
        userName.addValueChangeListener(valueChange1);

        // add value change event (containing the component) to the queue

        userName.queueEvent(new ValueChangeEvent(userName, "foo", "bar"));

        getFacesContext().getViewRoot().processValidators(getFacesContext());

        assertTrue(!System.getProperty(HANDLED_VALUEEVENT1).equals(EMPTY));
    }

// tests one component - multiple value change listeners

    public void testMultipleValueChange() {
        // for keeping track of events processed limit..
        //
        eventsProcessed = new HashMap();

        UIInput userName = new UIInput();
        getFacesContext().getViewRoot().getChildren().add(userName);

        // clear the property
        System.setProperty(HANDLED_VALUEEVENT1, EMPTY);
        System.setProperty(HANDLED_VALUEEVENT2, EMPTY);

        // add valueChangeListener to the component

        ValueChange1 valueChange1 = new ValueChange1();
        ValueChange2 valueChange2 = new ValueChange2();
        userName.addValueChangeListener(valueChange1);
        userName.addValueChangeListener(valueChange2);

        // add value change event (containing the component) to the queue

        userName.queueEvent(new ValueChangeEvent(userName, "foo", "bar"));

        getFacesContext().getViewRoot().processValidators(getFacesContext());

        assertTrue(!System.getProperty(HANDLED_VALUEEVENT1).equals(EMPTY));
        assertTrue(!System.getProperty(HANDLED_VALUEEVENT2).equals(EMPTY));
    }


    /**
     * ********************
     * PENDING() perhaps reactivate this if the EG wants event loop detection.
     * <p/>
     * <p/>
     * // tests event recursion - infinite loop
     * // ValueChangeEvent will fire back the same event it received..
     * <p/>
     * public void testValueChangeRecursion()
     * {
     * // for keeping track of events processed limit..
     * //
     * eventsProcessed = new HashMap();
     * <p/>
     * UIInput userName = new UIInput();
     * <p/>
     * // add valueChangeListener to the component
     * <p/>
     * ValueChangeRecursion valueChange = new ValueChangeRecursion();
     * userName.addValueChangeListener(valueChange);
     * <p/>
     * // add value change event (containing the component) to the queue
     * <p/>
     * userName.queueEvent(new ValueChangeEvent(
     * userName, "foo", "bar"));
     * <p/>
     * PhaseId phaseId = PhaseId.PROCESS_VALIDATIONS;
     * boolean exceptionthrown = false;
     * try {
     * processEvents(getFacesContext(), phaseId);
     * } catch (Throwable e) {
     * System.out.println(e.getMessage());
     * exceptionthrown = true;
     * }
     * <p/>
     * assertTrue(exceptionthrown);
     * }
     * ************************
     */

// tests one component - one action listener

    public void beginSignleAction(WebRequest theRequest) {
        theRequest.addParameter("button1", "button1");
    }


    public void testSingleAction() {
        // for keeping track of events processed limit..
        //
        eventsProcessed = new HashMap();

        UICommand button = new UICommand();
        button.setId("button1");
        getFacesContext().getViewRoot().getChildren().add(button);

        // clear the property
        System.setProperty(HANDLED_ACTIONEVENT1, EMPTY);

        // add actionListener to the component

        Action1 action1 = new Action1();
        button.addActionListener(action1);
        button.setImmediate(true);

        // add action event (containing the component) to the queue

        button.queueEvent(new ActionEvent(button));

        getFacesContext().getViewRoot().processDecodes(getFacesContext());

        assertTrue(!System.getProperty(HANDLED_ACTIONEVENT1).equals(EMPTY));
    }


    /**
     * ***********************
     * PENDING() perhaps reactivate this if the EG wants event loop detection.
     * <p/>
     * // tests event recursion - infinite loop
     * // ActionEvent will fire back the same event it received..
     * <p/>
     * public void testActionRecursion()
     * {
     * // for keeping track of events processed limit..
     * //
     * eventsProcessed = new HashMap();
     * <p/>
     * UICommandSub button = new UICommandSub();
     * // make sure we have no listeners.
     * List[] listeners = button.getListeners();
     * for (int i = 0, len = listeners.length; i < len; i++) {
     * if (null != listeners[i]) {
     * listeners[i].clear();
     * }
     * }
     * <p/>
     * // add actionListener to the component
     * ActionRecursion action = new ActionRecursion();
     * button.addActionListener(action);
     * <p/>
     * // add action event (containing the component) to the queue
     * <p/>
     * button.queueEvent(new ActionEvent(button));
     * <p/>
     * PhaseId phaseId = PhaseId.APPLY_REQUEST_VALUES;
     * boolean exceptionthrown = false;
     * try {
     * processEvents(getFacesContext(), phaseId);
     * } catch (Throwable t) {
     * System.out.println("Action Exception:"+t.getMessage());
     * exceptionthrown = true;
     * }
     * <p/>
     * assertTrue(exceptionthrown);
     * }
     * <p/>
     * private boolean limitReached(UIComponent source, HashMap eventsProcessed) {
     * if (!eventsProcessed.containsKey(source)) {
     * eventsProcessed.put(source, new Integer(1));
     * return false;
     * }
     * <p/>
     * int count = ((Integer)eventsProcessed.get(source)).intValue()+1;
     * if (limit != null) {
     * eventLimit = new Integer(limit).intValue();
     * }
     * if (count > eventLimit) {
     * return true;
     * }
     * <p/>
     * eventsProcessed.put(source, new Integer(count));
     * return false;
     * }
     * ********************
     */

    public class ValueChange1 implements ValueChangeListener {

        public void processValueChange(ValueChangeEvent event) {
            System.setProperty(HANDLED_VALUEEVENT1, HANDLED_VALUEEVENT1);
        }
    }

    public class ValueChange2 implements ValueChangeListener {

        public void processValueChange(ValueChangeEvent event) {
            System.setProperty(HANDLED_VALUEEVENT2, HANDLED_VALUEEVENT2);
        }
    }


    /**
     * ***********
     * PENDING() perhaps reactivate this if the EG wants event loop detection.
     * <p/>
     * // event recursion case - fires same event it received..
     * <p/>
     * public class ValueChangeRecursion implements ValueChangeListener {
     * public void processValueChange(ValueChangeEvent event) {
     * getFacesContext().addFacesEvent(new ValueChangeEvent(
     * event.getComponent(), "foo", "bar"));
     * }
     * }
     * *************
     */

    public class Action1 implements ActionListener {

        public void processAction(ActionEvent event) {
            System.setProperty(HANDLED_ACTIONEVENT1, HANDLED_ACTIONEVENT1);
        }
    }


    /**************
     * PENDING() perhaps reactivate this if the EG wants event loop detection.

     // event recursion case - fires same event it received..

     public class ActionRecursion implements ActionListener {
     public void processAction(ActionEvent event) {
     getFacesContext().addFacesEvent(new ActionEvent(
     event.getComponent()));
     }
     }

     *****************/

} // end of class TestProcessEvents
