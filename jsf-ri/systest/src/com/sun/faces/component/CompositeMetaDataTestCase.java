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

package com.sun.faces.component;

import com.sun.faces.htmlunit.AbstractTestCase;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CompositeMetaDataTestCase extends AbstractTestCase {

    public CompositeMetaDataTestCase(String name) {
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
        return (new TestSuite(CompositeMetaDataTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        super.tearDown();
    }


    /**
     * Added for issue 10
     *
     * @throws Exception
     */
    public void testPrefixMappedFaceletPage() throws Exception {

        HtmlPage page = getPage("/faces/composite/jsr276-using.xhtml");
        String text = page.asText();
        assertTrue(-1 != text.indexOf("composite component with correctly specified jsr276 metadata"));
        assertTrue(-1 == text.indexOf("prefix fmd"));
        assertTrue(-1 != text.indexOf("prefix metaData"));
    }

    public void testExtensionMappedFaceletPage() throws Exception {

        HtmlPage page = getPage("/composite/jsr276-using.faces");
        String text = page.asText();
        assertTrue(-1 != text.indexOf("composite component with correctly specified jsr276 metadata"));
        assertTrue(-1 != text.indexOf("composite component with incorrectly specified jsr276 metadata"));
    }

   public void testDirectlyAccessedCompositeComponent() throws Exception {

       HtmlPage page = null;
       client.setThrowExceptionOnFailingStatusCode(false);
       page = getPage("/faces/resources/composite/jsr276Correct01.xhtml");
       String text = page.asText();
       assertTrue(text.contains("Component Not Found for identifier"));
    }

    

} // end of class PathTestCase
