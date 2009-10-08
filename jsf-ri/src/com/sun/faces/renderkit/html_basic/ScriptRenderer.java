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
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.application.Resource;

/**
 * <p>This <code>Renderer</code> handles the rendering of external <code>script</code>
 * references.</p>
 */
public class ScriptRenderer extends ScriptStyleBaseRenderer {
    
    
    @Override
    protected void startElement(ResponseWriter writer, UIComponent component) throws IOException {
        writer.startElement("script", component);
        writer.writeAttribute("type", "text/javascript", "type");
    }
    
    @Override
    protected void endElement(ResponseWriter writer) throws IOException {
        writer.endElement("script");
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component)
          throws IOException {

        Map<String,Object> attributes = component.getAttributes();
        Map<Object, Object> contextMap = context.getAttributes();

        String name = (String) attributes.get("name");
        String library = (String) attributes.get("library");

        String key = name + library;
        
        if (null == name) {
            return;
        }
        
        // Ensure this script is not rendered more than once per request
        if (contextMap.containsKey(key)) {
            return;
        }
        contextMap.put(key, Boolean.TRUE);

        // Special case of scripts that have query strings
        // These scripts actually use their query strings internally, not externally
        // so we don't need the resource to know about them
        int queryPos = name.indexOf("?");
        String query = null;
        if (queryPos > -1 && name.length() > queryPos) {
            query = name.substring(queryPos+1);
            name = name.substring(0,queryPos);
        }


        Resource resource = context.getApplication().getResourceHandler()
              .createResource(name, library);

        ResponseWriter writer = context.getResponseWriter();
        this.startElement(writer, component);

        String resourceSrc;
        if (resource == null) {
            resourceSrc = "RES_NOT_FOUND";
        } else {
            resourceSrc = resource.getRequestPath();
            if (query != null) {
                resourceSrc = resourceSrc +
                        ((resourceSrc.indexOf("?") > -1) ? "+" : "?") +
                        query;
            }
        }

        writer.writeURIAttribute("src", resourceSrc, "src");
        this.endElement(writer);
        super.encodeEnd(context, component);
    }
    
}
