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

package com.sun.faces.facelets.compiler;

import com.sun.faces.facelets.el.ELText;
import com.sun.faces.facelets.tag.jsf.ComponentSupport;
import com.sun.faces.facelets.util.FastWriter;

import javax.el.ELException;
import javax.faces.component.UIComponent;
import javax.faces.component.UniqueIdVendor;
import javax.faces.view.facelets.FaceletContext;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Adam Winer
 * @version $Id$
 */
final class UIInstructionHandler extends AbstractUIHandler {

    private final String alias;

    private final String id;

    private final ELText txt;
    
    private final Instruction[] instructions;

    private final int length;
  
    private final boolean literal;

    public UIInstructionHandler(String alias, String id, Instruction[] instructions, ELText txt) {
        this.alias = alias;
        this.id = id;
        this.instructions = instructions;
        this.txt = txt;
        this.length = txt.toString().length();

        boolean literal = true;
        int size = instructions.length;

        for (int i = 0; i < size; i++) {
            Instruction ins = this.instructions[i];
            if (!ins.isLiteral()) {
                literal = false;
                break;
            }
        }

        this.literal = literal;
    }


    public void apply(FaceletContext ctx, UIComponent parent)
          throws IOException {
        if (parent != null) {
            // our id
            String id = ctx.generateUniqueId(this.id);

            // grab our component
            UIComponent c = ComponentSupport.findChildByTagId(parent, id);
            boolean componentFound = false;
            if (c != null) {
                componentFound = true;
                // mark all children for cleaning 
                ComponentSupport.markForDeletion(c);
            } else {
                Instruction[] applied;
                if (this.literal) {
                    applied = this.instructions;
                } else {
                    int size = this.instructions.length;
                    applied = new Instruction[size];
                    // Create a new list with all of the necessary applied
                    // instructions
                    Instruction ins;
                    for (int i = 0; i < size; i++) {
                        ins = this.instructions[i];
                        applied[i] = ins.apply(ctx.getExpressionFactory(), ctx);
                    }
                }

                c = new UIInstructions(txt, applied);
                // mark it owned by a facelet instance
                String uid;
                UIComponent ancestorNamingContainer = parent.getNamingContainer();
                if (null != ancestorNamingContainer &&
                        ancestorNamingContainer instanceof UniqueIdVendor) {
                    uid = ((UniqueIdVendor) ancestorNamingContainer).createUniqueId(ctx.getFacesContext(), id);
                } else {
                    uid = ComponentSupport.getViewRoot(ctx, parent).createUniqueId(ctx.getFacesContext(), id);
                }
                
                c.setId(uid);
                c.getAttributes().put(ComponentSupport.MARK_CREATED, id);
            }
            // finish cleaning up orphaned children
            if (componentFound) {
                ComponentSupport.finalizeForDeletion(c);               
                parent.getChildren().remove(c);
            }
            this.addComponent(ctx, parent, c);
        }
    }

    public String toString() {
        return this.txt.toString();
    }

    public String getText() {
        return this.txt.toString();
    }

    public String getText(FaceletContext ctx) {
        Writer writer = new FastWriter(this.length);
        try {
            this.txt.apply(ctx.getExpressionFactory(), ctx).write(writer, ctx);
        } catch (IOException e) {
            throw new ELException(this.alias + ": "+ e.getMessage(), e.getCause());
        }
        return writer.toString();
    }

}
