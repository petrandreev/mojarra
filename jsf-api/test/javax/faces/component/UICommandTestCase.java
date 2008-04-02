/*
 * $Id: UICommandTestCase.java,v 1.14 2003/10/09 22:58:11 craigmcc Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.component;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.component.ValueHolder;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.PhaseId;
import javax.faces.TestUtil;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Unit tests for {@link UICommand}.</p>
 */

public class UICommandTestCase extends ValueHolderTestCaseBase {


    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public UICommandTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods


    // Set up instance variables required by this test case.
    public void setUp() {
        super.setUp();
        component = new UICommand();
        expectedRendererType = "Button";
    }

    
    // Return the tests included in this test case.
    public static Test suite() {
        return (new TestSuite(UICommandTestCase.class));
    }


    // Tear down instance variables required by ths test case
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------- Individual Test Methods


    // Test attribute-property transparency
    public void testAttributesTransparency() {

        super.testAttributesTransparency();
        UICommand command = (UICommand) component;

        assertEquals(command.getAction(),
                     (String) command.getAttributes().get("action"));
        command.setAction("foo");
        assertEquals("foo", (String) command.getAttributes().get("action"));
        command.setAction(null);
        assertNull((String) command.getAttributes().get("action"));
        command.getAttributes().put("action", "bar");
        assertEquals("bar", command.getAction());
        command.getAttributes().put("action", null);
        assertNull(command.getAction());

        assertEquals(command.getActionRef(),
                     (String) command.getAttributes().get("actionRef"));
        command.setActionRef("foo");
        assertEquals("foo", (String) command.getAttributes().get("actionRef"));
        command.setActionRef(null);
        assertNull((String) command.getAttributes().get("actionRef"));
        command.getAttributes().put("actionRef", "bar");
        assertEquals("bar", command.getActionRef());
        command.getAttributes().put("actionRef", null);
        assertNull(command.getActionRef());

    }


    // Test event queuing and broadcasting (any phase listeners)
    public void testEventsGeneric() {

        UICommand command = (UICommand) component;
        ActionEvent event = new ActionEvent(command);

        // Register three listeners
        command.addActionListener
            (new TestActionListener("AP0", PhaseId.ANY_PHASE));
        command.addActionListener
            (new TestActionListener("AP1", PhaseId.ANY_PHASE));
        command.addActionListener
            (new TestActionListener("AP2", PhaseId.ANY_PHASE));

        // Fire events and evaluate results
        TestActionListener.trace(null);
        assertTrue(command.broadcast(event, PhaseId.APPLY_REQUEST_VALUES));
        assertTrue(command.broadcast(event, PhaseId.PROCESS_VALIDATIONS));
        assertTrue(!command.broadcast(event, PhaseId.INVOKE_APPLICATION));
        assertEquals("/AP0/AP1/AP2",
                     TestActionListener.trace());

    }


    // Test event queuing and broadcasting (mixed phase listeners)
    public void testEventsMixed() {

        UICommand command = (UICommand) component;
        ActionEvent event = new ActionEvent(command);

        // Register three listeners
        command.addActionListener
            (new TestActionListener("ARV", PhaseId.APPLY_REQUEST_VALUES));
        command.addActionListener
            (new TestActionListener("PV", PhaseId.PROCESS_VALIDATIONS));
        command.addActionListener
            (new TestActionListener("AP", PhaseId.ANY_PHASE));

        // Fire events and evaluate results
        TestActionListener.trace(null);
        assertTrue(command.broadcast(event, PhaseId.APPLY_REQUEST_VALUES));
        assertTrue(command.broadcast(event, PhaseId.PROCESS_VALIDATIONS));
        assertTrue(!command.broadcast(event, PhaseId.INVOKE_APPLICATION));
        assertEquals("/AP/ARV/PV",
                     TestActionListener.trace());

    }


    // Test event queuing and broadcasting (specific phase listeners)
    public void testEventsSpecific() {

        UICommand command = (UICommand) component;
        ActionEvent event = new ActionEvent(command);

        // Register five listeners
        command.addActionListener
            (new TestActionListener("ARV0", PhaseId.APPLY_REQUEST_VALUES));
        command.addActionListener
            (new TestActionListener("ARV1", PhaseId.APPLY_REQUEST_VALUES));
        command.addActionListener
            (new TestActionListener("PV0", PhaseId.PROCESS_VALIDATIONS));
        command.addActionListener
            (new TestActionListener("PV1", PhaseId.PROCESS_VALIDATIONS));
        command.addActionListener
            (new TestActionListener("PV2", PhaseId.PROCESS_VALIDATIONS));

        // Fire events and evaluate results
        TestActionListener.trace(null);
        assertTrue(command.broadcast(event, PhaseId.RESTORE_VIEW));
        assertTrue(command.broadcast(event, PhaseId.APPLY_REQUEST_VALUES));
        assertTrue(command.broadcast(event, PhaseId.PROCESS_VALIDATIONS));
        assertTrue(command.broadcast(event, PhaseId.UPDATE_MODEL_VALUES));
        assertTrue(!command.broadcast(event, PhaseId.INVOKE_APPLICATION));
        assertEquals("/ARV0/ARV1/PV0/PV1/PV2",
                     TestActionListener.trace());

    }


    // Test listener registration and deregistration
    public void testListeners() {

        TestCommand command = new TestCommand();
        TestActionListener listener = null;
        List lists[] = null;

        command.addActionListener
            (new TestActionListener("ARV0", PhaseId.APPLY_REQUEST_VALUES));
        command.addActionListener
            (new TestActionListener("ARV1", PhaseId.APPLY_REQUEST_VALUES));
        command.addActionListener
            (new TestActionListener("PV0", PhaseId.PROCESS_VALIDATIONS));
        command.addActionListener
            (new TestActionListener("PV1", PhaseId.PROCESS_VALIDATIONS));
        command.addActionListener
            (new TestActionListener("PV2", PhaseId.PROCESS_VALIDATIONS));

        /* PENDING(craigmcc) - listeners are no longer accessible
        lists = command.getListeners();
        assertEquals(PhaseId.VALUES.size(), lists.length);
        for (int i = 0; i < lists.length; i++) {
            if (i == PhaseId.APPLY_REQUEST_VALUES.getOrdinal()) {
                assertEquals(2, lists[i].size());
                listener = (TestActionListener) lists[i].get(0);
                assertEquals("ARV0", listener.getId());
                listener = (TestActionListener) lists[i].get(1);
                assertEquals("ARV1", listener.getId());
            } else if (i == PhaseId.PROCESS_VALIDATIONS.getOrdinal()) {
                assertEquals(3, lists[i].size());
                listener = (TestActionListener) lists[i].get(0);
                assertEquals("PV0", listener.getId());
                listener = (TestActionListener) lists[i].get(1);
                assertEquals("PV1", listener.getId());
                listener = (TestActionListener) lists[i].get(2);
                assertEquals("PV2", listener.getId());
            } else {
                assertNull(lists[i]);
            }
        }

        command.removeActionListener
            ((ActionListener) lists[PhaseId.APPLY_REQUEST_VALUES.getOrdinal()].get(0));
        command.removeActionListener
            ((ActionListener) lists[PhaseId.PROCESS_VALIDATIONS.getOrdinal()].get(1));

        lists = command.getListeners();
        assertEquals(PhaseId.VALUES.size(), lists.length);
        for (int i = 0; i < lists.length; i++) {
            if (i == PhaseId.APPLY_REQUEST_VALUES.getOrdinal()) {
                assertEquals(1, lists[i].size());
                listener = (TestActionListener) lists[i].get(0);
                assertEquals("ARV1", listener.getId());
            } else if (i == PhaseId.PROCESS_VALIDATIONS.getOrdinal()) {
                assertEquals(2, lists[i].size());
                listener = (TestActionListener) lists[i].get(0);
                assertEquals("PV0", listener.getId());
                listener = (TestActionListener) lists[i].get(1);
                assertEquals("PV2", listener.getId());
            } else {
                assertNull(lists[i]);
            }
        }
        */

    }


    // Test a pristine UICommand instance
    public void testPristine() {

        super.testPristine();
        UICommand command = (UICommand) component;

        assertNull("no action", command.getAction());
        assertNull("no actionRef", command.getActionRef());

    }


    // Test setting properties to invalid values
    public void testPropertiesInvalid() throws Exception {

        super.testPropertiesInvalid();
        UICommand command = (UICommand) component;

    }


    // Test setting properties to valid values
    public void testPropertiesValid() throws Exception {

        super.testPropertiesValid();
        UICommand command = (UICommand) component;

        command.setAction("foo");
        assertEquals("foo", command.getAction());
        command.setAction(null);
        assertNull(command.getAction());

        command.setActionRef("foo");
        assertEquals("foo", command.getActionRef());
        command.setActionRef(null);
        assertNull(command.getActionRef());

    }


    public void testImmediate() throws Exception {
	List [] listeners = null;
	UICommandSub command = new UICommandSub();
	assertTrue(!command.isImmediate());

	// if there is a change in the immediate flag, from false to
	// true, the default action listener should be replaced with an
	// instance of WrapperActionListener.
	command.setImmediate(true);
	assertTrue(command.isImmediate());
	listeners = command.getListeners();
	// we should have one listener for APPLY_REQUEST_VALUES
	assertTrue(1 == 
		   ((List)listeners[PhaseId.APPLY_REQUEST_VALUES.getOrdinal()]).size());
	// we should have no listeners for INVOKE_APPLICATION
	assertTrue(0 == 
		   ((List)listeners[PhaseId.INVOKE_APPLICATION.getOrdinal()]).size());
	
	// if there is a change in the immediate flag, from true to
	// false, the default action listener should be restored.
	command.setImmediate(false);
	assertTrue(!command.isImmediate());
	listeners = command.getListeners();
	// we should have one listener for INVOKE_APPLICATION
	assertTrue(1 == 
		   ((List)listeners[PhaseId.INVOKE_APPLICATION.getOrdinal()]).size());
	// we should have no listeners for APPLY_REQUEST_VALUES
	assertTrue(0 == 
		   ((List)listeners[PhaseId.APPLY_REQUEST_VALUES.getOrdinal()]).size());

    }


    // --------------------------------------------------------- Support Methods


    // Check that the properties on the specified components are equal
    protected void checkProperties(UIComponent comp1, UIComponent comp2) {
        super.checkProperties(comp1, comp2);
        UICommand c1 = (UICommand) comp1;
        UICommand c2 = (UICommand) comp2;
        assertEquals(c1.getAction(), c2.getAction());
        assertEquals(c1.getActionRef(), c2.getActionRef());
    }


    // Create a pristine component of the type to be used in state holder tests
    protected UIComponent createComponent() {
        UIComponent component = new UICommand();
        component.setRendererType(null);
        return (component);
    }


    // Populate a pristine component to be used in state holder tests
    protected void populateComponent(UIComponent component) {
        super.populateComponent(component);
        UICommand c = (UICommand) component;
        c.setAction("foo");
        c.setActionRef("bar");
    }


    protected boolean listenersAreEqual(FacesContext context,
					UICommandSub comp1,
					UICommandSub comp2) {
	List [] list1 = comp1.getListeners();
	List [] list2 = comp2.getListeners();
	// make sure they're either both null or both non-null
	if ((null == list1 && null != list2) ||
	    (null != list1 && null == list2)) {
	    return false;
	}
	if (null == list1) {
	    return true;
	}
	int i = 0, j = 0, outerLen = list1.length, innerLen = 0;
	boolean result = true;
	if (outerLen != list2.length) {
	    return false;
	}
	for (i = 0; i < outerLen; i++) {
	    if ((null == list1[i] && null != list2[i]) ||
		(null != list1[i] && null == list2[i])) {
		return false;
	    }
	    else if (null != list1[i]) {
		if (list1[i].size() != (innerLen = list2[i].size())) {
		    return false;
		}
		for (j = 0; j < innerLen; j++) {
		    result = list1[i].get(j).equals(list2[i].get(j));
		    if (!result) {
			return false;
		    }
		}
	    }
	}
	return true;
    }

    public static class UICommandSub extends UICommand {
	public List[] getListeners() { 
	    return listeners;
	}
    }

}
