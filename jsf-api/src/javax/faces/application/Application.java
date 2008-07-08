/*
 * $Id: Application.java,v 1.59.2.10 2008/04/12 16:32:29 edburns Exp $
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

package javax.faces.application;


import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
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
import javax.faces.event.SystemEventListenerHolder;
import javax.faces.validator.Validator;


/**
 * <p><strong class="changed_modified_2_0">Application</strong>
 * represents a per-web-application singleton object where applications
 * based on JavaServer Faces (or implementations wishing to provide
 * extended functionality) can register application-wide singletons that
 * provide functionality required by JavaServer Faces.  Default
 * implementations of each object are provided for cases where the
 * application does not choose to customize the behavior.</p>
 *
 * <p>The instance of {@link Application} is created by calling the
 * <code>getApplication()</code> method of {@link ApplicationFactory}.
 * Because this instance is shared, it must be implemented in a
 * thread-safe manner.</p>
 *
 * <p>The application also acts as a factory for several types of
 * Objects specified in the Faces Configuration file.  Please see {@link
 * Application#createComponent}, {@link Application#createConverter},
 * and {@link Application#createValidator}. </p>
 */

public abstract class Application {

    private static final Logger LOGGER =
          Logger.getLogger("javax.faces.application", "javax.faces.LogStrings");

    @SuppressWarnings({"UnusedDeclaration"})
    private Application defaultApplication;

    // ------------------------------------------------------------- Properties


    /**
     * <p>Return the default {@link ActionListener} to be registered for
     * all {@link javax.faces.component.ActionSource} components in this
     * appication.  If not explicitly set, a default implementation must
     * be provided that performs the
     *
     * following functions:</p>
     * <ul>
     * <li>The <code>processAction()</code> method must first call
     *     <code>FacesContext.renderResponse()</code> in order to bypass
     *     any intervening lifecycle phases, once the method returns.</li>
     * <li>The <code>processAction()</code> method must next determine
     *     the logical outcome of this event, as follows:
     *     <ul>
     *     <li>If the originating component has a non-<code>null</code>
     *     <code>action</code> property, retrieve the {@link
     *     MethodBinding} from the property, and call
     *     <code>invoke()</code> on it.  Convert the returned value (if
     *     any) to a String, and use it as the logical outcome.</li>

     *     <li>Otherwise, the logical outcome is <code>null</code>.</li>
     *     </ul></li>

     * <li>The <code>processAction()</code> method must finally retrieve
     *     the <code>NavigationHandler</code> instance for this
     *     application and call {@link
     *     NavigationHandler#handleNavigation} passing: 
     *
     *     <ul>

     *     <li>the {@link FacesContext} for the current request</li>

     *     <li>If there is a <code>MethodBinding</code> instance for the
     *     <code>action</code> property of this component, the result of
     *     calling {@link MethodBinding#getExpressionString} on it, null
     *     otherwise</li>
     *
     *     <li>the logical outcome as determined above</li>
     *
     *     </ul>
     *
     *     </li>
     * </ul>
     *
     * <p>Note that the specification for the default
     * <code>ActionListener</code> contiues to call for the use of a
     * <strong>deprecated</strong> property (<code>action</code>) and
     * class (<code>MethodBinding</code>).  Unfortunately, this is
     * necessary because the default <code>ActionListener</code> must
     * continue to work with components that do not implement {@link
     * javax.faces.component.ActionSource2}, and only implement {@link
     * javax.faces.component.ActionSource}.</p>
     */
    public abstract ActionListener getActionListener();


    /**
     * <p>Set the default {@link ActionListener} to be registered for all
     * {@link javax.faces.component.ActionSource} components.</p>
     * </p>
     *
     * @param listener The new default {@link ActionListener}
     *
     * @throws NullPointerException if <code>listener</code>
     *  is <code>null</code>
     */
    public abstract void setActionListener(ActionListener listener);


    /**
     * <p>Return the default <code>Locale</code> for this application.  If
     * not explicitly set, <code>null</code> is returned.</p>
     */ 
    public abstract Locale getDefaultLocale();


    /**
     * <p>Set the default <code>Locale</code> for this application.</p>
     *
     * @param locale The new default <code>Locale</code>
     *
     * @throws NullPointerException if <code>locale</code>
     *  is <code>null</code>
     */
    public abstract void setDefaultLocale(Locale locale);


    /**
     * <p>Return the <code>renderKitId</code> to be used for rendering
     * this application.  If not explicitly set, <code>null</code> is
     * returned.</p>
     */
    public abstract String getDefaultRenderKitId();


    /**
     * <p>Set the <code>renderKitId</code> to be used to render this
     * application.  Unless the client has provided a custom {@link ViewHandler}
     * that supports the use of multiple {@link javax.faces.render.RenderKit}
     * instances in the same application, this method must only be called at
     * application startup, before any Faces requests have been processed.
     * This is a limitation of the current Specification, and may be lifted in
     * a future release.</p>
     */
    public abstract void setDefaultRenderKitId(String renderKitId);



    /**
     * <p>Return the fully qualified class name of the
     * <code>ResourceBundle</code> to be used for JavaServer Faces messages
     * for this application.  If not explicitly set, <code>null</code>
     * is returned.</p>
     */
    public abstract String getMessageBundle();


    /**
     * <p>Set the fully qualified class name of the <code>ResourceBundle</code>
     * to be used for JavaServer Faces messages for this application.  See the
     * JavaDocs for the <code>java.util.ResourceBundle</code> class for more
     * information about the syntax for resource bundle names.</p>
     *
     * @param bundle Base name of the resource bundle to be used
     *
     * @throws NullPointerException if <code>bundle</code>
     *  is <code>null</code>
     */
    public abstract void setMessageBundle(String bundle);


    /**
     * <p>Return the {@link NavigationHandler} instance that will be passed
     * the outcome returned by any invoked application action for this
     * web application.  If not explicitly set, a default implementation
     * must be provided that performs the functions described in the
     * {@link NavigationHandler} class description.</p>
     */
    public abstract NavigationHandler getNavigationHandler();


    /**
     * <p>Set the {@link NavigationHandler} instance that will be passed
     * the outcome returned by any invoked application action for this
     * web application.</p>
     *
     * @param handler The new {@link NavigationHandler} instance
     *
     * @throws NullPointerException if <code>handler</code>
     *  is <code>null</code>
     */
    public abstract void setNavigationHandler(NavigationHandler handler);



    /**
     * <p class="changed_added_2_0">Return the singleton, stateless, thread-safe {@link
     * ResourceHandler} for this application.  The JSF implementation
     * must support the following techniques for declaring an alternate
     * implementation of <code>ResourceHandler</code>.</p>
     * <div class="changed_added_2_0">
     * <ul>
     *    <li><p>The <code>ResourceHandler</code> implementation is
     *    declared in the application configuration resources by giving
     *    the fully qualified class name as the value of the
     *    <code>&lt;resource-handler&gt;</code> element within the
     *    <code>&lt;application&gt;</code> element.  </p></li>
     *    <li><p>RELEASE_PENDING(edburns) It can also be declared via an
     *    annotation as specified in [287-ConfigAnnotations].</p></li>
     * </ul>
     * <p>In all of the above cases, the runtime must employ the
     * decorator pattern as for every other pluggable artifact in
     * JSF.</p>
     * </div>
     * @since 2.0
     */
    public ResourceHandler getResourceHandler() {

        if (defaultApplication != null) {
            return defaultApplication.getResourceHandler();
        }

        throw new UnsupportedOperationException();
        
    }

    /**
     * <p class="changed_added_2_0">Set the {@link ResourceHandler} instance that will be utilized
     * for rendering the markup for resources, and for satisfying client
     * requests to serve up resources.</p>
     * <div class="changed_added_2_0">
     *
     * @param resourceHandler The new <code>ResourceHandler</code> instance
     *
     * @throws IllegalStateException if this method is called after
     * at least one request has been processed by the
     * <code>Lifecycle</code> instance for this application.
     * @throws NullPointerException if <code>resourceHandler</code>
     *  is <code>null</code>
     * </div>
     * @since 2.0
     */
    public void setResourceHandler(ResourceHandler resourceHandler) {

        if (defaultApplication != null) {
            defaultApplication.setResourceHandler(resourceHandler);
        }

        throw new UnsupportedOperationException();
        
    }


    /**
     * <p>Return a {@link PropertyResolver} instance that wraps the
     * {@link ELResolver} instance that Faces provides to the unified EL
     * for the resolution of expressions that appear programmatically in
     * an application.</p>
     *
     * <p>Note that this no longer returns the default
     * <code>PropertyResolver</code> since that class is now a no-op
     * that aids in allowing custom <code>PropertyResolver</code>s to
     * affect the EL resolution process.</p>
     *
     * @deprecated This has been replaced by {@link #getELResolver}.  
     */
    public abstract PropertyResolver getPropertyResolver();


    /**
     * <p>Set the {@link PropertyResolver} instance that will be utilized
     * to resolve method and value bindings.</p>
     *
     * <p>This method is now deprecated but the implementation must
     * cause the argument to be set as the head of the legacy
     * <code>PropertyResolver</code> chain, replacing any existing value
     * that was set from the application configuration resources.</p>
     *
     *  <p>It is illegal to call this method after
     * the application has received any requests from the client.  If an
     * attempt is made to register a listener after that time it must have
     * no effect. </p>
     *
     * @param resolver The new {@link PropertyResolver} instance
     *
     * @throws NullPointerException if <code>resolver</code>
     *  is <code>null</code>
     *
     * @deprecated The recommended way to affect the execution of the EL
     * is to provide an <code>&lt;el-resolver&gt;</code> element at the
     * right place in the application configuration resources which will
     * be considered in the normal course of expression evaluation.
     * This method now will cause the argument <code>resolver</code> to
     * be wrapped inside an implementation of {@link ELResolver} and
     * exposed to the EL resolution system as if the user had called
     * {@link #addELResolver}.
     *
     * @throws IllegalStateException if called after the first
     * request to the {@link javax.faces.webapp.FacesServlet} has been
     * serviced.
     */
    public abstract void setPropertyResolver(PropertyResolver resolver);
    
    /**
     * <p>Find a <code>ResourceBundle</code> as defined in the
     * application configuration resources under the specified name.  If
     * a <code>ResourceBundle</code> was defined for the name, return an
     * instance that uses the locale of the current {@link
     * javax.faces.component.UIViewRoot}.</p>
     *
     * <p>The default implementation throws 
     * <code>UnsupportedOperationException</code> and is provided
     * for the sole purpose of not breaking existing applications that extend
     * this class.</p>
     *
     * @return <code>ResourceBundle</code> for the current UIViewRoot,
     * otherwise null
     *
     * @throws FacesException if a bundle was defined, but not resolvable
     *
     * @throws NullPointerException if ctx == null || name == null
     *
     * @since 1.2
     */
    
    public ResourceBundle getResourceBundle(FacesContext ctx, String name) {

       if (defaultApplication != null) {
            return defaultApplication.getResourceBundle(ctx, name);
        }
        
        throw new UnsupportedOperationException();

    }


    /**
     * <p class="changed_added_2_0">Return the project stage
     * for the currently running application instance.  The default
     * value is {@link ProjectStage#Production}</p> 

     * <div class="changed_added_2_0"> <p>The implementation of this
     * method must perform the following algorithm or an equivalent with
     * the same end result to determine the value to return.</p> <ul>
     *
     * <p>If the value has already been determined by a previous call to
     * this method, simply return that value.</p>

     * <p>Look for a <code>JNDI</code> environment entry under the key
     * given by the value of {@link
     * ProjectStage#PROJECT_STAGE_JNDI_NAME} (return type of
     * <code>java.lang.String</code>).  If found, continue with the
     * algorithm below, otherwise, look for an entry in the
     * <code>initParamMap</code> of the <code>ExternalContext</code>
     * from the current <code>FacesContext</code> with the key given by
     * the value of {@link ProjectStage#PROJECT_STAGE_PARAM_NAME}

     * </p>
     *
     * <p>If a value is found, see if an enum constant can be
     * obtained by calling <code>ProjectStage.valueOf()</code>, passing
     * the value from the <code>initParamMap</code>.  If this succeeds
     * without exception, save the value and return it.</p>
     *
     * <p>If not found, or any of the previous attempts to discover the
     * enum constant value have failed, log a descriptive error message,
     * assign the value as <code>ProjectStage.Production</code> and
     * return it.</p>
     *
     * </ul>
     * </div>
     *
     * @since 2.0
     */
    public ProjectStage getProjectStage() {
        
        if (defaultApplication != null) {
            return defaultApplication.getProjectStage();
        }
        
        return ProjectStage.Production;
    }


    /**
     * <p>Return the {@link VariableResolver} that wraps the {@link
     * ELResolver} instance that Faces provides to the unified EL for
     * the resolution of expressions that appear programmatically in an
     * application.  The implementation of the
     * <code>VariableResolver</code>must pass <code>null</code> as the
     * base argument for any methods invoked on the underlying
     * <code>ELResolver</code>.</p>
     *
     * <p>Note that this method no longer returns the default
     * <code>VariableResolver</code>, since that class now is a no-op
     * that aids in allowing custom <code>VariableResolver</code>s to
     * affect the EL resolution process.</p>
     *
     * @deprecated This has been replaced by {@link #getELResolver}.  
     */
    public abstract VariableResolver getVariableResolver();


    /**
     * <p>Set the {@link VariableResolver} instance that will be consulted
     * to resolve method and value bindings.</p>
     *
     * <p>This method is now deprecated but the implementation must
     * cause the argument to be set as the head of the legacy
     * <code>VariableResolver</code> chain, replacing any existing value
     * that was set from the application configuration resources.</p>
     *
     *  <p>It is illegal to call this method after
     * the application has received any requests from the client.  If an
     * attempt is made to register a listener after that time it must have 
     * no effect.</p>
     *
     * @param resolver The new {@link VariableResolver} instance
     *
     * @throws NullPointerException if <code>resolver</code>
     *  is <code>null</code>
     *
     * @deprecated The recommended way to affect the execution of the EL
     * is to provide an <code>&lt;el-resolver&gt;</code> element at the
     *
     * right place in the application configuration resources which will
     * be considered in the normal course of expression evaluation.
     * This method now will cause the argument <code>resolver</code> to
     * be wrapped inside an implementation of {@link ELResolver} and
     * exposed to the EL resolution system as if the user had called
     * {@link #addELResolver}.
     *
     * @throws IllegalStateException if called after the first
     * request to the {@link javax.faces.webapp.FacesServlet} has been
     * serviced.
     */
    public abstract void setVariableResolver(VariableResolver resolver);

    /**
     * <p>Cause an the argument <code>resolver</code> to be added to the
     * resolver chain as specified in section 5.5.1 of the JavaServer
     * Faces Specification.</p>
     *
     * <p>It is not possible to remove an <code>ELResolver</code>
     * registered with this method, once it has been registered.</p>
     *
     *  <p>It is illegal to register an <code>ELResolver</code> after
     * the application has received any requests from the client.  If an
     * attempt is made to register a listener after that time, an
     * <code>IllegalStateException</code> must be thrown. This restriction is
     * in place to allow the JSP container to optimize for the common
     * case where no additional <code>ELResolver</code>s are in the
     * chain, aside from the standard ones. It is permissible to add
     * <code>ELResolver</code>s before or after initialization to a
     * <code>CompositeELResolver</code> that is already in the
     * chain.</p>
     *
     * <p>The default implementation throws 
     * <code>UnsupportedOperationException</code> and is provided
     * for the sole purpose of not breaking existing applications that extend
     * {@link Application}.</p>
     *
     * @since 1.2
     */

    public void addELResolver(ELResolver resolver) {

        if (defaultApplication != null) {
            defaultApplication.addELResolver(resolver);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>Return the singleton {@link ELResolver} instance to be used
     * for all EL resolution.  This is actually an instance of {@link
     * javax.el.CompositeELResolver} that must contain the following
     * <code>ELResolver</code> instances in the following order:</p>
     *
     * 	<ol>
     *
     *	  <li><p><code>ELResolver</code> instances declared using the
     *	  &lt;el-resolver&gt; element in the application configuration
     *	  resources.  </p></li>
     *
     *	  <li><p>An <code>implementation</code> that wraps the head of
     *	  the legacy <code>VariableResolver</code> chain, as per section
     *	  <i>VariableResolver ChainWrapper</i> in Chapter 5 in the spec
     *	  document.</p></li>
     *
     *	  <li><p>An <code>implementation</code> that wraps the head of
     *	  the legacy <code>PropertyResolver</code> chain, as per section
     *	  <i>PropertyResolver ChainWrapper</i> in Chapter 5 in the spec
     *	  document.</p></li>
     *
     *	  <li><p>Any <code>ELResolver</code> instances added by calls to
     *	  {@link #addELResolver}.</p></li>
     *
     *	</ol>
     *
     * <p>The default implementation throws <code>UnsupportedOperationException</code>
     * and is provided for the sole purpose of not breaking existing applications 
     * that extend {@link Application}.</p>
     *
     * @since 1.2
     */

    public ELResolver getELResolver() {

        if (defaultApplication != null) {
            //noinspection TailRecursion
            return defaultApplication.getELResolver();
        }
        throw new UnsupportedOperationException();

    }


    /**
     * <p>Return the {@link ViewHandler} instance that will be utilized
     * during the <em>Restore View</em> and <em>Render Response</em>
     * phases of the request processing lifecycle.  If not explicitly set,
     * a default implementation must be provided that performs the functions
     * described in the {@link ViewHandler} description in the
     * JavaServer Faces Specification.</p>
     */
    public abstract ViewHandler getViewHandler();


    /**
     * <p>Set the {@link ViewHandler} instance that will be utilized
     * during the <em>Restore View</em> and <em>Render Response</em>
     * phases of the request processing lifecycle.</p>
     *
     * @param handler The new {@link ViewHandler} instance
     *
     * @throws IllegalStateException if this method is called after
     * at least one request has been processed by the
     * <code>Lifecycle</code> instance for this application.
     * @throws NullPointerException if <code>handler</code>
     *  is <code>null</code>
     */
    public abstract void setViewHandler(ViewHandler handler);



    /**
     * <p>Return the {@link StateManager} instance that will be utilized
     * during the <em>Restore View</em> and <em>Render Response</em>
     * phases of the request processing lifecycle.  If not explicitly set,
     * a default implementation must be provided that performs the functions
     * described in the {@link StateManager} description
     * in the JavaServer Faces Specification.</p>
     */
    public abstract StateManager getStateManager();


    /**
     * <p>Set the {@link StateManager} instance that will be utilized
     * during the <em>Restore View</em> and <em>Render Response</em>
     * phases of the request processing lifecycle.</p>
     *
     * @param manager The new {@link StateManager} instance
     *
     * @throws IllegalStateException if this method is called after
     * at least one request has been processed by the
     * <code>Lifecycle</code> instance for this application.
     * @throws NullPointerException if <code>manager</code>
     *  is <code>null</code>
     */
    public abstract void setStateManager(StateManager manager);


    // ------------------------------------------------------- Object Factories


    /**
     * <p>Register a new mapping of component type to the name of the
     * corresponding {@link UIComponent} class.  This allows subsequent calls
     * to <code>createComponent()</code> to serve as a factory for
     * {@link UIComponent} instances.</p>
     *
     * @param componentType The component type to be registered
     * @param componentClass The fully qualified class name of the
     *  corresponding {@link UIComponent} implementation
     *
     * @throws NullPointerException if <code>componentType</code> or
     *  <code>componentClass</code> is <code>null</code>
     */
    public abstract void addComponent(String componentType,
                                      String componentClass);


    /**
     * <p>Instantiate and return a new {@link UIComponent} instance of the
     * class specified by a previous call to <code>addComponent()</code> for
     * the specified component type.</p>
     *
     * RELEASE_PENDING (edburns, rogerk) The javadocs for ListenerFor state that the
     * factory responsible for or acting upon an artifact that must take this annotation
     * into account will take the appropriate action. Seems like the documentation is circular
     * and should be clarified either here, or in ListenFor
     *
     * RELEASE_PENDING (edburns, rogerk) this method will process @ResourceDependency
     * *and* @ListenerFor annotations (and their plural equivalents).
     * No action is taken on the Renderer unless createComponent() that takes
     * an additional rendererType argument is called.
     *
     * <p class="changed_added_2_0">Before the component instance is
     * returned, it must be inspected for the presence of a {@link
     * javax.faces.event.ListenerFor} annotation.  If this annotation is present,
     * the action listed in {@link javax.faces.event.ListenerFor} must be taken on
     * the component, before it is returned from this method.</p>
     * 
     * @param componentType The component type for which to create and
     *  return a new {@link UIComponent} instance
     *
     * @throws FacesException if a {@link UIComponent} of the
     *  specified type cannot be created
     * @throws NullPointerException if <code>componentType</code>
     *  is <code>null</code>
     */ 
    public abstract UIComponent createComponent(String componentType)
        throws FacesException;


    /**
     * <p>Wrap the argument <code>componentBinding</code> in an
     * implementation of {@link ValueExpression} and call through to
     * {@link
     * #createComponent(javax.el.ValueExpression,javax.faces.context.FacesContext,java.lang.String)}.</p>
     *
     * RELEASE_PENDING (edburns,rogerk) do we really want to add new functionality
     * to a deperecated method.  I don't think we do.
     *
     * <p class="changed_added_2_0">Before the component instance is
     * returned, it must be inspected for the presence of a {@link
     * javax.faces.event.ListenerFor} annotation.  If this annotation is present,
     * the action listed in {@link javax.faces.event.ListenerFor} must be taken on
     * the component, before it is returned from this method.</p>
     * 
     * @param componentBinding {@link ValueBinding} representing a
     * component value binding expression (typically specified by the
     * <code>component</code> attribute of a custom tag)
     * @param context {@link FacesContext} for the current request
     * @param componentType Component type to create if the {@link ValueBinding}
     *  does not return a component instance
     *
     * @throws FacesException if a {@link UIComponent} cannot be created
     * @throws NullPointerException if any parameter is <code>null</code>
     *
     *
     * @deprecated This has been replaced by {@link
     * #createComponent(javax.el.ValueExpression,javax.faces.context.FacesContext,java.lang.String)}.
     */
    public abstract UIComponent createComponent(ValueBinding componentBinding,
                                                FacesContext context,
                                                String componentType)
	throws FacesException;

    /**
     * <p>Call the <code>getValue()</code> method on the specified
     * {@link ValueExpression}.  If it returns a {@link
     * UIComponent} instance, return it as the value of this method.  If
     * it does not, instantiate a new {@link UIComponent} instance of
     * the specified component type, pass the new component to the
     * <code>setValue()</code> method of the specified {@link
     * ValueExpression}, and return it.</p>
     *
     * @param componentExpression {@link ValueExpression} representing a
     * component value expression (typically specified by the
     * <code>component</code> attribute of a custom tag)
     * @param context {@link FacesContext} for the current request
     * @param componentType Component type to create if the {@link
     * ValueExpression} does not return a component instance
     *
     * RELEASE_PENDING (edburns, rogerk) The javadocs for ListenerFor state that the
     * factory responsible for or acting upon an artifact that must take this annotation
     * into account will take the appropriate action. Seems like the documentation is circular
     * and should be clarified either here, or in ListenFor
     *
     * RELEASE_PENDING (edburns, rogerk) this method will process @ResourceDependency
     * *and* @ListenerFor annotations (and their plural equivalents).
     * No action is taken on the Renderer unless createComponent() that takes
     * an additional rendererType argument is called.
     *
     * <p class="changed_added_2_0">Before the component instance is
     * returned, it must be inspected for the presence of a {@link
     * javax.faces.event.ListenerFor} annotation.  If this annotation is present,
     * the action listed in {@link javax.faces.event.ListenerFor} must be taken on
     * the component, before it is returned from this method.</p>
     * 
     * @throws FacesException if a {@link UIComponent} cannot be created
     * @throws NullPointerException if any parameter is <code>null</code>
     *
     * <p>A default implementation is provided that throws 
     * <code>UnsupportedOperationException</code> so that users
     * that decorate <code>Application</code> can continue to function</p>.
     * 
     * @since 1.2
     */
    public UIComponent createComponent(ValueExpression componentExpression,
                                       FacesContext context,
                                       String componentType)
    throws FacesException {

        if (defaultApplication != null) {
            return defaultApplication.createComponent(componentExpression,
                                                      context,
                                                      componentType);
        }

        throw new UnsupportedOperationException();

    }


    /**
     * RELEASE_PENDING (edburs,rogerk) docs
     * @param componentExpression
     * @param context
     * @param componentType
     * @param rendererType
     * @return
     * @throws FacesException
     * @since 2.0
     */
    public UIComponent createComponent(ValueExpression componentExpression,
                                       FacesContext context,
                                       String componentType,
                                       String rendererType) {

        if (defaultApplication != null) {
            return defaultApplication.createComponent(componentExpression,
                                                      context,
                                                      componentType,
                                                      rendererType);
        }

        throw new UnsupportedOperationException();

    }


    /**
     * RELEASE_PENDING (edburns,rogerk) docs
     * @param componentType
     * @param rendererType
     * @return
     * @throws FacesException
     * @since 2.0
     */
     public UIComponent createComponent(FacesContext context,
                                        String componentType,
                                        String rendererType) {

        if (defaultApplication != null) {
            defaultApplication.createComponent(context,
                                               componentType,
                                               rendererType);
        }

        throw new UnsupportedOperationException();

     }


    /**
     * <p>Return an <code>Iterator</code> over the set of currently defined
     * component types for this <code>Application</code>.</p>
     */
    public abstract Iterator<String> getComponentTypes();


    /**
     * <p>Register a new mapping of converter id to the name of the
     * corresponding {@link Converter} class.  This allows subsequent calls
     * to <code>createConverter()</code> to serve as a factory for
     * {@link Converter} instances.</p>
     *
     * @param converterId The converter id to be registered
     * @param converterClass The fully qualified class name of the
     *  corresponding {@link Converter} implementation
     *
     * @throws NullPointerException if <code>converterId</code>
     *  or <code>converterClass</code> is <code>null</code>
     */
    public abstract void addConverter(String converterId, 
				      String converterClass);


    /**
     * <p>Register a new converter class that is capable of performing
     * conversions for the specified target class.</p>
     *
     * @param targetClass The class for which this converter is registered
     * @param converterClass The fully qualified class name of the
     *  corresponding {@link Converter} implementation
     *
     * @throws NullPointerException if <code>targetClass</code>
     *  or <code>converterClass</code> is <code>null</code>
     */
    public abstract void addConverter(Class targetClass,
                                      String converterClass);


    /**
     * <p>Instantiate and return a new {@link Converter} instance of the
     * class specified by a previous call to <code>addConverter()</code>
     * for the specified converter id.  If there is no such registration
     * for this converter id, return <code>null</code>.</p>
     *
     * @param converterId The converter id for which to create and
     *  return a new {@link Converter} instance
     *
     * @throws FacesException if the {@link Converter} cannot be
     *  created
     * @throws NullPointerException if <code>converterId</code>
     *  is <code>null</code>
     */ 
    public abstract Converter createConverter(String converterId);


    /**
     * <p>Instantiate and return a new {@link Converter} instance of the
     * class that has registered itself as capable of performing conversions
     * for objects of the specified type.  If no such {@link Converter} class
     * can be identified, return <code>null</code>.</p>
     *
     * <p>To locate an appropriate {@link Converter} class, the following
     * algorithm is performed, stopping as soon as an appropriate {@link
     * Converter} class is found:</p>
     * <ul>
     * <li>Locate a {@link Converter} registered for the target class itself.
     *     </li>
     * <li>Locate a {@link Converter} registered for interfaces that are
     *     implemented by the target class (directly or indirectly).</li>
     * <li>Locate a {@link Converter} registered for the superclass (if any)
     *     of the target class, recursively working up the inheritance
     *     hierarchy.</li>
     * </ul>
     *
     * <p>If the <code>Converter</code> has a single argument constructor that
     * accepts a <code>Class</code>, instantiate the <code>Converter</code>
     * using that constructor, passing the argument <code>targetClass</code> as
     * the sole argument.  Otherwise, simply use the zero-argument constructor.
     * </p>
     *
     * @param targetClass Target class for which to return a {@link Converter}
     *
     * @throws FacesException if the {@link Converter} cannot be
     *  created
     * @throws NullPointerException if <code>targetClass</code>
     *  is <code>null</code>
     */
    public abstract Converter createConverter(Class targetClass);


    /**
     * <p>Return an <code>Iterator</code> over the set of currently registered
     * converter ids for this <code>Application</code>.</p>
     */
    public abstract Iterator<String> getConverterIds();

    
    /**
     * <p>Return an <code>Iterator</code> over the set of <code>Class</code>
     * instances for which {@link Converter} classes have been explicitly
     * registered.</p>
     */
    public abstract Iterator<Class> getConverterTypes();

    /**
     * <p>Return the {@link ExpressionFactory} instance for this
     * application.  This instance is used by the convenience method
     * {@link #evaluateExpressionGet}.</p>
     *
     * <p>The implementation must return the
     * <code>ExpressionFactory</code> from the JSP container by calling
     * <code>JspFactory.getDefaultFactory().getJspApplicationContext(servletContext).getExpressionFactory()</code>. </p>
     *
     * <p>An implementation is provided that throws 
     * <code>UnsupportedOperationException</code> so that users that decorate
     * the <code>Application</code> continue to work.
     *
     * @since 1.2
     */

    public ExpressionFactory getExpressionFactory() {

        if (defaultApplication != null) {
            return defaultApplication.getExpressionFactory();
        }

        throw new UnsupportedOperationException();

    }

    /**
     * <p>Get a value by evaluating an expression.</p>
     *
     * <p>Call {@link #getExpressionFactory} then call {@link
     * ExpressionFactory#createValueExpression} passing the argument
     * <code>expression</code> and <code>expectedType</code>.  Call
     * {@link FacesContext#getELContext} and pass it to {@link
     * ValueExpression#getValue}, returning the result.</p>
     *
     * <p>An implementation is provided that throws 
     * <code>UnsupportedOperationException</code> so that users that decorate
     * the <code>Application</code> continue to work.
     *
     */

    public Object evaluateExpressionGet(FacesContext context,
                                        String expression,
                                        Class expectedType) throws ELException {

        if (defaultApplication != null) {
            return defaultApplication.evaluateExpressionGet(context,
                                                            expression,
                                                            expectedType);
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p>Call {@link #getExpressionFactory} then call {@link
     * ExpressionFactory#createMethodExpression}, passing the given
     * arguments, and wrap the result in a <code>MethodBinding</code>
     * implementation, returning it.</p>
     *
     * @param ref Method binding expression for which to return a
     *  {@link MethodBinding} instance
     * @param params Parameter signatures that must be compatible with those
     *  of the method to be invoked, or a zero-length array or <code>null</code>
     *  for a method that takes no parameters
     *
     * @throws NullPointerException if <code>ref</code>
     *  is <code>null</code>
     * @throws ReferenceSyntaxException if the specified <code>ref</code>
     *  has invalid syntax
     *
     * @deprecated This has been replaced by calling {@link
     * #getExpressionFactory} then {@link
     * ExpressionFactory#createMethodExpression}.
     */
    public abstract MethodBinding createMethodBinding(String ref,
                                                      Class params[])
        throws ReferenceSyntaxException;


    /**
     * <p>Return an <code>Iterator</code> over the supported
     * <code>Locale</code>s for this appication.</p>
     */ 
    public abstract Iterator<Locale> getSupportedLocales();


    /**
     * <p>Set the <code>Locale</code> instances representing the supported
     * <code>Locale</code>s for this application.</p>
     *
     * @param locales The set of supported <code>Locale</code>s
     *  for this application
     *
     * @throws NullPointerException if the argument
     * <code>newLocales</code> is <code>null</code>.
     *
     */ 
    public abstract void setSupportedLocales(Collection<Locale> locales);

    /**
     * <p>Provide a way for Faces applications to register an
     * <code>ELContextListener</code> that will be notified on creation
     * of <code>ELContext</code> instances.  This listener will be
     * called once per request.</p>
     * 
     * <p>An implementation is provided that throws 
     * <code>UnsupportedOperationException</code> so that users that decorate
     * the <code>Application</code> continue to work.
     *
     * @since 1.2
     */

    public void addELContextListener(ELContextListener listener) {

        if (defaultApplication != null) {
            defaultApplication.addELContextListener(listener);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>Remove the argument <code>listener</code> from the list of
     * {@link ELContextListener}s.  If <code>listener</code> is null, no
     * exception is thrown and no action is performed.  If
     * <code>listener</code> is not in the list, no exception is thrown
     * and no action is performed.</p>
     *
     * <p>An implementation is provided that throws 
     * <code>UnsupportedOperationException</code> so that users that decorate
     * the <code>Application</code> continue to work.
     * 
     * @since 1.2
     */

    public void removeELContextListener(ELContextListener listener) {

        if (defaultApplication != null) {
            defaultApplication.removeELContextListener(listener);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>If no calls have been made to {@link #addELContextListener},
     * this method must return an empty array.</p>
     *
     * <p>Otherwise, return an array representing the list of listeners
     * added by calls to {@link #addELContextListener}.</p>
     *
     * <p>An implementation is provided that throws 
     * <code>UnsupportedOperationException</code> so that users that decorate
     * the <code>Application</code> continue to work.
     *
     * @since 1.2
     */

    public ELContextListener [] getELContextListeners() {

        if (defaultApplication != null) {
            //noinspection TailRecursion
            return defaultApplication.getELContextListeners();
        } else {
            throw new UnsupportedOperationException();
        }

    }


    /**
     * <p>Register a new mapping of validator id to the name of the
     * corresponding {@link Validator} class.  This allows subsequent calls
     * to <code>createValidator()</code> to serve as a factory for
     * {@link Validator} instances.</p>
     *
     * @param validatorId The validator id to be registered
     * @param validatorClass The fully qualified class name of the
     *  corresponding {@link Validator} implementation
     *
     * @throws NullPointerException if <code>validatorId</code>
     *  or <code>validatorClass</code> is <code>null</code>
     */
    public abstract void addValidator(String validatorId, 
				      String validatorClass);


    /**
     * <p>Instantiate and return a new {@link Validator} instance of the
     * class specified by a previous call to <code>addValidator()</code>
     * for the specified validator id.</p>
     *
     * @param validatorId The validator id for which to create and
     *  return a new {@link Validator} instance
     *
     * @throws FacesException if a {@link Validator} of the
     *  specified id cannot be created
     * @throws NullPointerException if <code>validatorId</code>
     *  is <code>null</code>
     */ 
    public abstract Validator createValidator(String validatorId)
        throws FacesException;


    /**
     * <p>Return an <code>Iterator</code> over the set of currently registered
     * validator ids for this <code>Application</code>.</p>
     */
    public abstract Iterator<String> getValidatorIds();


    /**
     * <p>Call {@link #getExpressionFactory} then call {@link
     * ExpressionFactory#createValueExpression}, passing the argument
     * <code>ref</code>, <code>Object.class</code> for the expectedType,
     * and <code>null</code>, for the fnMapper.</p>
     *
     *
     * @param ref Value binding expression for which to return a
     *  {@link ValueBinding} instance
     *
     * @throws NullPointerException if <code>ref</code>
     *  is <code>null</code>
     * @throws ReferenceSyntaxException if the specified <code>ref</code>
     *  has invalid syntax
     *
     * @deprecated This has been replaced by calling {@link
     * #getExpressionFactory} then {@link
     * ExpressionFactory#createValueExpression}.
     */
    public abstract ValueBinding createValueBinding(String ref)
        throws ReferenceSyntaxException;


    /**
     * <p class="changed_added_2_0">If there are one or more listeners
     * for events of the type represented by
     * <code>systemEventClass</code>, call those listeners, passing
     * <code>source</code> as the source of the event.  The
     * implementation should be as fast as possible in determining
     * whether or not a listener for the given
     * <code>systemEventClass</code> and <code>source</code> has been
     * installed, and should return immediately once such a
     * determination has been made.  The implementation of
     * <code>publishEvent</code> must honor the requirements stated in
     * {@link #subscribeToEvent} regarding the storage and retrieval of
     * listener instances.</p>
     *
     * <div class="changed_added_2_0">
     *
     * <p>The default implementation must implement an algorithm
     * semantically equivalent to the following to locate listener
     * instances and to invoke them.</p>
     *
     * 	<ul>
     *
     * <li><p>If the <code>source</code> argument implements {@link
     * javax.faces.event.SystemEventListenerHolder}, call {@link
     * javax.faces.event.SystemEventListenerHolder#getListenersForEventClass}
     * on it, passing the <code>systemEventClass</code> argument.  If
     * the list is not empty, perform algorithm
     * <em>traverseListenerList</em> on the list.</p></li>
     *
     * <li><p>If any <code>Application</code> level listeners have
     * been installed by previous calls to {@link
     * #subscribeToEvent(Class, Class,
     *     javax.faces.event.SystemEventListener)}, perform algorithm
     * <em>traverseListenerList</em> on the list.</p></li>
     *
     * <li><p>If any <code>Application</code> level listeners have
     * been installed by previous calls to {@link
     * #subscribeToEvent(Class, javax.faces.event.SystemEventListener)},
     * perform algorithm <em>traverseListenerList</em> on the
     * list.</p></li>
     *
     * </ul>
     *
     * <p>If the act of invoking the <code>processListener</code> method
     * causes an {@link javax.faces.event.AbortProcessingException} to
     * be thrown, processing of the listeners must be aborted.</p>
     *
     * RELEASE_PENDING (edburns,rogerk) it may be prudent to specify how the
     * abortprocessingexception should be handled.  Logged or thrown?
     *
     * <p>Algorithm <em>traverseListenerList</em>: For each listener in
     * the list,</p>
     *
     * <ul>
     *
     * <li><p>Call {@link
     * javax.faces.event.SystemEventListener#isListenerForSource}, passing the
     * <code>source</code> argument.  If this returns
     * <code>false</code>, take no action on the listener.</p></li>
     *
     * <li><p>Otherwise, if the event to be passed to the listener
     * instances has not yet been constructed, construct the event,
     * passing <code>source</code> as the argument to the
     * one-argument constructor that takes an <code>Object</code>.
     * This same event instance must be passed to all listener
     * instances.</p></li>
     *
     * <li><p>Call {@link javax.faces.event.SystemEvent#isAppropriateListener},
     * passing the listener instance as the argument.  If this
     * returns <code>false</code>, take no action on the
     * listener.</p></li>
     *
     * <li><p>Call {@link javax.faces.event.SystemEvent#processListener},
     * passing the listener instance.  </p></li>
     *
     * </ul>
     * </div>
     *
     * @param systemEventClass The <code>Class</code> of event that is
     * being published.
     * @param source The source for the event of type
     * <code>systemEventClass</code>.
     *
     * @throws NullPointerException if either <code>systemEventClass</code> or
     *  <code>source</code> is <code>null</code>
     *
     * @since 2.0
     */
    public void publishEvent(Class<? extends SystemEvent> systemEventClass,
                                      SystemEventListenerHolder source) {

        if (defaultApplication != null) {
            defaultApplication.publishEvent(systemEventClass, source);
        }

        throw new UnsupportedOperationException();

    }


    /**
     * <p class="changed_added_2_0">Install the listener instance
     * referenced by argument <code>listener</code> into the
     * application as a listener for events of type
     * <code>systemEventClass</code> that originate from objects of type
     * <code>sourceClass</code>.</p>
     *
     * <div class="changed_added_2_0">
     *
     * <p>If argument <code>sourceClass</code> is non-<code>null</code>,
     * <code>sourceClass</code> and <code>systemEventClass</code> must be
     * used to store the argument <code>listener</code> in the application in
     * such a way that the <code>listener</code> can be quickly looked
     * up by the implementation of {@link #publishEvent} given
     * <code>systemEventClass</code> and an instance of the
     * <code>Class</code> referenced by <code>sourceClass</code>.  If
     * argument <code>sourceClass</code> is <code>null</code>, the
     * <code>listener</code> must be discoverable by the implementation
     * of {@link #publishEvent} given only <code>systemEventClass</code>.
     * </p>
     *
     * </div>
     *
     * @param systemEventClass the <code>Class</code> of event for which
     * <code>listener</code> must be fired.
     *
     * @param sourceClass the <code>Class</code> of the instance which
     * causes events of type <code>systemEventClass</code> to be fired.
     * May be <code>null</code>.
     *
     * @param listener the implementation of {@link
     * javax.faces.event.SystemEventListener} whose {@link
     * javax.faces.event.SystemEventListener#processEvent} method must be called when
     * events of type <code>systemEventClass</code> are fired.
     *
     * @throws <code>NullPointerException</code> if any combination of
     * <code>systemEventClass</code>, or <code>listener</code> are
     * <code>null</code>.
     *
     * @since 2.0
     */
    public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass,
                                 Class sourceClass,
                                 SystemEventListener listener) {

        if (defaultApplication != null) {
            defaultApplication.subscribeToEvent(systemEventClass,
                                                sourceClass,
                                                listener);
        }

        throw new UnsupportedOperationException();

    }


    /**
     * <p class="changed_added_2_0">Install the listener instance
     * referenced by argument <code>listener</code> into application
     * as a listener for events of type
     * <code>systemEventClass</code>.  The default implementation simply calls
     * through to {@link #subscribeToEvent(Class, Class, javax.faces.event.SystemEventListener)}
     * passing <code>null</code> as the <code>sourceClass</code> argument</p>
     *
     * @param systemEventClass the <code>Class</code> of event for which
     * <code>listener</code> must be fired.
     *
     * @param listener the implementation of {@link
     * javax.faces.event.SystemEventListener} whose {@link
     * javax.faces.event.SystemEventListener#processEvent} method must be called when
     * events of type <code>systemEventClass</code> are fired.
     *
     * @throws <code>NullPointerException</code> if any combination of
     * <code>systemEventClass</code>, or <code>listener</code> are
     * <code>null</code>.
     *
     * @since 2.0
     */
    public void subscribeToEvent(Class<? extends SystemEvent> systemEventClass,
                                 SystemEventListener listener) {

        if (defaultApplication != null) {
            defaultApplication.subscribeToEvent(systemEventClass, listener);
        }

        throw new UnsupportedOperationException();

    }


    /**
     * <p class="changed_added_2_0">Remove the listener instance
     * referenced by argument <code>listener</code> from the application
     * as a listener for events of type
     * <code>systemEventClass</code> that originate from objects of type
     * <code>sourceClass</code>.  See {@link
     * #subscribeToEvent(Class, Class,
     * javax.faces.event.SystemEventListener)} for the specification
     * of how the listener is stored, and therefore, how it must be
     * removed.</p>
     *
     * @param systemEventClass the <code>Class</code> of event for which
     * <code>listener</code> must be fired.
     *
     * @param sourceClass the <code>Class</code> of the instance which
     * causes events of type <code>systemEventClass</code> to be fired.
     * May be <code>null</code>.
     *
     * @param listener the implementation of {@link
     * javax.faces.event.SystemEventListener} to remove from the internal data
     * structure.
     *
     * @throws <code>NullPointerException</code> if any combination of
     * <code>context</code>,
     * <code>systemEventClass</code>, or <code>listener</code> are
     * <code>null</code>.
     *
     * @since 2.0
     */
    public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass,
                                     Class sourceClass,
                                     SystemEventListener listener) {

        if (defaultApplication != null) {
            defaultApplication.unsubscribeFromEvent(systemEventClass,
                                                    sourceClass,
                                                    listener);
        }

        throw new UnsupportedOperationException();

    }


    /**
     * <p class="changed_added_2_0">Remove the listener instance
     * referenced by argument <code>listener</code> from the application
     * as a listener for events of type <code>systemEventClass</code>.  The
     * default implementation simply calls through to {@link #unsubscribeFromEvent(Class, javax.faces.event.SystemEventListener)}
     * passing <code>null</code> as the <code>sourceClass</code> argument</p>
     *
     * @param systemEventClass the <code>Class</code> of event for which
     * <code>listener</code> must be fired.
     *
     * @param listener the implementation of {@link
     * javax.faces.event.SystemEventListener} to remove from the internal data
     * structure.
     *
     * @throws <code>NullPointerException</code> if any combination of
     * <code>context</code>, <code>systemEventClass</code>, or
     * <code>listener</code> are
     * <code>null</code>.
     *
     * @since 2.0
     */
    public void unsubscribeFromEvent(Class<? extends SystemEvent> systemEventClass,
                                     SystemEventListener listener) {

        if (defaultApplication != null) {
            defaultApplication.unsubscribeFromEvent(systemEventClass, listener);
        }

        throw new UnsupportedOperationException();

    }

}
