/*
 * $Id: ApplicationImpl.java,v 1.7 2003/04/29 20:51:30 eburns Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.application;

import java.util.Iterator;
import java.util.HashMap;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.MessageResources;
import javax.faces.convert.Converter;
import javax.faces.event.ActionListener;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ValueBinding;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.application.NavigationHandler;
import javax.faces.event.PhaseId;
import javax.faces.validator.Validator;
import javax.faces.FacesException;

import com.sun.faces.el.ValueBindingImpl;
import com.sun.faces.el.PropertyResolverImpl;
import com.sun.faces.el.VariableResolverImpl;
import com.sun.faces.config.ManagedBeanFactory;
import com.sun.faces.util.Util;

import org.mozilla.util.Assert;



/**
 * <p><strong>Application</strong> represents a per-web-application
 * singleton object where applications based on JavaServer Faces (or
 * implementations wishing to provide extended functionality) can
 * register application-wide singletons that provide functionality
 * required by JavaServer Faces.
 */
public class ApplicationImpl extends Application {

//
// Protected Constants
//

//
// Class Variables
//

// Attribute Instance Variables

    private ActionListener actionListener = null;
    private NavigationHandler navigationHandler = null;
    private PropertyResolver propertyResolver = null;
    private VariableResolver variableResolver = null;
    private HashMap valueBindingMap;

// Relationship Instance Variables

//
// Constructors and Initializers
//

    /**
     * Constructor 
     */
    public ApplicationImpl () {
        super();
        valueBindingMap = new HashMap();

        actionListener = new ActionListenerImpl();
    }

    /**
     * <p>Return the {@link ActionListener} that will be the default
     * {@link ActionListener} to be registered with relevant components
     * during the <em>Reconstitute Component Tree</em> phase of the
     * request processing lifecycle.
     */
    public ActionListener getActionListener() {
        return actionListener;
    }

    /**
     * <p>Replace the default {@link ActionListener} that will be registered
     * with relevant components during the <em>Reconstitute Component Tree</em>
     * phase of the requset processing lifecycle.  This
     * listener must return <code>PhaseId.INVOKE_APPLICATION</code> from its
     * <code>getPhaseId()</code> method.</p>
     *
     * @param listener The new {@link ActionListener}
     *
     * @exception IllegalArgumentException if the specified
     *	<code>listener</code> does not return
     *	<code>PhaseId.INVOKE_APPLICATION</code> from its
     *	<code>getPhaseId()</code> method
     * @exception NullPointerException if <code>listener</code>
     *	is <code>null</code>
     */
    public void setActionListener(ActionListener listener) {
        if (listener == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
        if (listener.getPhaseId() != PhaseId.INVOKE_APPLICATION) {
            throw new IllegalArgumentException(listener.getPhaseId().toString());
        }

        this.actionListener = listener;
    }

    /**
     * <p>Return the {@link NavigationHandler} instance that will be passed
     * the outcome returned by any invoked {@link Action} for this
     * web application.	 The default implementation must provide the behavior
     * described in the {@link NavigationHandler} class description.</p>
     */
    public NavigationHandler getNavigationHandler() {
        if (null == navigationHandler) {
            navigationHandler = new NavigationHandlerImpl();
        }
        return navigationHandler;
    }

    /**
     * <p>Set the {@link NavigationHandler} instance that will be passed
     * the outcome returned by any invoked {@link Action} for this
     * web application.</p>
     *
     * @param handler The new {@link NavigationHandler} instance
     *
     * @exception NullPointerException if <code>handler</code>
     *	is <code>null</code>
     */
    public void setNavigationHandler(NavigationHandler handler) {
        if (handler == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }

        this.navigationHandler = handler;
    }

    /**
     * <p>Return the {@link PropertyResolver} instance that will be utilized
     * to resolve action and valus references.	The default implementation
     * must provide the behavior described in the
     * {@link PropertyResolver} class description.</p>
     */
    public PropertyResolver getPropertyResolver() {
        if (null == propertyResolver) {
            propertyResolver = new PropertyResolverImpl();
        }
        return propertyResolver;
    }

    /**
     * <p>Set the {@link PropertyResolver} instance that will be utilized
     * to resolve action and value references.</p>
     *
     * @param resolver The new {@link PropertyResolver} instance
     *
     * @exception NullPointerException if <code>resolver</code>
     *	is <code>null</code>
     */
    public void setPropertyResolver(PropertyResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }

        this.propertyResolver = resolver;
    }

    /**
     * <p>Return a {@link ValueBinding} for the specified action or value
     * reference expression, which may be used to manipulate the corresponding
     * property value later.  The returned {@link ValueBinding} instance must
     * utilize the {@link PropertyResolver} and {@link VariableResolver}
     * instances registered with this {@link Application} instance at the
     * time that the {@link ValueBinding} instance was initially created.</p>
     *
     * <p>For maximum performance, implementations of {@link Application}
     * may, but are not required to, cache {@link ValueBinding} instances
     * in order to avoid repeated parsing of the reference expression.
     * However, under no circumstances may a particular {@link ValueBinding}
     * instance be shared across multiple web applications.</p>
     *
     * @param ref Reference expression for which to return a
     *	{@link ValueBinding} instance
     *
     * @exception NullPointerException if <code>ref</code>
     *	is <code>null</code>
     * @exception ReferenceSyntaxException if the specified <code>ref</code>
     *	has invalid syntax
     */
    public ValueBinding getValueBinding(String ref)
	throws ReferenceSyntaxException {
        if (ref == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }

        /* FIX_ME: check ref for valid syntax
        if (!isValidSyntax(ref)) {
            throw new ReferenceSyntaxException(ref));
        }
        */
        
        // Get ValueBinding from cache otherwise create new binding
        ValueBinding valueBinding;
        if (null == (valueBinding = (ValueBinding) valueBindingMap.get(ref))) {

            valueBinding = new ValueBindingImpl (this);
            ((ValueBindingImpl)valueBinding).setRef(ref);
            valueBindingMap.put(ref, valueBinding);
        }

        return valueBinding;
    }

    /**
     * <p>Return the {@link VariableResolver} instance that will be utilized
     * to resolve action and value references.	The default implementation
     * must provide the behavior described in the
     * {@link VariableResolver} class description.</p>
     */
    public VariableResolver getVariableResolver() {
        if (null == variableResolver) {
            variableResolver = new VariableResolverImpl();
        }
        return variableResolver;
    }

    /**
     * <p>Set the {@link VariableResolver} instance that will be utilized
     * to resolve action and value references.</p>
     *
     * @param resolver The new {@link VariableResolver} instance
     *
     * @exception NullPointerException if <code>resolver</code>
     *	is <code>null</code>
     */
    public void setVariableResolver(VariableResolver resolver) {
        if (resolver == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }

        this.variableResolver = resolver;
    }

    public void addComponent(String componentType, String componentClass) {
    }

    public UIComponent getComponent(String componentType) throws FacesException {
        return null;
    }

    public Iterator getComponentTypes() {
        return null;
    }

    public void addConverter(String converterId, String converterClass) {
    }

    public Converter getConverter(String converterId) throws FacesException {
        return null;
    }

    public Iterator getConverterIds() {
        return null;
    }

    public void addMessageResources(String messageResourcesId, String messageResourcesClass) {
    }

    public MessageResources getMessageResources(String messageResourcesId) 
        throws FacesException {
        return null;
    }

    public Iterator getMessageResourcesIds() { 
	return null;
    }

    public void addValidator(String validatorId, String validatorClass) {
    }

    public Validator getValidator(String validatorId) throws FacesException {
        return null;
    }

    public Iterator getValidatorIds() {
        return null;
    }


    private static final String APPLICATION = "application";
    private static final String SESSION = "session";
    private static final String REQUEST = "request";

    protected HashMap managedBeanFactories;

    /**
     * The ConfigFile managed has populated the managedBeanFactories
     * HashMap with ManagedBeanFactory object keyed by the bean name.
     * Find the ManagedBeanFactory object and if it exists instantiate
     * the bean and store it in the appropriate scope, if any.
     */
    public Object createAndMaybeStoreManagedBeans(FacesContext context,
        String managedBeanName) throws PropertyNotFoundException {

        ManagedBeanFactory managedBean = (ManagedBeanFactory) 
            managedBeanFactories.get(managedBeanName);
        if ( managedBean == null ) {
            return null;
        }
    
        Object bean;
        try {
            bean = managedBean.newInstance();
        } catch (Exception ex) {
            //FIX_ME: I18N error message
            throw new PropertyNotFoundException("Error instantiating bean", ex);
        }
        //add bean to appropriate scope
        String scope = managedBean.getScope();
        //scope cannot be null
        Assert.assert_it(null != scope);

        if (scope.equalsIgnoreCase(APPLICATION)) {
            context.getExternalContext().
                getApplicationMap().put(managedBeanName, bean);
        }
        else if (scope.equalsIgnoreCase(SESSION)) {
            context.getExternalContext().
                getSessionMap().put(managedBeanName, bean);
        }
        else if (scope.equalsIgnoreCase(REQUEST)) {
            context.getExternalContext().
                getRequestMap().put(managedBeanName, bean);
        }

        return bean;
    }

    /**
     * ConfigFiles manager populates the managedBeanFactories
     * HashMap with ManagedBeanFactory Objects.
     */
    public void addManagedBeanFactory(String managedBeanName,
                                      ManagedBeanFactory factory) {

        managedBeanFactories.put(managedBeanName, factory);
    }

    /**
     * Clear Hashmap when ConfigFile is reparsed.
     */
    public void clearManagedBeanFactories() {
        managedBeanFactories = new HashMap();
    }

}
