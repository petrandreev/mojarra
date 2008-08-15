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

package com.sun.faces.facelets.tag.jsf.html;

import com.sun.faces.facelets.tag.Tag;
import com.sun.faces.facelets.tag.TagAttribute;
import com.sun.faces.facelets.tag.TagAttributes;
import com.sun.faces.facelets.tag.TagDecorator;

/**
 * @author Jacob Hookom
 * @version $Id$
 */
public final class HtmlDecorator implements TagDecorator {

    public final static String XhtmlNamespace = "http://www.w3.org/1999/xhtml";

    public final static HtmlDecorator Instance = new HtmlDecorator();

    /**
     * 
     */
    public HtmlDecorator() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.facelets.tag.TagDecorator#decorate(com.sun.facelets.tag.Tag)
     */
    public Tag decorate(Tag tag) {
        if (XhtmlNamespace.equals(tag.getNamespace())) {
            String n = tag.getLocalName();
            if ("a".equals(n)) {
                return new Tag(tag.getLocation(), HtmlLibrary.Namespace,
                        "commandLink", tag.getQName(), tag.getAttributes());
            }
            if ("form".equals(n)) {
                return new Tag(tag.getLocation(), HtmlLibrary.Namespace,
                        "form", tag.getQName(), tag.getAttributes());
            }
            if ("input".equals(n)) {
                TagAttribute attr = tag.getAttributes().get("type");
                if (attr != null) {
                    String t = attr.getValue();
                    TagAttributes na = removeType(tag.getAttributes());
                    if ("text".equals(t)) {
                        return new Tag(tag.getLocation(),
                                HtmlLibrary.Namespace, "inputText", tag
                                        .getQName(), na);
                    }
                    if ("password".equals(t)) {
                        return new Tag(tag.getLocation(),
                                HtmlLibrary.Namespace, "inputSecret", tag
                                        .getQName(), na);
                    }
                    if ("hidden".equals(t)) {
                        return new Tag(tag.getLocation(),
                                HtmlLibrary.Namespace, "inputHidden", tag
                                        .getQName(), na);
                    }
                    if ("submit".equals(t)) {
                        return new Tag(tag.getLocation(),
                                HtmlLibrary.Namespace, "commandButton", tag
                                        .getQName(), na);
                    }
                }
            }
        }
        return null;
    }

    private static TagAttributes removeType(TagAttributes attrs) {
        TagAttribute[] o = attrs.getAll();
        TagAttribute[] a = new TagAttribute[o.length - 1];
        int p = 0;
        for (int i = 0; i < o.length; i++) {
            if (!"type".equals(o[i].getLocalName())) {
                a[p++] = o[i];
            }
        }
        return new TagAttributes(a);
    }

}
