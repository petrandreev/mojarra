/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.faces.facelets.tag;

import javax.faces.webapp.pdl.facelets.tag.TagException;
import javax.faces.webapp.pdl.facelets.tag.TagConfig;
import com.sun.faces.facelets.FaceletContextImplBase;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.el.ELException;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import javax.faces.webapp.pdl.facelets.FaceletContext;
import javax.faces.webapp.pdl.facelets.FaceletException;
import javax.faces.webapp.pdl.facelets.tag.TagAttribute;
import com.sun.faces.facelets.TemplateClient;
import com.sun.faces.facelets.el.VariableMapperWrapper;
import com.sun.faces.facelets.tag.ui.DefineHandler;

/**
 * A Tag that is specified in a FaceletFile. Takes all attributes specified and
 * sets them on the FaceletContext before including the targeted Facelet file.
 * 
 * @author Jacob Hookom
 * @version $Id$
 */
final class UserTagHandler extends TagHandlerImpl implements TemplateClient {

    protected final TagAttribute[] vars;

    protected final URL location;

    protected final Map handlers;

    /**
     * @param config
     */
    public UserTagHandler(TagConfig config, URL location) {
        super(config);
        this.vars = this.tag.getAttributes().getAll();
        this.location = location;
                Iterator itr = this.findNextByType(DefineHandler.class);
        if (itr.hasNext()) {
            handlers = new HashMap();

            DefineHandler d = null;
            while (itr.hasNext()) {
                d = (DefineHandler) itr.next();
                this.handlers.put(d.getName(), d);
            }
        } else {
            handlers = null;
        }
    }

    /**
     * Iterate over all TagAttributes and set them on the FaceletContext's
     * VariableMapper, then include the target Facelet. Finally, replace the old
     * VariableMapper.
     * 
     * @see TagAttribute#getValueExpression(FaceletContext, Class)
     * @see VariableMapper
     * @see com.sun.faces.facelets.FaceletHandler#apply(com.sun.faces.facelets.FaceletContext, javax.faces.component.UIComponent)
     */
    public void apply(FaceletContext ctxObj, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
        FaceletContextImplBase ctx = (FaceletContextImplBase) ctxObj;
        VariableMapper orig = ctx.getVariableMapper();
        
        // setup a variable map
        if (this.vars.length > 0) {
            VariableMapper varMapper = new VariableMapperWrapper(orig);
            for (int i = 0; i < this.vars.length; i++) {
                varMapper.setVariable(this.vars[i].getLocalName(), this.vars[i]
                        .getValueExpression(ctx, Object.class));
            }
            ctx.setVariableMapper(varMapper);
        }
        
        // eval include
        try {
            ctx.pushClient(this);
            ctx.includeFacelet(parent, this.location);
        } catch (FileNotFoundException e) {
            throw new TagException(this.tag, e.getMessage());
        } finally {
            
            // make sure we undo our changes
            ctx.popClient(this);
            ctx.setVariableMapper(orig);
        }
    }

    public boolean apply(FaceletContext ctx, UIComponent parent, String name) throws IOException, FacesException, FaceletException, ELException {
        if (name != null) {
            if (this.handlers == null) {
                return false;
            }
            DefineHandler handler = (DefineHandler) this.handlers.get(name);
            if (handler != null) {
                handler.applyDefinition(ctx, parent);
                return true;
            } else {
                return false;
            }
        } else {
             this.nextHandler.apply(ctx, parent);
             return true;
         }
    }

}
