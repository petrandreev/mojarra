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

import com.sun.faces.facelets.tag.TagLibrary;

import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagConfig;

/**
 * 
 * @author Jacob Hookom
 * @version $Id$
 */
class TagUnit extends CompilationUnit implements TagConfig {

    private final TagLibrary library;

    private final String id;

    private final Tag tag;
    
    private final String namespace;
    
    private final String name;

    public TagUnit(TagLibrary library, String namespace, String name, Tag tag, String id) {
        this.library = library;
        this.tag = tag;
        this.namespace = namespace;
        this.name = name;
        this.id = id;
    }

    /*
     * special case if you have a ui:composition tag.  If and only if the
     * composition is on the same facelet page as the
     * composite:implementation, throw a FacesException with a helpful error
     * message.
     * */

    @Override
    protected void startNotify(CompilationManager manager) {
        if (this.name.equals("composition") && this.namespace.equals("http://java.sun.com/jsf/facelets")) {
            CompilerPackageCompilationMessageHolder messageHolder =
                    (CompilerPackageCompilationMessageHolder) manager.getCompilationMessageHolder();
            CompilationManager compositeComponentCompilationManager = messageHolder.
                getCurrentCompositeComponentCompilationManager();
            if (manager.equals(compositeComponentCompilationManager)) {
                // PENDING I18N
                String messageStr = 
                        "Because the definition of ui:composition causes any " +
                        "parent content to be ignored, it is invalid to use " +
                        "ui:composition directly inside of a composite component. " +
                        "Consider ui:decorate instead.";
                throw new FaceletException(messageStr);
            }
        }
    }

    public FaceletHandler createFaceletHandler() {
        return this.library.createTagHandler(this.namespace, this.name, this);
    }

    public FaceletHandler getNextHandler() {
        return this.getNextFaceletHandler();
    }

    public Tag getTag() {
        return this.tag;
    }

    public String getTagId() {
        return this.id;
    }

    public String toString() {
        return this.tag.toString();
    }

}
