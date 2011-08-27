/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2011 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.faces.annotation;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;
import javax.servlet.http.HttpServletRequest;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.mgbean.BeanBuilder;
import com.sun.faces.mgbean.BeanManager;
import com.sun.faces.mgbean.ManagedBeanInfo;


public class AnnotationTestBean {

    public String getTestResult() {

        try {
            testAnnotatedComponentsWebInfClasses();
            testAnnotatedComponentsWebInfLib();
            return Boolean.TRUE.toString();
        } catch (Exception e) {
            Logger.getAnonymousLogger().log(Level.SEVERE,
                                            "AnnotationTestBean validation failure!",
                                            e);
            return Boolean.FALSE.toString();
        }
    }

    private void testAnnotatedComponentsWebInfClasses() throws Exception {
        
        String injectedString;

        FacesContext ctx = FacesContext.getCurrentInstance();
        Application app = ctx.getApplication();

        UIComponent c = app.createComponent("AnnotatedComponent");
        assertNotNull(c);
        assertTrue(c instanceof AnnotatedComponent);

        Converter cv = app.createConverter("AnnotatedConverter");
        assertNotNull(cv);
        assertTrue(cv instanceof AnnotatedConverter);
        injectedString = ((AnnotatedConverter)cv).getWelcomeMessage();
        assertTrue(injectedString.equals("Hello World from env-entry!"));
        

        cv = app.createConverter(java.lang.CharSequence.class);
        assertNotNull(cv);
        assertTrue(cv instanceof AnnotatedConverterForClass);

        Validator v = app.createValidator("AnnotatedValidator");
        assertNotNull(v);
        assertTrue(v instanceof AnnotatedValidator);
        Set<String> defaultValidatorIds = app.getDefaultValidatorInfo().keySet();
        assertFalse(defaultValidatorIds.contains("AnnotatedValidator"));
        injectedString = ((AnnotatedValidator)v).getWelcomeMessage();
        assertTrue(injectedString.equals("Hello World from env-entry!"));

        // AnnotatedValidatorDefault has isDefault set to true.  Make sure
        // it's present in the default validator info obtained above.
        assertTrue(defaultValidatorIds.contains("AnnotatedValidatorDefault"));
        
        Behavior b = app.createBehavior("AnnotatedBehavior");
        assertNotNull(b);
        assertTrue(b instanceof AnnotatedBehavior);
        injectedString = ((AnnotatedBehavior)b).getWelcomeMessage();
        assertTrue(injectedString.equals("Hello World from env-entry!"));

        RenderKitFactory rkf = (RenderKitFactory) FactoryFinder
              .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit rk =
              rkf.getRenderKit(ctx, RenderKitFactory.HTML_BASIC_RENDER_KIT);
        assertNotNull(rk);

        Renderer r = rk.getRenderer("AnnotatedRenderer", "AnnotatedRenderer");
        assertNotNull(r);
        assertTrue(r instanceof AnnotatedRenderer);

        ClientBehaviorRenderer br = rk.getClientBehaviorRenderer("AnnotatedBehaviorRenderer");
        assertNotNull(br);
        assertTrue(br instanceof AnnotatedBehaviorRenderer);
        // validate class annotated with @ManagedBean
        ApplicationAssociate associate =
              ApplicationAssociate.getInstance(ctx.getExternalContext());
        BeanManager manager = associate.getBeanManager();
        BeanBuilder bean1 = manager.getBuilder("annotatedBean");
        assertNotNull(bean1);
        ManagedBeanInfo bean1Info = bean1.getManagedBeanInfo();
        assertEquals("annotatedBean", bean1Info.getName());
        assertEquals("request", bean1Info.getScope());
        assertFalse(bean1Info.isEager());
        List<ManagedBeanInfo.ManagedProperty> managedProperties =
              bean1Info.getManagedProperties();
        assertNotNull(managedProperties);
        assertTrue(managedProperties.size() == 2);
        ManagedBeanInfo.ManagedProperty p1 = managedProperties.get(0);
        assertEquals("silly", p1.getPropertyName());
        assertEquals(String.class.getName(), p1.getPropertyClass());
        assertEquals("#{requestScope.name}", p1.getPropertyValue());
        ManagedBeanInfo.ManagedProperty p2 = managedProperties.get(1);
        assertEquals("age", p2.getPropertyName());
        assertEquals(Integer.TYPE.getName(), p2.getPropertyClass());
        assertEquals("#{requestScope.age}", p2.getPropertyValue());
        HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
        request.setAttribute("name", "Bill");
        request.setAttribute("age", 33);
        AnnotatedBean bean1Instance =
              (AnnotatedBean) manager.create("annotatedBean", ctx);
        assertEquals("Bill", bean1Instance.getSilly());
        assertEquals(33, bean1Instance.getAge());
        assertNotNull(request.getAttribute("annotatedBean"));
        request.removeAttribute("annotatedBean");

        // custom scope
        ExpressionFactory factory = ctx.getApplication().getExpressionFactory();
        ValueExpression ve = factory.createValueExpression(ctx.getELContext(),
                                                           "#{customScopeAnnotatedBean.greeting}",
                                                           String.class);
        String greeting = (String) ve.getValue(ctx.getELContext());
        assertEquals("Hello", greeting);
        assertTrue(ctx.getExternalContext().getRequestMap().get("customScopeAnnotatedBean") instanceof CustomScopeAnnotatedBean);

        // validate inheritance
        ve = factory.createValueExpression(ctx.getELContext(),
                                           "#{baseBean}",
                                           Object.class);
        BaseBeanImplementation impl = (BaseBeanImplementation) ve.getValue(ctx.getELContext());
        assertEquals(20, impl.getAge());
        assertEquals("Bill", impl.getName());

    }

    private void testAnnotatedComponentsWebInfLib() throws Exception {

        FacesContext ctx = FacesContext.getCurrentInstance();
        Application app = ctx.getApplication();

        UIComponent c = app.createComponent("AnnotatedComponent2");
        assertNotNull(c);
        assertTrue(c.getClass().getName().endsWith("AnnotatedComponent2"));

        Converter cv = app.createConverter("AnnotatedConverter2");
        assertNotNull(cv);
        assertTrue(cv.getClass().getName().endsWith("AnnotatedConverter2"));


        Validator v = app.createValidator("AnnotatedValidator2");
        assertNotNull(v);
        assertTrue(v.getClass().getName().endsWith("AnnotatedValidator2"));
        Set<String> defaultValidatorIds = app.getDefaultValidatorInfo().keySet();
        assertFalse(defaultValidatorIds.contains("AnnotatedValidator2"));

        RenderKitFactory rkf = (RenderKitFactory) FactoryFinder
              .getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit rk =
              rkf.getRenderKit(ctx, RenderKitFactory.HTML_BASIC_RENDER_KIT);
        assertNotNull(rk);

        Renderer r = rk.getRenderer("AnnotatedRenderer2", "AnnotatedRenderer2");
        assertNotNull(r);
        assertTrue(r.getClass().getName().endsWith("AnnotatedRenderer2"));

        // Test default naming logic
        assertNotNull(ApplicationAssociate.getInstance(ctx.getExternalContext())
              .getNamedEventManager().getNamedEvent("com.sun.faces.annotation.annotatedComponentSystem"));
        // Test short name
        assertNotNull(ApplicationAssociate.getInstance(ctx.getExternalContext())
              .getNamedEventManager().getNamedEvent("com.sun.faces.annotation.anotherAnnotatedComponentSystem"));
        // Test FQCN
        assertNotNull(ApplicationAssociate.getInstance(ctx.getExternalContext())
              .getNamedEventManager().getNamedEvent(AnnotatedComponentSystemEvent.class.getName()));
        assertNotNull(ApplicationAssociate.getInstance(ctx.getExternalContext())
              .getNamedEventManager().getNamedEvent("explicitEventName"));

        Object bean = ctx.getApplication().evaluateExpressionGet(ctx,
                                                                 "#{annotatedBean4}",
                                                                 Object.class);
        assertNotNull(bean);
        assertEquals("com.sun.faces.annotation.AnnotatedBean4", bean.getClass().getName());

        // negative tests - if the jar files are metadata-complete, then their
        // annotated classes shouldn't be scanned/registered

        // faces-config is versioned at 2.0 and is marked metadata-complete
        bean = ctx.getApplication().evaluateExpressionGet(ctx,
                                                          "#{notFoundBean1}",
                                                          Object.class);
        assertTrue(bean == null);

        // faces-config is versioned at 1.2 which assumes metadata-complete
        bean = ctx.getApplication().evaluateExpressionGet(ctx,
                                                          "#{notFoundBean2}",
                                                          Object.class);
        assertTrue(bean == null);

    }

    private void assertNotNull(Object v) {
        if (v == null) {
            throw new RuntimeException();
        }
    }

    private void assertTrue(boolean t) {
        if (!t) {
            throw new RuntimeException();
        }
    }

    private void assertEquals(Object o1, Object o2) {
        if (o1 == null && o2 != null) {
            throw new RuntimeException();
        }
        if (o2 == null && o1 != null) {
            throw new RuntimeException();
        }
        if (o1 == null) {
            return;
        }
        if (!o1.equals(o2)) {
            throw new RuntimeException();
        }

    }

    private void assertFalse(boolean t) {
        if (t) {
            throw new RuntimeException();
        }
    }
}
