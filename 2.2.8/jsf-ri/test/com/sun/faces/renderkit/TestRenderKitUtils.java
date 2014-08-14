/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.faces.renderkit;

import com.sun.faces.cactus.ServletFacesTestCase;
import com.sun.faces.util.Util;

import javax.faces.component.UICommand;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import java.io.StringWriter;
import java.util.Collections;

public class TestRenderKitUtils extends ServletFacesTestCase {


    // ------------------------------------------------------------ Constructors


    public TestRenderKitUtils() {
        super();    
    }


    public TestRenderKitUtils(String name) {
        super(name);
    }


    // ------------------------------------------------------------ Test Methods


    public void testOnClickBackslashEscaping() throws Exception {

        String input = "return confirm('foo\\');";
        String expectedResult ="return confirm(\\'foo\\\\\\');";
        HtmlCommandLink link = new HtmlCommandLink();
        link.setOnclick(input);
        StringWriter capture = new StringWriter();
        ResponseWriter current = getFacesContext().getResponseWriter();
        if (current == null) {
            RenderKit renderKit =
                  RenderKitUtils.getCurrentRenderKit(getFacesContext());
            current = renderKit.createResponseWriter(capture, null, null);
            getFacesContext().setResponseWriter(current);
        } else {
            getFacesContext().setResponseWriter(current.cloneWithWriter(capture));
        }

        getFacesContext().getResponseWriter().startElement("link", link);
        RenderKitUtils.renderOnclick(getFacesContext(),
                                     link,
                                     null,
                                     "form",
                                     true);
        getFacesContext().getResponseWriter().endElement("link");

        String actualResult = capture.toString();
        assertTrue(actualResult.contains(expectedResult));

    }
}
