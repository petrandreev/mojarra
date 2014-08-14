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
package com.sun.faces.test.servlet30.composite;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class Spec745IT {

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

    @Test
    public void testType1() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXunset");
        assertTrue(page.asXml().contains("type of @untypedXunset: Object"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXunset: Object"));       
    }

    @Test
    public void testType2() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXsetByApi");
        assertTrue(page.asXml().contains("type of @untypedXsetByApi: Object"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXsetByApi: Object"));       
    }

    @Test
    public void testType3() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXliteral");
        assertTrue(page.asXml().contains("type of @untypedXliteral: Object"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXliteral: Object"));       
    }

    @Test
    public void testType4() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXwideEL");
        assertTrue(page.asXml().contains("type of @untypedXwideEL: Animal"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXwideEL: Animal"));       
    }

    @Test
    public void testType5() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXmediumEL");
        assertTrue(page.asXml().contains("type of @untypedXmediumEL: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXmediumEL: Dog"));       
    }

    @Test
    public void testType6() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXnarrowEL");
        assertTrue(page.asXml().contains("type of @untypedXnarrowEL: Wienerdoodle"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXnarrowEL: Wienerdoodle"));       
    }

    @Test
    public void testType7() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=untypedXnullEL");
        assertTrue(page.asXml().contains("type of @untypedXnullEL: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @untypedXnullEL: Dog"));       
    }

    @Test
    @Ignore
    public void testType8() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXunset");
        assertTrue(page.asXml().contains("type of @typedXunset: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXunset: Dog"));       
    }

    @Test
    @Ignore
    public void testType9() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXliteral");
        assertTrue(page.asXml().contains("type of @typedXliteral: Integer"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXliteral: Integer"));       
    }

    @Test
    @Ignore
    public void testType10() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXwideEL");
        assertTrue(page.asXml().contains("type of @typedXwideEL: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXwideEL: Dog"));       
    }

    @Test
    public void testType11() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXmediumEL");
        assertTrue(page.asXml().contains("type of @typedXmediumEL: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXmediumEL: Dog"));       
    }

    @Test
    public void testType12() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXnarrowEL");
        assertTrue(page.asXml().contains("type of @typedXnarrowEL: Wienerdoodle"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXnarrowEL: Wienerdoodle"));       
    }

    @Test
    public void testType13() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXnullEL");
        assertTrue(page.asXml().contains("type of @typedXnullEL: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXnullEL: Dog"));       
    }

    @Test
    @Ignore
    public void testType14() throws Exception {
        HtmlPage page = webClient.getPage(webUrl + "faces/attribute/attributeType.xhtml?test=typedXsetByApi");
        assertTrue(page.asXml().contains("type of @typedXsetByApi: Dog"));       
        HtmlElement button = (HtmlElement) page.getElementById("form:submit");
        page = button.click();
        assertTrue(page.asXml().contains("type of @typedXsetByApi: Dog"));       
    }
}
