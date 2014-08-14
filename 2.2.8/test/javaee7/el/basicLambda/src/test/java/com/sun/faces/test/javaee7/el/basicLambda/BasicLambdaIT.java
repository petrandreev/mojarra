/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2014 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.faces.test.javaee7.el.basicLambda;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import static com.sun.faces.test.junit.JsfServerExclude.WEBLOGIC_12_1_4;
import com.sun.faces.test.junit.JsfTest;
import com.sun.faces.test.junit.JsfTestRunner;
import static com.sun.faces.test.junit.JsfVersion.JSF_2_2_0;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JsfTestRunner.class)
public class BasicLambdaIT {

    private String webUrl;
    private WebClient webClient;

    @Before
    public void setUp() {
        webUrl = System.getProperty("integration.url");
        webClient = new WebClient();
    }

    @After
    public void tearDown() {
        webClient.closeAllWindows();
    }

    @JsfTest(value = JSF_2_2_0)
    @Test
    public void testIndex() throws Exception {
        HtmlPage page = webClient.getPage(webUrl);
        HtmlElement out = page.getHtmlElementById("output");
        assertEquals("20", out.asText());

        HtmlTextInput input = (HtmlTextInput) page.getElementById("input");
        input.setValueAttribute("1");
        HtmlSubmitInput button = (HtmlSubmitInput) page.getElementById("button");
        page = button.click();
        out = page.getHtmlElementById("output");
        assertEquals("40", out.asText());

        input = (HtmlTextInput) page.getElementById("input");
        input.setValueAttribute("2");
        button = (HtmlSubmitInput) page.getElementById("button");
        page = button.click();
        out = page.getHtmlElementById("output");
        assertEquals("60", out.asText());
    }

    @JsfTest(value = JSF_2_2_0, excludes = {WEBLOGIC_12_1_4})
    @Test
    public void testBookTable() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/bookTable.xhtml");
        assertTrue(page.asText().contains("At Swim Two Birds"));
        assertTrue(page.asText().contains("The Third Policeman"));
        HtmlElement out = page.getHtmlElementById("output2");
        assertEquals("The Picture of Dorian Gray", out.asText());
    }
}
