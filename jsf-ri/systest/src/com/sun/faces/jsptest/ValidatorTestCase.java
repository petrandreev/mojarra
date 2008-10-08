/*
 * $Id: ValidatorTestCase.java,v 1.11 2007/04/27 22:01:09 ofung Exp $
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

package com.sun.faces.jsptest;

import java.util.List;

import com.sun.faces.htmlunit.AbstractTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>Test that invalid values don't cause valueChangeEvents to occur.</p>
 */

public class ValidatorTestCase extends AbstractTestCase {

    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ValidatorTestCase(String name) {
        super(name);
    }

    // ------------------------------------------------------ Instance Variables

    // ---------------------------------------------------- Overall Test Methods


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(ValidatorTestCase.class));
    }

    // ------------------------------------------------- Individual Test Methods
    public void testValidator() throws Exception {
        HtmlPage page = getPage("/faces/validator02.jsp");
        List list;
        list = getAllElementsOfGivenClass(page, null,
                HtmlTextInput.class);

        // set the initial value to be 1 for all input fields
        ((HtmlTextInput) list.get(0)).setValueAttribute("1111111111");
        ((HtmlTextInput) list.get(1)).setValueAttribute("1111111111");
        ((HtmlTextInput) list.get(2)).setValueAttribute("1111111111");
        ((HtmlTextInput) list.get(3)).setValueAttribute("1111111111");
        ((HtmlTextInput) list.get(4)).setValueAttribute("1111111111");
        ((HtmlTextInput) list.get(5)).setValueAttribute("1111111111");
        ((HtmlTextInput) list.get(6)).setValueAttribute("1111111111");

        list = getAllElementsOfGivenClass(page, null,
                HtmlSubmitInput.class);
        HtmlSubmitInput button = (HtmlSubmitInput) list.get(0);
        page = (HtmlPage) button.click();
        assertTrue(-1 != page.asText().indexOf("text1 was validated"));
        assertTrue(-1 != page.asText().indexOf("text2 was validated"));
        assertTrue(-1 != page.asText().indexOf("text3 was validated"));
        assertTrue(-1 != page.asText().indexOf("text4 was validated"));
        String str = "allowable maximum of " + '"' + "2" + '"';
        assertTrue(-1 != page.asText().indexOf(str));
        assertTrue(-1 != page.asText().indexOf("allowable maximum of '5'"));
        assertTrue(-1 != page.asText().indexOf("allowable maximum of '4'"));

    }

    public void testValidatorMessages() throws Exception {
        HtmlPage page = getPage("/faces/validator03.jsp");
        List list;
        list = getAllElementsOfGivenClass(page, null,
                HtmlTextInput.class);

        // set the initial value to be "1" for all input fields
        for (int i = 0; i < list.size(); i++) {
            ((HtmlTextInput) list.get(i)).setValueAttribute("1");
        }

        list = getAllElementsOfGivenClass(page, null,
                HtmlSubmitInput.class);
        HtmlSubmitInput button = (HtmlSubmitInput) list.get(0);
        page = (HtmlPage) button.click();
        assertTrue(-1 != page.asText().indexOf("j_idj_id17:dr1: Validation Error: Specified attribute is not between the expected values of 2 and 5."));
        assertTrue(-1 != page.asText().indexOf("DoubleRange2: Validation Error: Specified attribute is not between the expected values of 2 and 5."));
        assertTrue(-1 != page.asText().indexOf("j_idj_id17:l1: Validation Error: Value is less than allowable minimum of '2'"));
        assertTrue(-1 != page.asText().indexOf("Length2: Validation Error: Value is less than allowable minimum of '2'"));
        assertTrue(-1 != page.asText().indexOf("j_idj_id17:lr1: Validation Error: Specified attribute is not between the expected values of 2 and 5."));
        assertTrue(-1 != page.asText().indexOf("LongRange2: Validation Error: Specified attribute is not between the expected values of 2 and 5."));
    }

    public void testRequiredValidatorMessage() throws Exception {
        HtmlPage page = getPage("/faces/validator04.jsp");
        List list;
        list = getAllElementsOfGivenClass(page, null, HtmlTextInput.class);
        ((HtmlTextInput) list.get(2)).setValueAttribute("a");
        ((HtmlTextInput) list.get(3)).setValueAttribute("a");
        ((HtmlTextInput) list.get(4)).setValueAttribute("20");
        ((HtmlTextInput) list.get(5)).setValueAttribute("20");
        list = getAllElementsOfGivenClass(page, null, HtmlSubmitInput.class);
        page = (HtmlPage) ((HtmlSubmitInput) list.get(0)).click();
        assertTrue(-1 != page.asText().indexOf("Literal Message"));
        assertTrue(-1 != page.asText().indexOf("New String Value"));
        assertTrue(-1 != page.asText().indexOf("Converter Literal"));
        assertTrue(-1 != page.asText().indexOf("Converter Message Expression"));
        assertTrue(-1 != page.asText().indexOf("Validator Literal"));
        assertTrue(-1 != page.asText().indexOf("Validator Message Expression"));
    }

}
