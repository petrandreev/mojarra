/*
 * $Id: TestApplicationImpl_Config.java,v 1.37 2005/08/22 22:11:09 ofung Exp $
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

// TestApplicationImpl_Config.java

package com.sun.faces.application;

import com.sun.faces.ServletFacesTestCase;
import com.sun.faces.TestComponent;
import com.sun.faces.TestConverter;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.LengthValidator;
import javax.faces.validator.Validator;

import java.util.Iterator;
import java.util.Locale;

/**
 * <B>TestApplicationImpl_Config</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: TestApplicationImpl_Config.java,v 1.37 2005/08/22 22:11:09 ofung Exp $
 */

public class TestApplicationImpl_Config extends ServletFacesTestCase {

//
// Protected Constants
//

    public static String standardComponentTypes[] = {
        "javax.faces.Column",
        "javax.faces.Command",
        "javax.faces.Data",
        "javax.faces.Form",
        "javax.faces.Graphic",
        "javax.faces.Input",
        "javax.faces.Message",
        "javax.faces.Messages",
        "javax.faces.NamingContainer",
        "javax.faces.Output",
        "javax.faces.Panel",
        "javax.faces.Parameter",
        "javax.faces.SelectBoolean",
        "javax.faces.SelectItem",
        "javax.faces.SelectItems",
        "javax.faces.SelectMany",
        "javax.faces.SelectOne",
        "javax.faces.ViewRoot",
        "javax.faces.HtmlCommandButton",
        "javax.faces.HtmlCommandLink",
        "javax.faces.HtmlDataTable",
        "javax.faces.HtmlForm",
        "javax.faces.HtmlGraphicImage",
        "javax.faces.HtmlInputHidden",
        "javax.faces.HtmlInputSecret",
        "javax.faces.HtmlInputText",
        "javax.faces.HtmlInputTextarea",
        "javax.faces.HtmlMessage",
        "javax.faces.HtmlMessages",
        "javax.faces.HtmlOutputFormat",
        "javax.faces.HtmlOutputLabel",
        "javax.faces.HtmlOutputLink",
        "javax.faces.HtmlOutputText",
        "javax.faces.HtmlPanelGrid",
        "javax.faces.HtmlPanelGroup",
        "javax.faces.HtmlSelectBooleanCheckbox",
        "javax.faces.HtmlSelectManyCheckbox",
        "javax.faces.HtmlSelectManyListbox",
        "javax.faces.HtmlSelectManyMenu",
        "javax.faces.HtmlSelectOneListbox",
        "javax.faces.HtmlSelectOneMenu",
        "javax.faces.HtmlSelectOneRadio"
    };

    public static Class standardComponentClasses[] = {
        javax.faces.component.UIColumn.class,
        javax.faces.component.UICommand.class,
        javax.faces.component.UIData.class,
        javax.faces.component.UIForm.class,
        javax.faces.component.UIGraphic.class,
        javax.faces.component.UIInput.class,
        javax.faces.component.UIMessage.class,
        javax.faces.component.UIMessages.class,
        javax.faces.component.UINamingContainer.class,
        javax.faces.component.UIOutput.class,
        javax.faces.component.UIPanel.class,
        javax.faces.component.UIParameter.class,
        javax.faces.component.UISelectBoolean.class,
        javax.faces.component.UISelectItem.class,
        javax.faces.component.UISelectItems.class,
        javax.faces.component.UISelectMany.class,
        javax.faces.component.UISelectOne.class,
        javax.faces.component.UIViewRoot.class,
        javax.faces.component.html.HtmlCommandButton.class,
        javax.faces.component.html.HtmlCommandLink.class,
        javax.faces.component.html.HtmlDataTable.class,
        javax.faces.component.html.HtmlForm.class,
        javax.faces.component.html.HtmlGraphicImage.class,
        javax.faces.component.html.HtmlInputHidden.class,
        javax.faces.component.html.HtmlInputSecret.class,
        javax.faces.component.html.HtmlInputText.class,
        javax.faces.component.html.HtmlInputTextarea.class,
        javax.faces.component.html.HtmlMessage.class,
        javax.faces.component.html.HtmlMessages.class,
        javax.faces.component.html.HtmlOutputFormat.class,
        javax.faces.component.html.HtmlOutputLabel.class,
        javax.faces.component.html.HtmlOutputLink.class,
        javax.faces.component.html.HtmlOutputText.class,
        javax.faces.component.html.HtmlPanelGrid.class,
        javax.faces.component.html.HtmlPanelGroup.class,
        javax.faces.component.html.HtmlSelectBooleanCheckbox.class,
        javax.faces.component.html.HtmlSelectManyCheckbox.class,
        javax.faces.component.html.HtmlSelectManyListbox.class,
        javax.faces.component.html.HtmlSelectManyMenu.class,
        javax.faces.component.html.HtmlSelectOneListbox.class,
        javax.faces.component.html.HtmlSelectOneMenu.class,
        javax.faces.component.html.HtmlSelectOneRadio.class
    };

    public static String standardConverterIds[] = {
        "javax.faces.BigDecimal",
        "javax.faces.BigInteger",
        "javax.faces.Boolean",
        "javax.faces.Byte",
        "javax.faces.Character",
        "javax.faces.DateTime",
        "javax.faces.Double",
        "javax.faces.Float",
        "javax.faces.Integer",
        "javax.faces.Long",
        "javax.faces.Number",
        "javax.faces.Short"
    };
    public static Class standardConverterClasses[] = {
        javax.faces.convert.BigDecimalConverter.class,
        javax.faces.convert.BigIntegerConverter.class,
        javax.faces.convert.BooleanConverter.class,
        javax.faces.convert.ByteConverter.class,
        javax.faces.convert.CharacterConverter.class,
        javax.faces.convert.DateTimeConverter.class,
        javax.faces.convert.DoubleConverter.class,
        javax.faces.convert.FloatConverter.class,
        javax.faces.convert.IntegerConverter.class,
        javax.faces.convert.LongConverter.class,
        javax.faces.convert.NumberConverter.class,
        javax.faces.convert.ShortConverter.class
    };

    public static Class standardConverterByIdClasses[] = {
        java.math.BigDecimal.class,
        java.math.BigInteger.class,
        java.lang.Boolean.class,
        java.lang.Byte.class,
        java.lang.Character.class,
        null,
        java.lang.Double.class,
        java.lang.Float.class,
        java.lang.Integer.class,
        java.lang.Long.class,
        null,
        java.lang.Short.class
    };

    public static Class standardConverterPrimitiveClasses[] = {
        null,
        null,
        java.lang.Boolean.TYPE,
        java.lang.Byte.TYPE,
        java.lang.Character.TYPE,
        null,
        java.lang.Double.TYPE,
        java.lang.Float.TYPE,
        java.lang.Integer.TYPE,
        java.lang.Long.TYPE,
        null,
        java.lang.Short.TYPE
    };


//
// Class Variables
//

//
// Instance Variables
//
    private ApplicationImpl application = null;

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

    public TestApplicationImpl_Config() {
        super("TestApplicationImpl_Config");
    }


    public TestApplicationImpl_Config(String name) {
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
        ApplicationFactory aFactory =
            (ApplicationFactory) FactoryFinder.getFactory(
                FactoryFinder.APPLICATION_FACTORY);
        application = (ApplicationImpl) aFactory.getApplication();
    }
	
    //****
    //**** NOTE: We should add a test for finding a faces-config.xml file under 
    //****       WEB-INF/classes/META-INF.
    //****

    //
    // Test Config related methods
    //

    public void testComponentPositive() {
        TestComponent
            newTestComponent = null,
            testComponent = new TestComponent();
        UIComponent uic = null;

        // runtime addition

        application.addComponent(testComponent.getComponentType(),
                                 "com.sun.faces.TestComponent");
        assertTrue(
            null !=
            (newTestComponent =
             (TestComponent)
            application.createComponent(testComponent.getComponentType())));
        assertTrue(newTestComponent != testComponent);

        // built-in components
        for (int i = 0, len = standardComponentTypes.length; i < len; i++) {
            assertTrue(null != (uic =
                                application.createComponent(
                                    standardComponentTypes[i])));
            assertTrue(
                standardComponentClasses[i].isAssignableFrom(uic.getClass()));
        }
    }


    public void testComponentNegative() {
        boolean exceptionThrown = false;

        // componentType/componentClass with non-existent class
        try {
            application.addComponent("William",
                                     "BillyBoy");
            application.createComponent("William");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // non-existent mapping
        exceptionThrown = false;
        try {
            application.createComponent("Joebob");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

    }


    public void testGetComponentTypes() {
        Iterator iter = application.getComponentTypes();
        assertTrue(null != iter);

        assertTrue(isSubset(standardComponentTypes, iter));
    }


    public void testConverterPositive() {
        TestConverter
            newTestConverter = null,
            testConverter = new TestConverter();
        Converter conv = null;

        // runtime addition

        application.addConverter(testConverter.getConverterId(),
                                 "com.sun.faces.TestConverter");
        assertTrue(
            null !=
            (newTestConverter =
             (TestConverter)
            application.createConverter(testConverter.getConverterId())));
        assertTrue(newTestConverter != testConverter);

        // built-in components

        // by-id
        for (int i = 0, len = standardConverterIds.length; i < len; i++) {
            assertTrue(null != (conv =
                                application.createConverter(
                                    standardConverterIds[i])));
            assertTrue(
                standardConverterClasses[i].isAssignableFrom(conv.getClass()));
        }

        // by-class
        for (int i = 0, len = standardConverterByIdClasses.length; i < len; i++) {
            // skip entries for which by-class registation doesn't make sense.
            if (null == standardConverterByIdClasses[i]) {
                continue;
            }
            assertTrue("null != " + standardConverterByIdClasses[i].toString(),
                       null !=
                       (conv =
                        application.createConverter(
                            standardConverterByIdClasses[i])));
            assertTrue(
                standardConverterClasses[i].isAssignableFrom(conv.getClass()));
        }

        // primitive classes
        for (int i = 0, len = standardConverterPrimitiveClasses.length; i <
            len; i++) {
            if (null == standardConverterPrimitiveClasses[i]) {
                continue;
            }

            assertTrue(
                "null != " + standardConverterPrimitiveClasses[i].toString(),
                null !=
                (conv =
                 application.createConverter(
                     standardConverterPrimitiveClasses[i])));
            assertTrue(
                standardConverterClasses[i].isAssignableFrom(conv.getClass()));
        }
    }


    public void testConverterNegative() {
        boolean exceptionThrown = false;

        // componentType/componentClass with non-existent class
        try {
            application.addConverter("William",
                                     "BillyBoy");
            application.createConverter("William");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // non-existent mapping
        exceptionThrown = false;
        try {
            application.createConverter("Joebob");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

    }


    public void testGetConverterIds() {
        Iterator iter = application.getConverterIds();
        assertTrue(null != iter);

        assertTrue(isSubset(standardConverterIds, iter));
    }


    public void testValidatorPositive() {
        Validator
            newTestValidator = null,
            testValidator = new LengthValidator();
        Validator val = null;

        // runtime addition

        application.addValidator("Billybob",
                                 "javax.faces.validator.LengthValidator");
        assertTrue(null != (newTestValidator =
            application.createValidator("Billybob")));
        assertTrue(newTestValidator != testValidator);

        // test standard components
        assertTrue(
            null !=
            (val = application.createValidator("javax.faces.DoubleRange")));
        assertTrue(val instanceof Validator);
        assertTrue(
            null != (val = application.createValidator("javax.faces.Length")));
        assertTrue(val instanceof Validator);
        assertTrue(
            null !=
            (val = application.createValidator("javax.faces.LongRange")));
        assertTrue(val instanceof Validator);

    }


    public void testValidatorNegative() {
        boolean exceptionThrown = false;

        // componentType/componentClass with non-existent class
        try {
            application.addValidator("William",
                                     "BillyBoy");
            application.createValidator("William");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        // non-existent mapping
        exceptionThrown = false;
        try {
            application.createValidator("Joebob");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

    }


    public void testGetValidatorIds() {
        Iterator iter = application.getValidatorIds();
        assertTrue(null != iter);
        String standardValidatorIds[] = {
            "javax.faces.DoubleRange",
            "javax.faces.Length",
            "javax.faces.LongRange"
        };

        assertTrue(isSubset(standardValidatorIds, iter));
    }


    public void testUpdateRuntimeComponents() {
        loadFromInitParam("/runtime-components.xml");
        ApplicationFactory aFactory =
            (ApplicationFactory) FactoryFinder.getFactory(
                FactoryFinder.APPLICATION_FACTORY);
        application = (ApplicationImpl) aFactory.getApplication();

        ActionListener actionListener = null;
        NavigationHandler navHandler = null;
        PropertyResolver propResolver = null;
        VariableResolver varResolver = null;
        ViewHandler viewHandler = null;
        StateManager stateManager = null;

        assertTrue(null != (actionListener =
                            application.getActionListener()));
        assertTrue(actionListener instanceof com.sun.faces.TestActionListener);

        assertTrue(null != (navHandler =
                            application.getNavigationHandler()));
        assertTrue(navHandler instanceof com.sun.faces.TestNavigationHandler);

        // JSF1.2 BI: application.getPropertyResolver() no longer returns the 
        // head of the PropertyResolver. Instead returns the head of the 
        // ELResolver stack wrapped in a PropertyResolver.This also applies to
        // VariableResolver
        assertTrue(null != (propResolver =
                            application.getPropertyResolver()));
        assertTrue(
            application.getPropertyResolver() instanceof javax.faces.el.PropertyResolver);
        assertTrue(null != (varResolver =
                            application.getVariableResolver()));
        assertTrue(varResolver instanceof javax.faces.el.VariableResolver);

        assertTrue(null != (viewHandler =
                            application.getViewHandler()));
        assertTrue(viewHandler instanceof javax.faces.application.ViewHandler);

        assertTrue(null != (stateManager =
                            application.getStateManager()));
        assertTrue(
            stateManager instanceof javax.faces.application.StateManager);
        System.out.println("DEFAULT:" + application.getDefaultRenderKitId());
        assertEquals("WackyRenderKit", application.getDefaultRenderKitId());
    }


    public void testLocaleConfigPositive() {
        loadFromInitParam("/locale-config.xml");
        ApplicationFactory aFactory =
            (ApplicationFactory) FactoryFinder.getFactory(
                FactoryFinder.APPLICATION_FACTORY);
        application = (ApplicationImpl) aFactory.getApplication();

        Locale locale;

        assertNotNull("Can't get default locale from Application",
                      locale = application.getDefaultLocale());
        assertEquals(Locale.US, locale);

        Iterator iter;
        int j = 0, len = 0;
        boolean found = false;
        String[][] expected = {
            {"de", "DE"},
            {"en", "US"},
            {"fr", "FR"},
            {"ps", "PS"}
        };
        len = expected.length;

        iter = application.getSupportedLocales();
        System.out.println("actual supported locales: ");
        while (iter.hasNext()) {
            System.out.println(iter.next().toString());
        }


        // test that the supported locales are a superset of the
        // expected locales
        for (j = 0; j < len; j++) {
            assertNotNull("Can't get supportedLocales from Application",
                          iter = application.getSupportedLocales());
            found = false;
            while (iter.hasNext()) {
                locale = (Locale) iter.next();
                if (expected[j][0].equals(locale.getLanguage()) &&
                    expected[j][1].equals(locale.getCountry())) {
                    found = true;
                }
            }
            assertTrue("Can't find expected locale " + expected[j][0] + "_" +
                       expected[j][1] + " in supported-locales list",
                       found);
        }

    }


    public void testLocaleConfigNegative2() {
        boolean exceptionThrown = false;
        try {
            loadFromInitParam("/locale-config2.xml");
        } catch (FacesException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

    }


} // end of class TestApplicationImpl_Config
