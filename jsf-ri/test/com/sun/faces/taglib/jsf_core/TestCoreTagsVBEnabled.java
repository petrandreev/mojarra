/*
 * $Id: TestCoreTagsVBEnabled.java,v 1.5 2004/02/26 20:34:43 eburns Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.taglib.jsf_core;

import com.sun.faces.JspFacesTestCase;
import com.sun.faces.RIConstants;
import com.sun.faces.lifecycle.ApplyRequestValuesPhase;
import com.sun.faces.lifecycle.Phase;
import com.sun.faces.lifecycle.ProcessValidationsPhase;
import com.sun.faces.lifecycle.RenderResponsePhase;
import org.apache.cactus.WebRequest;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import java.util.Iterator;

/**
 * <B>TestValidatorTags</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: TestCoreTagsVBEnabled.java,v 1.5 2004/02/26 20:34:43 eburns Exp $
 */

public class TestCoreTagsVBEnabled extends JspFacesTestCase {

//
// Protected Constants
//

    public static final String TEST_URI = "/TestCoreTagVBEnabled.jsp";
    public static final String LONGRANGE_ID = "validatorForm" +
        NamingContainer.SEPARATOR_CHAR +
        "longRange";
    public static final String LONGRANGE_VALUE = "115";
    public static final String INTRANGE_ID = "validatorForm" +
        NamingContainer.SEPARATOR_CHAR +
        "intRange";
    public static final String INTRANGE_VALUE = "NorthAmerica";
    public static final String DOUBLERANGE_ID = "validatorForm" +
        NamingContainer.SEPARATOR_CHAR +
        "doubleRange";
    public static final String DOUBLERANGE_VALUE = "1500";


    public boolean sendResponseToFile() {
        return false;
    }

//
// Class Variables
//

//
// Instance Variables
//

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

    public TestCoreTagsVBEnabled() {
        super("TestValidatorTags");
    }


    public TestCoreTagsVBEnabled(String name) {
        super(name);
    }

//
// Class methods
//

//
// General Methods
//


    public void beginValidators(WebRequest theRequest) {
        theRequest.setURL("localhost:8080", "/test", "/faces", TEST_URI, null);
        theRequest.addParameter(LONGRANGE_ID, LONGRANGE_VALUE);
        theRequest.addParameter(INTRANGE_ID, INTRANGE_VALUE);
        theRequest.addParameter(DOUBLERANGE_ID, DOUBLERANGE_VALUE);
        theRequest.addParameter("validatorForm", "validatorForm");

    }


    public void setUp() {

        RIConstants.IS_UNIT_TEST_MODE = true;
        super.setUp();
        (getFacesContext().getExternalContext().getRequestMap()).put("intMin",
                                                                     new Integer(
                                                                         1));
        (getFacesContext().getExternalContext().getRequestMap()).put("intMax",
                                                                     new Integer(
                                                                         10));
        (getFacesContext().getExternalContext().getRequestMap()).put("longMin",
                                                                     new Long(
                                                                         100));
        (getFacesContext().getExternalContext().getRequestMap()).put("longMax",
                                                                     new Long(
                                                                         110));
        (getFacesContext().getExternalContext().getRequestMap()).put(
            "doubleMin", new Double(1.0));
        (getFacesContext().getExternalContext().getRequestMap()).put(
            "doubleMax", new Double(10.0));
    }


    public void testValidators() {
        System.out.println("Test VBEnabled attributes on core Validator tags");
        // Verify the parmeters are as expected
        String paramVal = (String) (getFacesContext().getExternalContext()
            .getRequestParameterMap()).get(LONGRANGE_ID);
        assertTrue(LONGRANGE_VALUE.equals(paramVal));

        String paramVal1 = (String) (getFacesContext().getExternalContext()
            .getRequestParameterMap()).get(DOUBLERANGE_ID);
        assertTrue(DOUBLERANGE_VALUE.equals(paramVal1));

        String paramVal2 = (String) (getFacesContext().getExternalContext()
            .getRequestParameterMap()).get(INTRANGE_ID);
        assertTrue(INTRANGE_VALUE.equals(paramVal2));

        boolean result = false;
        String value = null;
        Phase
            renderResponse = new RenderResponsePhase(),
            processValidations = new ProcessValidationsPhase(),
            applyRequestValues = new ApplyRequestValuesPhase();

        UIViewRoot page = new UIViewRoot();
        page.setViewId(TEST_URI);
        getFacesContext().setViewRoot(page);

        // This builds the tree, and usefaces saves it in the session
        renderResponse.execute(getFacesContext());
        assertTrue(!(getFacesContext().getRenderResponse()) &&
                   !(getFacesContext().getResponseComplete()));

        // This causes the components to be set to valid
        applyRequestValues.execute(getFacesContext());
        assertTrue(!(getFacesContext().getRenderResponse()) &&
                   !(getFacesContext().getResponseComplete()));

        // process the validations
        processValidations.execute(getFacesContext());
        // We know there are validation errors on the page
        assertTrue(getFacesContext().getRenderResponse());

        System.out.println("Verifying results...");
        // verify the messages have been added correctly.
        UIComponent comp = null;
        Iterator messages = null;

        assertTrue(null != (messages = getFacesContext().getMessages()));
        assertTrue(messages.hasNext());

        // check the messages for each component in the page
        assertTrue(null !=
                   (comp =
                    getFacesContext().getViewRoot().findComponent(LONGRANGE_ID)));
        assertTrue(
            null !=
            (messages =
             getFacesContext().getMessages(comp.getClientId(getFacesContext()))));
        assertTrue(messages.hasNext());

        assertTrue(null !=
                   (comp =
                    getFacesContext().getViewRoot().findComponent(INTRANGE_ID)));
        assertTrue(
            null !=
            (messages =
             getFacesContext().getMessages(comp.getClientId(getFacesContext()))));
        assertTrue(messages.hasNext());

        assertTrue(null !=
                   (comp =
                    getFacesContext().getViewRoot().findComponent(
                        DOUBLERANGE_ID)));
        assertTrue(
            null !=
            (messages =
             getFacesContext().getMessages(comp.getClientId(getFacesContext()))));
        assertTrue(messages.hasNext());


        RIConstants.IS_UNIT_TEST_MODE = false;
    }


} // end of class TestValidatorTags
