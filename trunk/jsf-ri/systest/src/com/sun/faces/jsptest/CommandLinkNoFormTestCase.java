/*
 * $Id: CommandLinkNoFormTestCase.java,v 1.3 2006/03/29 22:38:45 rlubke Exp $
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

package com.sun.faces.jsptest;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.sun.faces.htmlunit.AbstractTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/** <p>Verify expected behavior when command link is not enclosed by a form</p> */

public class CommandLinkNoFormTestCase extends AbstractTestCase {

    // ------------------------------------------------------------ Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public CommandLinkNoFormTestCase(String name) {

        super(name);

    }

    // ---------------------------------------------------------- Public Methods


    /** Return the tests included in this test suite. */
    public static Test suite() {

        return (new TestSuite(CommandLinkNoFormTestCase.class));

    }

    // ------------------------------------------------------ Instance Variables

    // ---------------------------------------------------- Overall Test Methods


    /** Set up instance variables required by this test case. */
    public void setUp() throws Exception {

        super.setUp();

    }


    /** Tear down instance variables required by this test case. */
    public void tearDown() {

        super.tearDown();

    }

    // ------------------------------------------------- Individual Test Methods    

    public void testRenderedClinkWithNoForm() throws Exception {

        String noFormString =
              ": This link is disabled as it is not nested within a JSF form.";
        HtmlPage page = getPage("/faces/standard/clinknoform.jsp");
        List list = getAllElementsOfGivenClass(page, null,
                                               HtmlSpan.class);

        HtmlSpan p = (HtmlSpan) list.get(0);
        assertEquals("Link1" + noFormString, p.asText());
        p = (HtmlSpan) list.get(1);
        assertEquals("Link2" + noFormString, p.asText());
        p = (HtmlSpan) list.get(2);
        assertEquals("Click me once and click me twice" +
                     noFormString, p.asText());

    }

}
