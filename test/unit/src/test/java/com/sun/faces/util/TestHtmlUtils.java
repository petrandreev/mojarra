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
// TestHtmlUtils.java
package com.sun.faces.util;

import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

/**
 * <B>TestHtmlUtils</B> is a class ...
 */
public class TestHtmlUtils extends TestCase {

    public void testWriteURL() throws IOException {
        //Test url with no params
        testURLEncoding("http://www.google.com",
                "http://www.google.com",
                "http://www.google.com");
        //Test URL with one param
        testURLEncoding("http://www.google.com?joe=10",
                "http://www.google.com?joe=10",
                "http://www.google.com?joe=10");
        //Test URL with two params
        testURLEncoding("http://www.google.com?joe=10&fred=20",
                "http://www.google.com?joe=10&amp;fred=20",
                "http://www.google.com?joe=10&amp;fred=20");
        //Test URL with & entity encoded
        testURLEncoding("/index.jsf?joe=10&amp;fred=20",
                "/index.jsf?joe=10&amp;fred=20",
                "/index.jsf?joe=10&amp;fred=20");
        //Test URL with two params and second & close to end of string
        testURLEncoding("/index.jsf?joe=10&f=20",
                "/index.jsf?joe=10&amp;f=20",
                "/index.jsf?joe=10&amp;f=20");
        //Test URL with misplaced & expected behavior but not necissarily right.
        testURLEncoding("/index.jsf?joe=10&f=20&",
                "/index.jsf?joe=10&amp;f=20&amp;",
                "/index.jsf?joe=10&amp;f=20&amp;");
        //Test URL with encoded entity at end of URL expected behavior but not necissarily right.
        testURLEncoding("/index.jsf?joe=10&f=20&amp;",
                "/index.jsf?joe=10&amp;f=20&amp;",
                "/index.jsf?joe=10&amp;f=20&amp;");
    }

    public void testControlCharacters() throws IOException {

        final char[] controlCharacters = new char[32];
        for (int i = 0; i < 32; i++) {
            controlCharacters[i] = (char) i;
        }

        String[] stringValues = new String[32];
        for (int i = 0; i < 32; i++) {
            stringValues[i] = "b" + controlCharacters[i] + "b";
        }

        final String[] largeStringValues = new String[32];
        for (int i = 0; i < 32; i++) {
            largeStringValues[i] = (stringValues[i] + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        }

        for (int i = 0; i < 32; i++) {
            char[] textBuffer = new char[1024];
            char[] buffer = new char[1024];
            StringWriter writer = new StringWriter();
            HtmlUtils.writeAttribute(writer, false, false, buffer, stringValues[i], textBuffer, false);
            if (i == 9 || i == 10 || i == 12 || i == 13) {
                assertTrue(writer.toString().length() == 3);
            } else {
                assertTrue(writer.toString().length() == 2);
            }
        }

        for (int i = 0; i < 32; i++) {
            char[] textBuffer = new char[1024];
            char[] buffer = new char[1024];
            StringWriter writer = new StringWriter();
            HtmlUtils.writeAttribute(writer, false, false, buffer, largeStringValues[i], textBuffer, false);
            if (i == 9 || i == 10 || i == 12 || i == 13) {
                assertTrue(writer.toString().length() == 34);
            } else {
                assertTrue(writer.toString().length() == 33);
            }
        }

        for (int i = 0; i < 32; i++) {
            char[] textBuffer = new char[1024];
            char[] buffer = new char[1024];
            StringWriter writer = new StringWriter();
            HtmlUtils.writeText(writer, false, false, buffer, stringValues[i], textBuffer);
            if (i == 9 || i == 10 || i == 12 || i == 13) {
                assertTrue(writer.toString().length() == 3);
            } else {
                assertTrue(writer.toString().length() == 2);
            }
        }

        for (int i = 0; i < 32; i++) {
            char[] textBuffer = new char[1024];
            char[] buffer = new char[1024];
            StringWriter writer = new StringWriter();
            HtmlUtils.writeText(writer, false, false, buffer, largeStringValues[i], textBuffer);
            if (i == 9 || i == 10 || i == 12 || i == 13) {
                assertTrue(writer.toString().length() == 34);
            } else {
                assertTrue(writer.toString().length() == 33);
            }
        }
    }

    private void testURLEncoding(String urlToEncode, String expectedHTML, String expectedXML)
            throws IOException {
        char[] textBuffer = new char[1024];
        StringWriter xmlWriter = new StringWriter();
        HtmlUtils.writeURL(xmlWriter, urlToEncode, textBuffer, "UTF-8");
        System.out.println("XML: " + xmlWriter.toString());
        assertEquals(xmlWriter.toString(), expectedXML);
        StringWriter htmlWriter = new StringWriter();
        HtmlUtils.writeURL(htmlWriter, urlToEncode, textBuffer, "UTF-8");
        System.out.println("HTML: " + htmlWriter.toString());
        assertEquals(htmlWriter.toString(), expectedHTML);
    }
}
