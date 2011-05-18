/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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


import com.gargoylesoftware.htmlunit.html.*;
import com.sun.faces.htmlunit.HtmlUnitFacesTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.List;


/**
 * <p>Test Case for JSP Interoperability.</p>
 */

public class CommandLinkMultiFormTestCase extends HtmlUnitFacesTestCase {

    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public CommandLinkMultiFormTestCase(String name) {
        super(name);
        addExclusion(Container.TOMCAT6, "testMultiForm");
        addExclusion(Container.TOMCAT7, "testMultiForm");
        addExclusion(Container.WLS_10_3_4_NO_CLUSTER, "testMultiForm");
    }

    // ------------------------------------------------------ Instance Variables

    // ---------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Return the tests included in this test suite.
     * @return Tests included in suite
     */
    public static Test suite() {
        return (new TestSuite(CommandLinkMultiFormTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        super.tearDown();
    }

    // ------------------------------------------------- Individual Test Methods


    public void testMultiForm() throws Exception {
        HtmlForm form1, form2;
        HtmlAnchor link1, link2, link3;
        HtmlTextInput input;
        HtmlPage page, page1;
        HtmlHiddenInput hidden1, hidden2;

        page = getPage("/faces/taglib/commandLink_multiform_test.jsp");
        // press all command links..
        List forms = page.getForms();
        form1 = (HtmlForm) forms.get(0);
        form2 = (HtmlForm) forms.get(1);

        // links within the first form
        hidden1 = (HtmlHiddenInput) form1.getInputByName("form01:j_idcl");
        assertNotNull(hidden1);
        //hidden1.setValueAttribute("form01:Link1");
        //page1 = (HtmlPage) form1.submit();
        page1 = page.getAnchorByName("form01:Link1").click();
        assertTrue(-1 != page1.asText().indexOf("Thank you"));
        //hidden1.setValueAttribute("form01:Link2");
        //page1 = (HtmlPage) form1.submit();
        page1 = page.getAnchorByName("form01:Link2").click();
        assertTrue(-1 != page1.asText().indexOf("Thank you"));

        // links within second form
        hidden2 = (HtmlHiddenInput) form2.getInputByName("form02:j_idcl");
        assertNotNull(hidden2);
        //hidden2.setValueAttribute("form02:Link3");
        //page1 = (HtmlPage) form1.submit();
        page1 = page.getAnchorByName("form02:Link3").click();
        assertTrue(-1 != page1.asText().indexOf("Thank you"));
        //hidden2.setValueAttribute("form02:Link4");
        //page1 = (HtmlPage) form1.submit();
        page1 = page.getAnchorByName("form02:Link4").click();
        assertTrue(-1 != page1.asText().indexOf("Thank you"));
    }
}
