/*
 * $Id: UISelectOneBaseTestCase.java,v 1.5 2003/09/19 00:57:17 craigmcc Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.component.base;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.component.ValueHolder;
import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Unit tests for {@link UISelectOneBase}.</p>
 */

public class UISelectOneBaseTestCase extends UIInputBaseTestCase {


    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public UISelectOneBaseTestCase(String name) {
        super(name);
    }


    // ---------------------------------------------------- Overall Test Methods


    // Set up instance variables required by this test case.
    public void setUp() {
        super.setUp();
        component = new UISelectOneBase();
        expectedRendererType = "Menu";
    }

    
    // Return the tests included in this test case.
    public static Test suite() {
        return (new TestSuite(UISelectOneBaseTestCase.class));
    }


    // Tear down instance variables required by ths test case
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------- Individual Test Methods


    // Test a pristine UISelectOneBase instance
    public void testPristine() {

        super.testPristine();
        UISelectOne selectOne = (UISelectOne) component;

    }


    // Test setting properties to invalid values
    public void testPropertiesInvalid() throws Exception {

        super.testPropertiesInvalid();
        UISelectOne selectOne = (UISelectOne) component;

    }


    // Test setting properties to valid values
    public void testPropertiesValid() throws Exception {

        super.testPropertiesValid();
        UISelectOne selectOne = (UISelectOne) component;

    }


    // Test validation of value against the valid list
    public void testValidation() throws Exception {

        // Add valid options to the component under test
        UISelectOne selectOne = (UISelectOne) component;
        selectOne.getChildren().add(new UISelectItemSub("foo", null, null));
        selectOne.getChildren().add(new UISelectItemSub("bar", null, null));
        selectOne.getChildren().add(new UISelectItemSub("baz", null, null));

        // Validate a value that is on the list
        selectOne.setValid(true);
        selectOne.setValue("bar");
        selectOne.validate(facesContext);
        assertTrue(selectOne.isValid());

        // Validate a value that is not on the list
        selectOne.setValid(true);
        selectOne.setValue("bop");
        selectOne.validate(facesContext);
        assertTrue(!selectOne.isValid());

    }


    protected ValueHolder createValueHolder() {

        UIComponent component = new UISelectOneBase();
        component.setRendererType(null);
        return ((ValueHolder) component);

    }



}
