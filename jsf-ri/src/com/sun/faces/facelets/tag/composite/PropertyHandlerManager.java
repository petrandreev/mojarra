/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2005-2007 The Apache Software Foundation
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


package com.sun.faces.facelets.tag.composite;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.component.UIComponent;
import javax.faces.application.ProjectStage;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.beans.FeatureDescriptor;


class PropertyHandlerManager {
    
    private static final Map<String,PropertyHandler> ALL_HANDLERS =
          new HashMap<String,PropertyHandler>(12, 1.0f);
    static {
        ALL_HANDLERS.put("targets", new StringValueExpressionPropertyHandler());
        ALL_HANDLERS.put("method-signature", new StringValueExpressionPropertyHandler());
        ALL_HANDLERS.put("type", new StringValueExpressionPropertyHandler());
        ALL_HANDLERS.put("default", new StringValueExpressionPropertyHandler());
        ALL_HANDLERS.put("displayName", new DisplayNamePropertyHandler());
        ALL_HANDLERS.put("shortDescription", new ShortDescriptionPropertyHandler());
        ALL_HANDLERS.put("expert", new ExpertPropertyHandler());
        ALL_HANDLERS.put("hidden", new HiddenPropertyHandler());
        ALL_HANDLERS.put("preferred", new PreferredPropertyHandler());
        ALL_HANDLERS.put("required", new BooleanValueExpressionPropertyHandler());
        ALL_HANDLERS.put("name", new NamePropertyHandler());
        ALL_HANDLERS.put("componentType", new ComponentTypePropertyHandler());
    }

    private static final String[] DEV_ONLY_ATTRIBUTES = {
          "displayName",
          "shortDescription",
          "export",
          "hidden",
          "preferred"
    };
    static {
        Arrays.sort(DEV_ONLY_ATTRIBUTES);
    }

    private Map<String,PropertyHandler> managedHandlers;
    private PropertyHandler genericHandler =
          new ObjectValueExpressionPropertyHandler();


    // -------------------------------------------------------- Constructors


    private PropertyHandlerManager(Map<String,PropertyHandler> managedHandlers) {

        this.managedHandlers = managedHandlers;

    }


    // ------------------------------------------------- Package Private Methods


    static PropertyHandlerManager getInstance(String[] attributes) {

        
        Map<String,PropertyHandler> handlers =
              new HashMap<String,PropertyHandler>(attributes.length, 1.0f);
        for (String attribute : attributes) {
            handlers.put(attribute, ALL_HANDLERS.get(attribute));
        }

        return new PropertyHandlerManager(handlers);

    }


    PropertyHandler getHandler(FaceletContext ctx, String name) {

        if (!ctx.getFacesContext().isProjectStage(ProjectStage.Development)) {
            if (Arrays.binarySearch(DEV_ONLY_ATTRIBUTES, name) >= 0) {
                return null;
            }
        }
        
        PropertyHandler h = managedHandlers.get(name);
        return ((h != null) ? h : genericHandler);

    }


    // ---------------------------------------------------------- Nested Classes
    
    
    private abstract static class BooleanFeatureDescriptorPropertyHandler
          implements TypedPropertyHandler {

        public Class<?> getEvalType() {
            return Boolean.class;
        }

    } // END BooleanFeatureDescriptorPropertyHandler
    
    
    private abstract static class StringFeatureDescriptorPropertyHandler
          implements TypedPropertyHandler {

        public Class<?> getEvalType() {
            return String.class;
        }

    } // END StringPropertyDescriptionPropertyHandler

    
    private abstract static class TypedValueExpressionPropertyHandler
          implements TypedPropertyHandler {

        public void apply(FaceletContext ctx,
                          String propName,
                          FeatureDescriptor target,
                          TagAttribute attribute) {

            target.setValue(propName,
                            attribute.getValueExpression(ctx, getEvalType()));

        }

        public abstract Class<?> getEvalType();

    } // END TypeValueExpressionPropertyHandler

    
    private static final class NamePropertyHandler 
          extends StringFeatureDescriptorPropertyHandler {

        public void apply(FaceletContext ctx, 
                          String propName, 
                          FeatureDescriptor target, 
                          TagAttribute attribute) {
            
            ValueExpression ve = attribute.getValueExpression(ctx, getEvalType());
            String v = (String) ve.getValue(ctx);
            if (v != null) {
                target.setShortDescription((String) ve.getValue(ctx));
            }
            
        }
    }

    
    private static final class ShortDescriptionPropertyHandler
          extends StringFeatureDescriptorPropertyHandler {

        public void apply(FaceletContext ctx, 
                          String propName, 
                          FeatureDescriptor target, 
                          TagAttribute attribute) {

            ValueExpression ve = attribute.getValueExpression(ctx, getEvalType());
            String v = (String) ve.getValue(ctx);
            if (v != null) {
                target.setShortDescription((String) ve.getValue(ctx));
            }

        }

    } // END ShortDescriptionPropertyHandler
    

    private static class StringValueExpressionPropertyHandler
          extends TypedValueExpressionPropertyHandler {

        public Class<?> getEvalType() {
            return String.class;
        }

    } // END StringValueExpressionPropertyHandler


    private static class ObjectValueExpressionPropertyHandler
          extends TypedValueExpressionPropertyHandler {

        @Override
        public Class<?> getEvalType() {
            return Object.class;
        }

    } // END ObjectValueExpressionPropertyHandler


    private static class ComponentTypePropertyHandler
          extends StringValueExpressionPropertyHandler {

        @Override
        public void apply(FaceletContext ctx,
                          String propName,
                          FeatureDescriptor target,
                          TagAttribute attribute) {
            super.apply(ctx,
                        UIComponent.COMPOSITE_COMPONENT_TYPE_KEY,
                        target,
                        attribute);

        }

    } // END ComponentTypePropertyHandler
    

    private static final class PreferredPropertyHandler
          extends BooleanFeatureDescriptorPropertyHandler {

        public void apply(FaceletContext ctx, String propName, FeatureDescriptor target, TagAttribute attribute) {

            ValueExpression ve = attribute
                  .getValueExpression(ctx, getEvalType());
            target.setPreferred((Boolean) ve.getValue(ctx));

        }

    } // END PreferredPropertyHandler

    
    private static final class HiddenPropertyHandler
          extends BooleanFeatureDescriptorPropertyHandler {

        public void apply(FaceletContext ctx, String propName, FeatureDescriptor target, TagAttribute attribute) {

            ValueExpression ve = attribute
                  .getValueExpression(ctx, getEvalType());
            target.setHidden((Boolean) ve.getValue(ctx));

        }

    } // END HiddenPropertyHandler
    

    private static final class ExpertPropertyHandler
          extends BooleanFeatureDescriptorPropertyHandler {

        public void apply(FaceletContext ctx, String propName, FeatureDescriptor target, TagAttribute attribute) {

            ValueExpression ve = attribute
                  .getValueExpression(ctx, getEvalType());
            target.setExpert((Boolean) ve.getValue(ctx));

        }

    } // END ExpertPropertyHandler

    
    private static final class DisplayNamePropertyHandler
          extends StringFeatureDescriptorPropertyHandler {

        public void apply(FaceletContext ctx, String propName, FeatureDescriptor target, TagAttribute attribute) {

            ValueExpression ve = attribute
                  .getValueExpression(ctx, getEvalType());
            target.setDisplayName((String) ve.getValue(ctx));
        }

    } // END DisplayNamePropertyHandler

    
    private static class BooleanValueExpressionPropertyHandler
          extends TypedValueExpressionPropertyHandler {

        public Class<?> getEvalType() {
            return Boolean.class;
        }

    } // END BooleanValueExpressionPropertyHandler
    
}
