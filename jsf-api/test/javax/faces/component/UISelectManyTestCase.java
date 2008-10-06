/*
 * $Id: UISelectManyTestCase.java,v 1.32 2008/01/08 22:29:24 rlubke Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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


import junit.framework.Test;
import junit.framework.TestSuite;

import javax.faces.application.FacesMessage;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;


/**
 * <p>Unit tests for {@link UISelectMany}.</p>
 */

public class UISelectManyTestCase extends UIInputTestCase {


    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public UISelectManyTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods


    // Set up instance variables required by this test case.
    public void setUp() {
        super.setUp();
        component = new UISelectMany();
        expectedFamily = UISelectMany.COMPONENT_FAMILY;
        expectedRendererType = "javax.faces.Listbox";
    }

    
    // Return the tests included in this test case.
    public static Test suite() {
        return (new TestSuite(UISelectManyTestCase.class));
    }


    // Tear down instance variables required by ths test case
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------- Individual Test Methods


    // Test the compareValues() method
    public void testCompareValues() {

        TestSelectMany selectMany = new TestSelectMany();
        Object values1a[] = new Object[] { "foo", "bar", "baz" };
        Object values1b[] = new Object[] { "foo", "baz", "bar" };
        Object values1c[] = new Object[] { "baz", "foo", "bar" };
        Object values2[] = new Object[] { "foo", "bar" };
        Object values3[] = new Object[] { "foo", "bar", "baz", "bop" };
        Object values4[] = null;

        assertTrue(!selectMany.compareValues(values1a, values1a));
        assertTrue(!selectMany.compareValues(values1a, values1b));
        assertTrue(!selectMany.compareValues(values1a, values1c));
        assertTrue(!selectMany.compareValues(values2, values2));
        assertTrue(!selectMany.compareValues(values3, values3));
        assertTrue(!selectMany.compareValues(values4, values4));

        assertTrue(selectMany.compareValues(values1a, values2));
        assertTrue(selectMany.compareValues(values1a, values3));
        assertTrue(selectMany.compareValues(values1a, values4));
        assertTrue(selectMany.compareValues(values2, values3));
        assertTrue(selectMany.compareValues(values2, values4));
        assertTrue(selectMany.compareValues(values4, values1a));
        assertTrue(selectMany.compareValues(values4, values2));
        assertTrue(selectMany.compareValues(values4, values3));

    }


    // Test a pristine UISelectMany instance
    public void testPristine() {

        super.testPristine();
        UISelectMany selectMany = (UISelectMany) component;

        assertNull("no selectedValues", selectMany.getSelectedValues());

    }


    // Test setting properties to invalid values
    public void testPropertiesInvalid() throws Exception {

        super.testPropertiesInvalid();
        UISelectMany selectMany = (UISelectMany) component;

    }


    // Test setting properties to valid values
    public void testPropertiesValid() throws Exception {

        super.testPropertiesValid();
        UISelectMany selectMany = (UISelectMany) component;

        Object values[] = new Object[] { "foo", "bar" };

        selectMany.setSelectedValues(values);
        assertEquals(values, selectMany.getSelectedValues());
        assertEquals(values, selectMany.getValue());
        selectMany.setSelectedValues(null);
        assertNull(selectMany.getSelectedValues());
        assertNull(selectMany.getValue());

        // Test transparency between "value" and "selectedValues" properties
        selectMany.setValue(values);
        assertEquals(values, selectMany.getSelectedValues());
        assertEquals(values, selectMany.getValue());
        selectMany.setValue(null);
        assertNull(selectMany.getSelectedValues());
        assertNull(selectMany.getValue());

        // Transparency applies to value bindings as well
        assertNull(selectMany.getValueBinding("selectedValues"));
        assertNull(selectMany.getValueBinding("value"));
        request.setAttribute("foo", new Object[] { "bar", "baz" });
        ValueBinding vb = application.createValueBinding("#{foo}");
        selectMany.setValueBinding("selectedValues", vb);
        assertTrue(vb == selectMany.getValueBinding("selectedValues"));
        assertTrue(vb == selectMany.getValueBinding("value"));
        selectMany.setValueBinding("selectedValues", null);
        assertNull(selectMany.getValueBinding("selectedValues"));
        assertNull(selectMany.getValueBinding("value"));
        selectMany.setValueBinding("value", vb);
        assertTrue(vb == selectMany.getValueBinding("selectedValues"));
        assertTrue(vb == selectMany.getValueBinding("value"));
        selectMany.setValueBinding("selectedValues", null);
        assertNull(selectMany.getValueBinding("selectedValues"));
        assertNull(selectMany.getValueBinding("value"));

    }



    // Test validation of value against the valid list
    public void testValidation() throws Exception {

        // Put our component under test in a tree under a UIViewRoot
        UIViewRoot root = facesContext.getApplication().getViewHandler().createView(facesContext, null);
        root.getChildren().add(component);

        // Add valid options to the component under test
        UISelectMany selectMany = (UISelectMany) component;
        selectMany.getChildren().add(new UISelectItemSub("foo", null, null));
        selectMany.getChildren().add(new UISelectItemSub("bar", null, null));
        selectMany.getChildren().add(new UISelectItemSub("baz", null, null));

        // Validate two values that are on the list
        selectMany.setValid(true);
        selectMany.setSubmittedValue(new Object[] { "foo", "baz" });
        selectMany.validate(facesContext);
        assertTrue(selectMany.isValid());

        // Validate one value on the list and one not on the list
        selectMany.getAttributes().put("label", "mylabel");
        selectMany.setValid(true);
        selectMany.setSubmittedValue(new Object[] { "bar", "bop"});
        selectMany.setRendererType(null); // We don't have any renderers
        selectMany.validate(facesContext);
        assertTrue(!selectMany.isValid());

        Iterator messages = facesContext.getMessages();
        while (messages.hasNext()) {
            FacesMessage message = (FacesMessage) messages.next();
            assertTrue(message.getSummary().indexOf("mylabel") >= 0);
        }


    }


    // Test validation of component with UISelectItems pointing to map
    public void testValidation2() throws Exception {

         // Put our component under test in a tree under a UIViewRoot
        UIViewRoot root = facesContext.getApplication().getViewHandler().createView(facesContext, null);
        root.getChildren().add(component);

        // Add valid options to the component under test
        Map map = new HashMap();
        map.put("key_foo", "foo");
        map.put("key_bar", "bar");
        map.put("key_baz", "baz");
        UISelectItems items = new UISelectItems();
        items.setValue(map);
        UISelectMany selectMany = (UISelectMany) component;
        selectMany.getChildren().add(items);

        // Validate two values that are on the list
        selectMany.setValid(true);
        selectMany.setSubmittedValue(new Object[] { "foo", "baz" });
        selectMany.validate(facesContext);
        assertTrue(selectMany.isValid());

        // Validate one value on the list and one not on the list
        selectMany.setValid(true);
        selectMany.setSubmittedValue(new Object[] { "bar", "bop"});
        selectMany.setRendererType(null); // We don't have any renderers
        selectMany.validate(facesContext);
        assertTrue(!selectMany.isValid());

    }


    // Test validation of component with UISelectItems pointing to Set and the
    // value of the component is Set
    public void testValidation3() throws Exception {

        Set<SelectItem> items = new HashSet<SelectItem>();
        items.add(new SelectItem("foo"));
        items.add(new SelectItem("bar"));
        items.add(new SelectItem("baz"));
        Set<String> submittedValues = new HashSet<String>();
        submittedValues.add("bar");
        submittedValues.add("baz");
        Set<String> invalidValues = new HashSet<String>();
        invalidValues.add("bar");
        invalidValues.add("car");
        testValidateWithCollection(items,
                                   submittedValues,
                                   invalidValues);

    }

    // Test validation of component with UISelectItems pointing to List
    public void testValidation4() throws Exception {

        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem("foo"));
        items.add(new SelectItem("bar"));
        items.add(new SelectItem("baz"));
        List<String> submittedValues = new ArrayList<String>();
        submittedValues.add("bar");
        submittedValues.add("baz");
        ArrayList<String> invalidValues = new ArrayList<String>();
        invalidValues.add("bar");
        invalidValues.add("car");
        testValidateWithCollection(items,
                                   submittedValues,
                                   invalidValues);

    }


    // Test validation of component with UISelectItems pointing to an Array
    public void testValidation5() throws Exception {

        // Put our component under test in a tree under a UIViewRoot
        UIViewRoot root = facesContext.getApplication().getViewHandler()
              .createView(facesContext, null);
        root.getChildren().add(component);

        // Add valid options to the component under test
        SelectItem[] itemsArray = {
              new SelectItem("foo"),
              new SelectItem("bar"),
              new SelectItem("baz")
        };
        UISelectItems items = new UISelectItems();
        items.setValue(itemsArray);
        UISelectMany selectMany = (UISelectMany) component;
        selectMany.getChildren().add(items);

        selectMany.setValid(true);
        selectMany.setSubmittedValue(new String[] { "bar", "baz" });
        selectMany.validate(facesContext);
        assertTrue(selectMany.isValid());

        // Validate one value on the list and one not on the list
        selectMany.setValid(true);
        selectMany.setSubmittedValue(new String[] { "bar", "car" });
        selectMany.setRendererType(null); // We don't have any renderers
        selectMany.validate(facesContext);
        assertTrue(!selectMany.isValid());

    }



    private void testValidateWithCollection(Collection<SelectItem> selectItems,
                                            Object validValues,
                                            Object invalidValues)
    throws Exception {

        UIViewRoot root = facesContext.getApplication().getViewHandler().createView(facesContext, null);
        root.getChildren().add(component);

        UISelectItems itemsComponent = new UISelectItems();
        itemsComponent.setValue(selectItems);
        UISelectMany selectMany = (UISelectMany) component;
        selectMany.setRendererType(null);
        selectMany.getChildren().add(itemsComponent);

        selectMany.setValue(true);
        selectMany.setSubmittedValue(validValues);
        selectMany.validate(facesContext);
        assertTrue(selectMany.isValid());
        selectMany.updateModel(facesContext);

        selectMany.setValid(true);
        selectMany.setSubmittedValue(invalidValues);
        selectMany.validate(facesContext);
        assertTrue(!selectMany.isValid());

    }


    private String legalValues[] =
    { "A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3" };


    private String illegalValues[] =
    { "D1", "D2", "Group A", "Group B", "Group C" };

    // Test validation against a nested list of available options
    public void testValidateNested() throws Exception {

        // Set up UISelectMany with nested UISelectItems
        UIViewRoot root = facesContext.getApplication().getViewHandler().createView(facesContext, null);
        root.getChildren().add(component);
        UISelectMany selectMany = (UISelectMany) component;
        UISelectItems selectItems = new UISelectItems();
        selectItems.setValue(setupOptions());
        selectMany.getChildren().add(selectItems);
        selectMany.setRequired(true);
        checkMessages(0);

        // Verify that all legal values will validate
        for (int i = 0; i < legalValues.length; i++) {
            selectMany.setValid(true);
            selectMany.setSubmittedValue
                (new Object[] { legalValues[0], legalValues[i] });
            selectMany.validate(facesContext);
            assertTrue("Value '" + legalValues[i] + "' found",
                       selectMany.isValid());
            checkMessages(0);
        }

        // Verify that illegal values will not validate
        for (int i = 0; i < illegalValues.length; i++) {
            selectMany.setValid(true);
            selectMany.setSubmittedValue
                (new Object[] { legalValues[0], illegalValues[i] });
            selectMany.validate(facesContext);
            assertTrue("Value '" + illegalValues[i] + "' not found",
                       !selectMany.isValid());
            checkMessages(i + 1);
        }


    }

    // Test validation against a nested Set of available options
    public void testValidateNestedSet() throws Exception {

        // Set up UISelectMany with nested UISelectItems
        UIViewRoot root = facesContext.getApplication().getViewHandler().createView(facesContext, null);
        root.getChildren().add(component);
        UISelectMany selectMany = (UISelectMany) component;
        UISelectItems selectItems = new UISelectItems();
        selectItems.setValue(setupOptionsSet());
        selectMany.getChildren().add(selectItems);
        selectMany.setRequired(true);
        checkMessages(0);

        // Verify that all legal values will validate
        for (int i = 0; i < legalValues.length; i++) {
            selectMany.setValid(true);
            selectMany.setSubmittedValue
                (new Object[] { legalValues[0], legalValues[i] });
            selectMany.validate(facesContext);
            assertTrue("Value '" + legalValues[i] + "' found",
                       selectMany.isValid());
            checkMessages(0);
        }

        // Verify that illegal values will not validate
        for (int i = 0; i < illegalValues.length; i++) {
            selectMany.setValid(true);
            selectMany.setSubmittedValue
                (new Object[] { legalValues[0], illegalValues[i] });
            selectMany.validate(facesContext);
            assertTrue("Value '" + illegalValues[i] + "' not found",
                       !selectMany.isValid());
            checkMessages(i + 1);
        }


    }


    // Test validation of a required field
    public void testValidateRequired() throws Exception {

        UIViewRoot root = facesContext.getApplication().getViewHandler().createView(facesContext, null);
        root.getChildren().add(component);
        UISelectMany selectMany = (UISelectMany) component;
        selectMany.getChildren().add(new UISelectItemSub("foo", null, null));
        selectMany.getChildren().add(new UISelectItemSub("bar", null, null));
        selectMany.getChildren().add(new UISelectItemSub("baz", null, null));
        selectMany.setRequired(true);
        checkMessages(0);

        selectMany.setValid(true);
        selectMany.setSubmittedValue(new Object[] { "foo" });
        selectMany.validate(facesContext);
        checkMessages(0);
        assertTrue(selectMany.isValid());

        selectMany.setValid(true);
        selectMany.setSubmittedValue(new Object[] { "" });
        selectMany.validate(facesContext);
        checkMessages(1);
        assertTrue(!selectMany.isValid());

        selectMany.setValid(true);
        selectMany.setSubmittedValue(null);
	// this execution of validate shouldn't add any messages to the
	// queue, since a value of null means "don't validate".  This is
	// different behavior than in previous versions of this
	// testcase, which expected the UISelectMany.validate() to
	// operate on the previously validated value, which is not
	// correct.
        selectMany.validate(facesContext);
        checkMessages(1);
	// since we're setting the submitted value to null, we don't
	// want validation to occurr, therefore, the valid state of the
	// componet should be as we left it.
        assertTrue(selectMany.isValid());

    }


    // Test that appropriate properties are value binding enabled
    public void testValueBindings() {

	super.testValueBindings();
	UISelectMany test = (UISelectMany) component;

	// "value" property
	request.setAttribute("foo", "bar");
	test.setValue(null);
	assertNull(test.getValue());
	test.setValueBinding("value", application.createValueBinding("#{foo}"));
	assertNotNull(test.getValueBinding("value"));
	assertEquals("bar", test.getValue());
	test.setValue("baz");
	assertEquals("baz", test.getValue());
	test.setValue(null);
	assertEquals("bar", test.getValue());
	test.setValueBinding("value", null);
	assertNull(test.getValueBinding("value"));
	assertNull(test.getValue());

    }

    public void testSelectItemsIterator() {
        // sub test 1: non-selectitem at end 
        UISelectMany selectMany = (UISelectMany) component;
        selectMany.getChildren().add(new UISelectItemSub("orr", null, null));
        UIParameter param = new UIParameter();
        param.setName("param");
        param.setValue("paramValue");
        selectMany.getChildren().add(param);
        selectMany.getChildren().add(new UISelectItemSub("esposito", null, null));
        Iterator iter = new SelectItemsIterator(selectMany);
        while (iter.hasNext()) {
            Object object = iter.next();
            assertTrue(object instanceof javax.faces.model.SelectItem);
            assertTrue((((SelectItem)object).getValue().equals("orr")) ||
                (((SelectItem)object).getValue().equals("esposito")));
        }

        // sub test 2: non-selectitem in middle
        selectMany = new UISelectMany();
        selectMany.getChildren().add(new UISelectItemSub("gretsky", null, null));
        selectMany.getChildren().add(param);
        selectMany.getChildren().add(new UISelectItemSub("howe", null, null));
        iter = new SelectItemsIterator(selectMany);
        while (iter.hasNext()) {
            Object object = iter.next();
            assertTrue(object instanceof javax.faces.model.SelectItem);
            assertTrue((((SelectItem)object).getValue().equals("gretsky")) ||
                (((SelectItem)object).getValue().equals("howe")));
        }

    }


    // --------------------------------------------------------- Support Methods


    // Create a pristine component of the type to be used in state holder tests
    protected UIComponent createComponent() {
        UIComponent component = new UISelectMany();
        component.setRendererType(null);
        return (component);
    }


    protected void setupNewValue(UIInput input) {

        input.setSubmittedValue(new Object[] { "foo" });
        UISelectItem si = new UISelectItem();
        si.setItemValue("foo");
        si.setItemLabel("foo label");
        input.getChildren().add(si);

    }


    // Create an options list with nested groups
    protected List setupOptions() {
        SelectItemGroup group, subgroup;
        subgroup = new SelectItemGroup("Group C");
        subgroup.setSelectItems(new SelectItem[]
            { new SelectItem("C1"),
              new SelectItem("C2"),
              new SelectItem("C3") });
        List options = new ArrayList();
        options.add(new SelectItem("A1"));
        group = new SelectItemGroup("Group B");
        group.setSelectItems(new SelectItem[]
            { new SelectItem("B1"),
              subgroup,
              new SelectItem("B2"),
              new SelectItem("B3") });
        options.add(group);
        options.add(new SelectItem("A2"));
        options.add(new SelectItem("A3"));
        return (options);
    }

    // Create an options list with nested groups
    protected Set setupOptionsSet() {
        SelectItemGroup group, subgroup;
        subgroup = new SelectItemGroup("Group C");
        subgroup.setSelectItems(new SelectItem[]
            { new SelectItem("C1"),
              new SelectItem("C2"),
              new SelectItem("C3") });
        Set options = new HashSet();
        options.add(new SelectItem("A1"));
        group = new SelectItemGroup("Group B");
        group.setSelectItems(new SelectItem[]
            { new SelectItem("B1"),
              subgroup,
              new SelectItem("B2"),
              new SelectItem("B3") });
        options.add(group);
        options.add(new SelectItem("A2"));
        options.add(new SelectItem("A3"));
        return (options);
    }



}
