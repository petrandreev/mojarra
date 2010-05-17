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

package com.sun.faces.renderkit.html_basic;

import java.io.IOException;
import java.util.ListIterator;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.Attribute;
import com.sun.faces.renderkit.AttributeManager;

/**
 * <p>This <code>Renderer</code> is responsible for rendering
 * the standard HTML body element as well as rendering any resources
 * that should be output before the <code>body</code> tag is closed.</p>
 */
public class BodyRenderer extends Renderer {

    private static final Attribute[] BODY_ATTRIBUTES =
             AttributeManager.getAttributes(AttributeManager.Key.OUTPUTBODY);


    @Override
    public void decode(FacesContext context, UIComponent component) {
        // no-op
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component)
          throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("body", component);
        String styleClass = (String) component.getAttributes().get("styleClass");
        if (styleClass != null && styleClass.length() != 0) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        RenderKitUtils.renderPassThruAttributes(context,
                                                writer,
                                                component,
                                                BODY_ATTRIBUTES);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component)
          throws IOException {
        // no-op
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
          throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator iter = (viewRoot.getComponentResources(context, "body")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = (UIComponent)iter.next();
            resource.encodeAll(context);
        }
        RenderKitUtils.renderUnhandledMessages(context);
        writer.endElement("body");
    }
    
}
