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

package com.sun.faces.facelets.tag.jsf;

import com.sun.faces.context.StateContext;
import com.sun.faces.facelets.Facelet;
import com.sun.faces.facelets.FaceletFactory;
import com.sun.faces.facelets.util.ReflectionUtil;
import com.sun.faces.facelets.el.VariableMapperWrapper;
import com.sun.faces.facelets.tag.jsf.ComponentTagHandlerDelegateImpl.CreateComponentDelegate;
import com.sun.faces.facelets.tag.MetaRulesetImpl;
import com.sun.faces.facelets.tag.MetadataTargetImpl;
import com.sun.faces.util.RequestStateManager;
import com.sun.faces.util.Util;
import com.sun.faces.util.FacesLogger;
import java.beans.BeanDescriptor;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.application.Resource;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.component.ActionSource;
import javax.faces.component.ValueHolder;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UISelectOne;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectHandler;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.MetaRule;
import java.beans.PropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.faces.FactoryFinder;
import javax.faces.view.ViewDeclarationLanguage;
import javax.faces.view.ViewDeclarationLanguageFactory;

/**
 * <p>
 * Facelet handler responsible for, building the component tree representation
 * of a composite component based on the metadata contained in the composite
 * interface and implementation sections of the composite component template.
 * </p>
 */
public class CompositeComponentTagHandler extends ComponentHandler implements CreateComponentDelegate {

    private static final Logger LOGGER = FacesLogger.TAGLIB.getLogger();
    private Resource ccResource;
    private UIComponent cc;
    private TagAttribute binding;


    // ------------------------------------------------------------ Constructors


    CompositeComponentTagHandler(Resource ccResource, ComponentConfig config) {
        super(config);
        this.ccResource = ccResource;
        this.binding = config.getTag().getAttributes().get("binding");
        ((ComponentTagHandlerDelegateImpl)this.getTagHandlerDelegate()).setCreateCompositeComponentDelegate(this);
    }


    // ------------------------------------ Methods from CreateComponentDelegate
    


    public UIComponent createComponent(FaceletContext ctx) {
        
        FacesContext context = ctx.getFacesContext();
        // we have to handle the binding here, as Application doesn't
        // expose a method to do so with Resource.
        if (binding != null) {
            ValueExpression ve = binding.getValueExpression(ctx, UIComponent.class);
            cc = (UIComponent) ve.getValue(ctx);
            if (cc != null && !UIComponent.isCompositeComponent(cc)) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.log(Level.SEVERE,
                               "jsf.compcomp.binding.eval.non.compcomp",
                               binding.toString());
                }
                cc = null;
            }
            if (cc == null) {
                cc = context.getApplication().createComponent(context, ccResource);
                ve.setValue(ctx, cc);
            }
        } else {
            cc = context.getApplication().createComponent(context, ccResource);
        }

        return cc;

    }


    // ------------------------------------------- Methods from ComponentHandler

    
    @Override
    public void applyNextHandler(FaceletContext ctx, UIComponent c) throws IOException, FacesException, ELException {

        // attributes need to be applied before any action is taken on
        // nested children handlers or the composite component handlers
        // as there may be an expression evaluated at tree creation time
        // that needs access to these attributes
        setAttributes(ctx, c);

        // Allow any nested elements that reside inside the markup element
        // for this tag to get applied
        super.applyNextHandler(ctx, c);

        // Apply the facelet for this composite component
        applyCompositeComponent(ctx, c);

        // Allow any PDL declared attached objects to be retargeted
        if (ComponentHandler.isNew(c)) {
            FacesContext context = ctx.getFacesContext();
            String viewId = context.getViewRoot().getViewId();
            // PENDING(rlubke): performance
            ViewDeclarationLanguageFactory factory = (ViewDeclarationLanguageFactory)
                    FactoryFinder.getFactory(FactoryFinder.VIEW_DECLARATION_LANGUAGE_FACTORY);

            ViewDeclarationLanguage vdl = factory.getViewDeclarationLanguage(viewId);
            vdl.retargetAttachedObjects(context, c,
                    getAttachedObjectHandlers(c, false));
            vdl.retargetMethodExpressions(context, c);

            // RELEASE_PENDING This is *ugly*.  See my comments in
            // ComponentTagHandlerDelegateImpl at the end of the apply()
            // method
            if (StateContext.getStateContext(context).partialStateSaving(context, viewId)) {
                markInitialState(c);
            }


        }

    }


    /**
     * Specialized implementation to prevent caching of the MetaRuleset when
     * ProjectStage is Development.
     */
    @Override
    public void setAttributes(FaceletContext ctx, Object instance) {

        if (instance != null) {
            if (ctx.getFacesContext().isProjectStage(ProjectStage.Development)) {
                Metadata meta = createMetaRuleset(instance.getClass()).finish();
                meta.applyMetadata(ctx, instance);
            } else {
                super.setAttributes(ctx, instance);
            }
        }

    }


    /**
     * This is basically a copy of what's define in ComponentTagHandlerDelegateImpl
     * except for the MetaRuleset implementation that's being used.
     *
     * This also allows us to treat composite component's backed by custom component
     * implementation classes based on their type.
     *
     * @param type the <code>Class</code> for which the
     * <code>MetaRuleset</code> must be created.
     *
     */
    @Override
    protected MetaRuleset createMetaRuleset(Class type) {

        Util.notNull("type", type);
        MetaRuleset m = new CompositeComponentMetaRuleset(getTag(), type, (BeanInfo) cc.getAttributes().get(UIComponent.BEANINFO_KEY));

        // ignore standard component attributes
        m.ignore("binding").ignore("id");

        m.addRule(CompositeComponentRule.Instance);

        // if it's an ActionSource
        if (ActionSource.class.isAssignableFrom(type)) {
            m.addRule(ActionSourceRule.Instance);
        }

        // if it's a ValueHolder
        if (ValueHolder.class.isAssignableFrom(type)) {
            m.addRule(ValueHolderRule.Instance);

            // if it's an EditableValueHolder
            if (EditableValueHolder.class.isAssignableFrom(type)) {
                m.ignore("submittedValue");
                m.ignore("valid");
                m.addRule(EditableValueHolderRule.Instance);
            }
        }

        // if it's a selectone or selectmany
        if (UISelectOne.class.isAssignableFrom(type) || UISelectMany.class.isAssignableFrom(type)) {
            m.addRule(RenderPropertyRule.Instance);
        }

        return m;

    }


    // ---------------------------------------------------------- Public Methods


    public static List<AttachedObjectHandler> getAttachedObjectHandlers(UIComponent component) {

        return getAttachedObjectHandlers(component, true);

    }
    

    @SuppressWarnings({"unchecked"})
    public static List<AttachedObjectHandler> getAttachedObjectHandlers(UIComponent component,
                                                                        boolean create) {
        Map<String, Object> attrs = component.getAttributes();
        List<AttachedObjectHandler> result = (List<AttachedObjectHandler>)
              attrs.get("javax.faces.RetargetableHandlers");

        if (result == null) {
            if (create) {
                result = new ArrayList<AttachedObjectHandler>();
                attrs.put("javax.faces.RetargetableHandlers", result);
            } else {
                result = Collections.EMPTY_LIST;
            }
        }
        return result;

    }



    // --------------------------------------------------------- Private Methods

    
    private void applyCompositeComponent(FaceletContext ctx, UIComponent c)
    throws IOException {

        FacesContext facesContext = ctx.getFacesContext();
        FaceletFactory factory = (FaceletFactory)
              RequestStateManager.get(facesContext, RequestStateManager.FACELET_FACTORY);
        VariableMapper orig = ctx.getVariableMapper();
        
        UIPanel facetComponent;
        if (ComponentHandler.isNew(c)) {
            facetComponent = (UIPanel)
            facesContext.getApplication().createComponent("javax.faces.Panel");
            facetComponent.setRendererType("javax.faces.Group");
            c.getFacets().put(UIComponent.COMPOSITE_FACET_NAME, facetComponent);
        }                                                                                 
        else {
            facetComponent = (UIPanel) 
                    c.getFacets().get(UIComponent.COMPOSITE_FACET_NAME);
        }
        assert(null != facetComponent);
        
        try {
            Facelet f = factory.getFacelet(ccResource.getURL());

            VariableMapper wrapper = new VariableMapperWrapper(orig) {

                @Override
                public ValueExpression resolveVariable(String variable) {
                    return super.resolveVariable(variable);
                }
                
            };
            ctx.setVariableMapper(wrapper);
            f.apply(facesContext, facetComponent);
        } finally {
            ctx.setVariableMapper(orig);
        }

    }






    private void markInitialState(UIComponent c) {
        if (!c.initialStateMarked()) {
            c.markInitialState();
            for (Iterator<UIComponent> i = c.getFacetsAndChildren(); i.hasNext(); ) {
                markInitialState(i.next());
            }
        }
    }


    // ---------------------------------------------------------- Nested Classes


    /**
     * Specialized MetaRulesetImpl to return CompositeMetadataTarget for component
     * attribute handling.
     */
    private static final class CompositeComponentMetaRuleset extends MetaRulesetImpl {

        private BeanInfo compBeanInfo;
        private Class<?> type;

        public CompositeComponentMetaRuleset(Tag tag,
                                             Class<?> type,
                                             BeanInfo compBeanInfo) {

            super(tag, type);
            this.compBeanInfo = compBeanInfo;
            this.type = type;

        }

        @Override
        protected MetadataTarget getMetadataTarget() {
            try {
                return new CompositeMetadataTarget(type, compBeanInfo);
            } catch (IntrospectionException ie) {
                throw new FacesException(ie);
            }
        }


        // ------------------------------------------------------ Nested Classes


        /**
         * This class is responsible for creating ValueExpression instances with
         * the expected type based off the following:
         *
         *  - if the composite:attribute metadata is present, then use the type
         *    if specified by the author, or default to Object.class
         *  - if no composite:attribute is specified, then attempt to return the
         *    type based off the bean info for this component
         */
        private static final class CompositeMetadataTarget extends MetadataTargetImpl {

            private BeanInfo compBeanInfo;


            // ---------------------------------------------------- Construcrors


            public CompositeMetadataTarget(Class<?> type, BeanInfo compBeanInfo)
            throws IntrospectionException {

                super(type);
                this.compBeanInfo = compBeanInfo;

            }


            // --------------------------------- Methods from MetadataTargetImpl


            @Override
            public Class getPropertyType(String name) {
                PropertyDescriptor compDescriptor = findDescriptor(name);
                if (compDescriptor != null) {
                    // composite:attribute declaration...
                    ValueExpression typeVE = (ValueExpression) compDescriptor.getValue("type");
                    if (typeVE == null) {
                        return Object.class;
                    } else {
                        String className = (String) typeVE.getValue(FacesContext.getCurrentInstance().getELContext());
                        if (className != null) {
                            className = prefix(className);
                            try {
                                return ReflectionUtil.forName(className);
                            } catch (ClassNotFoundException cnfe) {
                                throw new FacesException(cnfe);
                            }
                        } else {
                            return Object.class;
                        }
                    }
                } else {
                    // defer to the default processing which will inspect the
                    // PropertyDescriptor of the UIComponent type
                    return super.getPropertyType(name);
                }
            }


            // ------------------------------------------------- Private Methods


            private PropertyDescriptor findDescriptor(String name) {

                for (PropertyDescriptor pd : compBeanInfo.getPropertyDescriptors()) {

                    if (pd.getName().equals(name)) {
                        return pd;
                    }

                }
                return null;

            }


            private String prefix(String className) {

                if (className.indexOf('.') == -1
                    && Character.isUpperCase(className.charAt(0))) {
                    return ("java.lang." + className);
                } else {
                    return className;
                }

            }
        }

    } // END CompositeComponentMetaRuleset


    /**
     * <code>MetaRule</code> for populating the ValueExpression map of a
     * composite component.
     */
    private static class CompositeComponentRule extends MetaRule {

        private static final CompositeComponentRule Instance = new CompositeComponentRule();


        // ------------------------------------------ Methods from ComponentRule


        public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {

            if (meta.isTargetInstanceOf(UIComponent.class)) {
                Class type = meta.getPropertyType(name);
                if (type == null) {
                    type = Object.class;
                }

                if (!attribute.isLiteral()) {
                    return new CompositeExpressionMetadata(name, type, attribute);
                } else {
                    return new LiteralAttributeMetadata(name, type, attribute);
                }
            }
            return null;

        }


        // ------------------------------------------------------ Nested Classes


        /**
         * For literal expressions, coerce the literal value to the type
         * as provided to the constructor prior to setting the value into
         * the component's attribute map.
         */
        private static final class LiteralAttributeMetadata extends Metadata {

            private String name;
            private Class<?> type;
            private TagAttribute attribute;


            // ---------------------------------------------------- Constructors


            public LiteralAttributeMetadata(String name,
                                            Class<?> type,
                                            TagAttribute attribute) {

                this.name = name;
                this.type = type;
                this.attribute = attribute;
                
            }


            // ------------------------------------------- Methods from Metadata


            public void applyMetadata(FaceletContext ctx, Object instance) {

                UIComponent c = (UIComponent) instance;
                c.getAttributes().put(name, attribute.getObject(ctx, type));

            }

        } // END LiteralAttributeMetadata


        /**
         * CompositeExpressionMetadata sets up specialized wrapper ValueExpression
         * instances around the source ValueExpression that, when evaluated,
         * will cause the parent composite component of the currently available
         * composite component to be pushed onto a stack that the
         * ImplicitObjectELResolver will check for.
         */
        private static final class CompositeExpressionMetadata extends Metadata {

            private String name;
            private Class<?> type;
            private TagAttribute attr;


            // ---------------------------------------------------- Constructors


            public CompositeExpressionMetadata(String name,
                                               Class<?> type,
                                               TagAttribute attr) {
                this.name = name;
                this.type = type;
                this.attr = attr;


            }

            // ------------------------------------------- Methods from Metadata


            public void applyMetadata(FaceletContext ctx, Object instance) {

                ValueExpression ve = attr.getValueExpression(ctx, type);
                UIComponent cc = (UIComponent) instance;
                assert (UIComponent.isCompositeComponent(cc));
                Map<String, Object> attrs = cc.getAttributes();
                BeanInfo componentMetadata = (BeanInfo) attrs.get(UIComponent.BEANINFO_KEY);
                BeanDescriptor desc = componentMetadata.getBeanDescriptor();
                Collection<String> attributesWithDeclaredDefaultValues = (Collection<String>)
                        desc.getValue(UIComponent.ATTRS_WITH_DECLARED_DEFAULT_VALUES);
                if (null != attributesWithDeclaredDefaultValues &&
                        attributesWithDeclaredDefaultValues.contains(name)) {
                    // It is necessary to remove the value from the attribute
                    // map because the ELexpression transparancy doesn't know
                    // about the value's existence.
                    attrs.remove(name);
                }
                cc.setValueExpression(name, ve);

            }


        } // END CompositeExpressionMetadata


    } // END CompositeComponentRule
    
}
