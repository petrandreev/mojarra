/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.faces.test.servlet30.ajax;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Issue1284IT {

    private String webUrl;
    private WebClient webClient;

    @Before
    public void setUp() {
        webUrl = System.getProperty("integration.url");
        webClient = new WebClient();
        webClient.setJavaScriptEnabled(true);
        webClient.setJavaScriptTimeout(60000);
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    @Test
    public void testCdataEscape5() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/issue1284.xhtml");
        assertTrue(page.getHtmlElementById("form1:out1").asText().equals("")); 
        assertTrue(page.getHtmlElementById("form1:in1").asText().equals("")); 

        HtmlTextInput in1 = (HtmlTextInput) page.getHtmlElementById("form1:in1");
        in1.type("[");

        // Submit the ajax request
        HtmlSubmitInput button1 = (HtmlSubmitInput) page.getHtmlElementById("form1:button1");
        page = (HtmlPage) button1.click();
        webClient.waitForBackgroundJavaScript(60000);

        // Check that the ajax request succeeds
        assertTrue(page.getHtmlElementById("form1:out1").asText().equals("[")); 
    }

    @Test
    public void testCdataEscape6() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/issue1284.xhtml");
        assertTrue(page.getHtmlElementById("form1:out1").asText().equals("")); 
        assertTrue(page.getHtmlElementById("form1:in1").asText().equals("")); 

        HtmlTextInput in1 = (HtmlTextInput) page.getHtmlElementById("form1:in1");
        in1.type("var a=[");

        // Submit the ajax request
        HtmlSubmitInput button1 = (HtmlSubmitInput) page.getHtmlElementById("form1:button1");
        page = (HtmlPage) button1.click();
        webClient.waitForBackgroundJavaScript(60000);

        // Check that the ajax request succeeds
        assertTrue(page.getHtmlElementById("form1:out1").asText().equals("var a=[")); 
    }
}
