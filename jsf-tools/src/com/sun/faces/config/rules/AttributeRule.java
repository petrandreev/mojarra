/*
 * $Id: AttributeRule.java,v 1.9 2007/04/27 22:02:45 ofung Exp $
 */

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

package com.sun.faces.config.rules;


import org.xml.sax.Attributes;

import com.sun.faces.config.beans.AttributeBean;
import com.sun.faces.config.beans.AttributeHolder;


/**
 * <p>Digester rule for the <code>&lt;attribute&gt;</code> element.</p>
 */

public class AttributeRule extends FeatureRule {


    private static final String CLASS_NAME =
        "com.sun.faces.config.beans.AttributeBean";


    // ------------------------------------------------------------ Rule Methods


    /**
     * <p>Create an empty instance of <code>AttributeBean</code>
     * and push it on to the object stack.</p>
     *
     * @param namespace the namespace URI of the matching element, or an 
     *   empty string if the parser is not namespace aware or the element has
     *   no namespace
     * @param name the local name if the parser is namespace aware, or just 
     *   the element name otherwise
     * @param attributes The attribute list of this element
     *
     * @exception IllegalStateException if the parent stack element is not
     *  of type FacesConfigBean
     */
    public void begin(String namespace, String name,
                      Attributes attributes) throws Exception {

        assert digester.peek() instanceof AttributeHolder
              : "Assertion Error: Expected AttributeHolder to be at the top of the stack";
       
        if (digester.getLogger().isDebugEnabled()) {
            digester.getLogger().debug("[AttributeRule]{" +
                                       digester.getMatch() +
                                       "} Push " + CLASS_NAME);
        }
        Class clazz =
            digester.getClassLoader().loadClass(CLASS_NAME);
        AttributeBean ab = (AttributeBean) clazz.newInstance();
        digester.push(ab);

    }


    /**
     * <p>No body processing is required.</p>
     *
     * @param namespace the namespace URI of the matching element, or an 
     *   empty string if the parser is not namespace aware or the element has
     *   no namespace
     * @param name the local name if the parser is namespace aware, or just 
     *   the element name otherwise
     * @param text The text of the body of this element
     */
    public void body(String namespace, String name,
                     String text) throws Exception {
    }


    /**
     * <p>Pop the <code>AttributeBean</code> off the top of the stack,
     * and either add or merge it with previous information.</p>
     *
     * @param namespace the namespace URI of the matching element, or an 
     *   empty string if the parser is not namespace aware or the element has
     *   no namespace
     * @param name the local name if the parser is namespace aware, or just 
     *   the element name otherwise
     *
     * @exception IllegalStateException if the popped object is not
     *  of the correct type
     */
    public void end(String namespace, String name) throws Exception {

        AttributeBean top = null;
        try {
            top = (AttributeBean) digester.pop();
        } catch (Exception e) {
            throw new IllegalStateException("Popped object is not a " +
                                            CLASS_NAME + " instance");
        }
        AttributeHolder ah = (AttributeHolder) digester.peek();
        AttributeBean old = ah.getAttribute(top.getAttributeName());
        if (old == null) {
            if (digester.getLogger().isDebugEnabled()) {
                digester.getLogger().debug("[AttributeRule]{" +
                                           digester.getMatch() +
                                           "} New(" +
                                           top.getAttributeName() +
                                           ")");
            }
            ah.addAttribute(top);
        } else {
            if (digester.getLogger().isDebugEnabled()) {
                digester.getLogger().debug("[AttributeRule]{" +
                                          digester.getMatch() +
                                          "} Merge(" +
                                          top.getAttributeName() +
                                          ")");
            }
            mergeAttribute(top, old);
        }

    }


    /**
     * <p>No finish processing is required.</p>
     *
     */
    public void finish() throws Exception {
    }


    // ---------------------------------------------------------- Public Methods


    public String toString() {

        StringBuffer sb = new StringBuffer("AttributeRule[className=");
        sb.append(CLASS_NAME);
        sb.append("]");
        return (sb.toString());

    }


    // --------------------------------------------------------- Package Methods


    // Merge "top" into "old"
    static void mergeAttribute(AttributeBean top, AttributeBean old) {

        // Merge singleton properties
        if (top.getAttributeClass() != null) {
            old.setAttributeClass(top.getAttributeClass());
        }
        if (top.getSuggestedValue() != null) {
            old.setSuggestedValue(top.getSuggestedValue());
        }
        if (top.getDefaultValue() != null) {
            old.setDefaultValue(top.getDefaultValue());
        }
        if (top.isPassThrough()) {
            old.setPassThrough(true);
        }
        if (top.isRequired()) {
            old.setRequired(true);
        }
        if (!top.isTagAttribute()) {
            old.setTagAttribute(false);
        }
        if(null != top.getBehaviors()){
        	old.addAllBehaviors(top.getBehaviors());
        }
        if(top.isDefaultBehavior()){
        	old.setDefaultBehavior(true);
        }

        // Merge common collections
        mergeFeatures(top, old);

        // Merge unique collections

    }


    // Merge "top" into "old"
    static void mergeAttributes(AttributeHolder top, AttributeHolder old) {

        AttributeBean ab[] = top.getAttributes();
        for (int i = 0; i < ab.length; i++) {
            AttributeBean abo = old.getAttribute(ab[i].getAttributeName());
            if (abo == null) {
                old.addAttribute(ab[i]);
            } else {
                mergeAttribute(ab[i], abo);
            }
        }

    }


}
