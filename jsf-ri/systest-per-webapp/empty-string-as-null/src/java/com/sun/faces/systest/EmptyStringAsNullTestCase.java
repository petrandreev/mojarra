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

package com.sun.faces.systest;

import java.net.URL;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.sun.faces.htmlunit.AbstractTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.List;

public class EmptyStringAsNullTestCase extends AbstractTestCase {

    public EmptyStringAsNullTestCase(String name) {
        super(name);
    }

    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(EmptyStringAsNullTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        super.tearDown();
    }
    
    
    // ------------------------------------------------------------ Test Methods
    
    public void testWriteAttributeDisabled() throws Exception {

        HtmlPage page = getPage("/faces/test.xhtml");

        HtmlTextInput stringInput = (HtmlTextInput) getInputContainingGivenId(page, "string");
        HtmlTextInput integerInput = (HtmlTextInput) getInputContainingGivenId(page, "integer");

        assertNotNull(stringInput);
        assertNotNull(integerInput);
        assertEquals(stringInput.getValueAttribute(), "");
        assertEquals(integerInput.getValueAttribute(), "");

        String pageAsText = page.asText();
        assertTrue(pageAsText.contains("VC1 Fired:false"));
        assertTrue(pageAsText.contains("VC2 Fired:false"));
        assertTrue(pageAsText.contains("String model set with null: false"));
        assertTrue(pageAsText.contains("Integer model set with null: false"));

        HtmlSubmitInput submit = (HtmlSubmitInput) getInputContainingGivenId(page, "command");
        assertNotNull(submit);

        stringInput.setValueAttribute("11");
        integerInput.setValueAttribute("11");

        page = (HtmlPage) submit.click();

        stringInput = (HtmlTextInput) getInputContainingGivenId(page, "string");
        integerInput = (HtmlTextInput) getInputContainingGivenId(page, "integer");

        assertNotNull(stringInput);
        assertNotNull(integerInput);
        assertEquals(stringInput.getValueAttribute(), "11");
        assertEquals(integerInput.getValueAttribute(), "11");

        pageAsText = page.asText();
        assertTrue(pageAsText.contains("VC1 Fired:true"));
        assertTrue(pageAsText.contains("VC2 Fired:true"));
        assertTrue(pageAsText.contains("String model set with null: false"));
        assertTrue(pageAsText.contains("Integer model set with null: false"));

        submit = (HtmlSubmitInput) getInputContainingGivenId(page, "command");
        assertNotNull(submit);

        stringInput.setValueAttribute("");
        integerInput.setValueAttribute("");

        page = (HtmlPage) submit.click();

        stringInput = (HtmlTextInput) getInputContainingGivenId(page, "string");
        integerInput = (HtmlTextInput) getInputContainingGivenId(page, "integer");

        assertNotNull(stringInput);
        assertNotNull(integerInput);
        assertEquals(stringInput.getValueAttribute(), "");
        assertEquals(integerInput.getValueAttribute(), "");

        pageAsText = page.asText();
        assertTrue(pageAsText.contains("VC1 Fired:true"));
        assertTrue(pageAsText.contains("VC2 Fired:true"));
        assertTrue(pageAsText.contains("String model set with null: true"));
        assertTrue(pageAsText.contains("Integer model set with null: true"));

        submit = (HtmlSubmitInput) getInputContainingGivenId(page, "command");
        assertNotNull(submit);

        stringInput.setValueAttribute("");
        integerInput.setValueAttribute("");

        page = (HtmlPage) submit.click();

        stringInput = (HtmlTextInput) getInputContainingGivenId(page, "string");
        integerInput = (HtmlTextInput) getInputContainingGivenId(page, "integer");

        assertNotNull(stringInput);
        assertNotNull(integerInput);
        assertEquals(stringInput.getValueAttribute(), "");
        assertEquals(integerInput.getValueAttribute(), "");

        pageAsText = page.asText();
        assertTrue(pageAsText.contains("VC1 Fired:false"));
        assertTrue(pageAsText.contains("VC2 Fired:false"));
        assertTrue(pageAsText.contains("String model set with null: true"));
        assertTrue(pageAsText.contains("Integer model set with null: true"));
        
    }
}
