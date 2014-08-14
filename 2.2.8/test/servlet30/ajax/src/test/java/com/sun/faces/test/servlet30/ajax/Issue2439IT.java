/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDLGPL_1_1.html
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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import org.junit.*;
import static org.junit.Assert.*;

public class Issue2439IT {

    /**
     * Stores the web URL.
     */
    private String webUrl;
    /**
     * Stores the web client.
     */
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


    // ------------------------------------------------------------ Test Methods

    /**
     * This test verifies that an attribute nameed 'value' can be successfully updated
     * from a partial response (over Ajax). 
     */
    @Test
    public void testUpdateAttributeNamedValue() throws Exception {
        String expectedString1 = "<input id="+'"'+"form1:input1"+'"'+" type="+'"'+"text"+'"'+" name="+'"'+"form1:input1"+'"'+"/>";
        
        String expectedString2 = "<input id="+'"'+"form1:input2"+'"'+" type="+'"'+"text"+'"'+" name="+'"'+"form1:input2"+'"'+" onchange="+'"'+"jsf.util.chain(this,event,'mojarra.ab(this,event,"+"\\"+"'valueChange\\"+"'"+",\\'@this\\',\\'@all\\')')"+'"'+"/>";

        String expectedString3 = "<input id="+'"'+"form1:input3"+'"'+" type="+'"'+"text"+'"'+" name="+'"'+"form1:input3"
+'"'+" onchange="+'"'+"jsf.util.chain(this,event,'alert(\\'Hello, World!\\');')"+'"'+"/>";

        HtmlPage page = webClient.getPage(webUrl+"faces/disabledBehaviors.xhtml");
        assertTrue(page.asXml().contains(expectedString1));
        assertTrue(page.asXml().contains(expectedString2));
        assertTrue(page.asXml().contains(expectedString3));
    }
}

