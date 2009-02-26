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

package javax.faces.application;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.validator.Validator;


/**
 * RELEASE_PENDING (edburns,rogerk) docs for class and all methods
 * @since 2.0
 */
public abstract class ApplicationWrapper extends Application implements FacesWrapper<Application> {

    public abstract Application getWrapped();

    @Override
    public ActionListener getActionListener() {
        return getWrapped().getActionListener();
    }

    @Override
    public void setActionListener(ActionListener listener) {
        getWrapped().setActionListener(listener);
    }

    @Override
    public Locale getDefaultLocale() {
        return getWrapped().getDefaultLocale();
    }

    @Override
    public void setDefaultLocale(Locale locale) {
        getWrapped().setDefaultLocale(locale);
    }

    @Override
    public String getDefaultRenderKitId() {
        return getWrapped().getDefaultRenderKitId();
    }

    @Override
    public void addDefaultValidatorId(String validatorId) {
        getWrapped().addDefaultValidatorId(validatorId);
    }

    @Override
    public Map<String, String> getDefaultValidatorInfo() {
        return getWrapped().getDefaultValidatorInfo();
    }

    @Override
    public void setDefaultRenderKitId(String renderKitId) {
        getWrapped().setDefaultRenderKitId(renderKitId);
    }

    @Override
    public String getMessageBundle() {
        return getWrapped().getMessageBundle();
    }

    @Override
    public void setMessageBundle(String bundle) {
        getWrapped().setMessageBundle(bundle);
    }

    @Override
    public NavigationHandler getNavigationHandler() {
        return getWrapped().getNavigationHandler();
    }

    @Override
    public void setNavigationHandler(NavigationHandler handler) {
        getWrapped().setNavigationHandler(handler);
    }

    @Override
    public PropertyResolver getPropertyResolver() {
        return getWrapped().getPropertyResolver();
    }

    @Override
    public void setPropertyResolver(PropertyResolver resolver) {
        getWrapped().setPropertyResolver(resolver);
    }

    @Override
    public VariableResolver getVariableResolver() {
        return getWrapped().getVariableResolver();
    }

    @Override
    public void setVariableResolver(VariableResolver resolver) {
        getWrapped().setVariableResolver(resolver);
    }

    @Override
    public ViewHandler getViewHandler() {
        return getWrapped().getViewHandler();
    }

    @Override
    public void setViewHandler(ViewHandler handler) {
        getWrapped().setViewHandler(handler);
    }

    @Override
    public StateManager getStateManager() {
        return getWrapped().getStateManager();
    }

    @Override
    public void setStateManager(StateManager manager) {
        getWrapped().setStateManager(manager);
    }

    @Override
    public void addComponent(String componentType, String componentClass) {
        getWrapped().addComponent(componentType, componentClass);
    }

    @Override
    public UIComponent createComponent(String componentType)
          throws FacesException {
        return getWrapped().createComponent(componentType);
    }

    @Override
    public UIComponent createComponent(ValueBinding componentBinding,
                                       FacesContext context,
                                       String componentType)
          throws FacesException {
        return getWrapped().createComponent(componentBinding,
                                            context,
                                            componentType);
    }

    @Override
    public Iterator<String> getComponentTypes() {
        return getWrapped().getComponentTypes();
    }

    @Override
    public void addConverter(String converterId, String converterClass) {
        getWrapped().addConverter(converterId, converterClass);
    }

    @Override
    public void addConverter(Class<?> targetClass, String converterClass) {
        getWrapped().addConverter(targetClass, converterClass);
    }

    @Override
    public Converter createConverter(String converterId) {
        return getWrapped().createConverter(converterId);
    }

    @Override
    public Converter createConverter(Class<?> targetClass) {
        return getWrapped().createConverter(targetClass);
    }

    @Override
    public Iterator<String> getConverterIds() {
        return getWrapped().getConverterIds();
    }

    @Override
    public Iterator<Class<?>> getConverterTypes() {
        return getWrapped().getConverterTypes();
    }

    @Override
    public MethodBinding createMethodBinding(String ref, Class<?>[] params)
          throws ReferenceSyntaxException {
        return getWrapped().createMethodBinding(ref, params);
    }

    @Override
    public Iterator<Locale> getSupportedLocales() {
        return getWrapped().getSupportedLocales();
    }

    @Override
    public void setSupportedLocales(Collection<Locale> locales) {
        getWrapped().setSupportedLocales(locales);
    }

    @Override
    public void addValidator(String validatorId, String validatorClass) {
        getWrapped().addValidator(validatorId, validatorClass);
    }

    @Override
    public Validator createValidator(String validatorId) throws FacesException {
        return getWrapped().createValidator(validatorId);
    }

    @Override
    public Iterator<String> getValidatorIds() {
        return getWrapped().getValidatorIds();
    }

    @Override
    public ValueBinding createValueBinding(String ref)
          throws ReferenceSyntaxException {
        return getWrapped().createValueBinding(ref);
    }

    @Override
    public ResourceHandler getResourceHandler() {
        return getWrapped().getResourceHandler();
    }

    @Override
    public void setResourceHandler(ResourceHandler resourceHandler) {
        getWrapped().setResourceHandler(resourceHandler);
    }

    @Override
    public ResourceBundle getResourceBundle(FacesContext ctx, String name) {
        return getWrapped().getResourceBundle(ctx, name);
    }

    @Override
    public ProjectStage getProjectStage() {
        return getWrapped().getProjectStage();
    }

    @Override
    public void addELResolver(ELResolver resolver) {
        getWrapped().addELResolver(resolver);
    }

    @Override
    public ELResolver getELResolver() {
        return getWrapped().getELResolver();
    }

    @Override
    public UIComponent createComponent(ValueExpression componentExpression,
                                       FacesContext context,
                                       String componentType)
          throws FacesException {
        return getWrapped().createComponent(componentExpression, context, componentType);
    }

    @Override
    public UIComponent createComponent(ValueExpression componentExpression,
                                       FacesContext context,
                                       String componentType,
                                       String rendererType) {
        return getWrapped().createComponent(componentExpression, context, componentType, rendererType);
    }

    @Override
    public UIComponent createComponent(FacesContext context,
                                       String componentType,
                                       String rendererType) {
        return getWrapped().createComponent(context, componentType, rendererType);
    }

    @Override
    public UIComponent createComponent(FacesContext context,
                                       Resource componentResource) {
        return getWrapped().createComponent(context, componentResource);
    }

    @Override
    public ExpressionFactory getExpressionFactory() {
        return getWrapped().getExpressionFactory();
    }

    @Override
    public <T> T evaluateExpressionGet(FacesContext context,
                                       String expression,
                                       Class<? extends T> expectedType)
          throws ELException {
        return getWrapped().evaluateExpressionGet(context, expression, expectedType);
    }

    @Override
    public void addELContextListener(ELContextListener listener) {
        getWrapped().addELContextListener(listener);
    }

    @Override
    public void removeELContextListener(ELContextListener listener) {
        getWrapped().removeELContextListener(listener);
    }

    @Override
    public ELContextListener[] getELContextListeners() {
        return getWrapped().getELContextListeners();
    }

    @Override
    public void publishEvent(Class<? extends SystemEvent> systemEventClass,
                             Object source) {
        getWrapped().publishEvent(systemEventClass, source);
    }

    @Override
    public void publishEvent(Class<? extends SystemEvent> systemEventClass,
                             Class<?> sourceBaseType,
                             Object source) {
        getWrapped().publishEvent(systemEventClass, sourceBaseType, source);
    }

    @Override
    public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass,
                                 Class<?> sourceClass,
                                 SystemEventListener listener) {
        getWrapped().subscribeToEvent(systemEventClass, sourceClass, listener);
    }

    @Override
    public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass,
                                 SystemEventListener listener) {
        getWrapped().subscribeToEvent(systemEventClass, listener);
    }

    @Override
    public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass,
                                     Class<?> sourceClass,
                                     SystemEventListener listener) {
        getWrapped().unsubscribeFromEvent(systemEventClass, sourceClass, listener);
    }

    @Override
    public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass,
                                     SystemEventListener listener) {
        getWrapped().unsubscribeFromEvent(systemEventClass, listener);
    }
}
