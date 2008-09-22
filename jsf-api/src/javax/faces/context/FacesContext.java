/*
 * $Id: FacesContext.java,v 1.76.2.2 2008/04/09 08:59:05 edburns Exp $
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

package javax.faces.context;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKit;

import javax.el.ELContext;
import javax.faces.event.PhaseId;


/**
 * <p><strong class="changed_modified_2_0">FacesContext</strong>
 * contains all of the per-request state information related to the
 * processing of a single JavaServer Faces request, and the rendering of
 * the corresponding response.  It is passed to, and potentially
 * modified by, each phase of the request processing lifecycle.</p>
 *
 * <p>A {@link FacesContext} instance is associated with a particular
 * request at the beginning of request processing, by a call to the
 * <code>getFacesContext()</code> method of the {@link FacesContextFactory}
 * instance associated with the current web application.  The instance
 * remains active until its <code>release()</code> method is called, after
 * which no further references to this instance are allowed.  While a
 * {@link FacesContext} instance is active, it must not be referenced
 * from any thread other than the one upon which the servlet container
 * executing this web application utilizes for the processing of this request.
 * </p>
 */

public abstract class FacesContext {


    /**
     * <p class="changed_added_2_0">
     * The request parameter name whose request parameter value 
     * is a <code>List</code> of client identifiers identifying the
     * components that must be processed during the 
     * <em>Render Response</em> phase of the request processing 
     * lifecycle.</p>
     *
     * @since 2.0
     */
    public static final String PARTIAL_RENDER_PARAM_NAME =
          "javax.faces.partial.render";


    /**
     * <p class="changed_added_2_0">
     * The request parameter name whose request parameter value 
     * is a <code>List</code> of client identifiers identifying the
     * components that must be processed during the 
     * <em>Apply Request Values</em>, <em>Process Validations</em>,
     * and <em>Update Model Values</em> phases of the request 
     * processing lifecycle.</p>
     *
     * @since 2.0
     */
    public static final String PARTIAL_EXECUTE_PARAM_NAME =
          "javax.faces.partial.execute";


    /**
     * <p class="changed_added_2_0">
     * The value that when used with {@link #PARTIAL_EXECUTE_PARAM_NAME}
     * or {@link #PARTIAL_RENDER_PARAM_NAME} indicates these phases
     * must be skipped.</p>  
     *
     * @since 2.0
     */
    public static final String NO_PARTIAL_PHASE_CLIENT_IDS = "none";


    @SuppressWarnings({"UnusedDeclaration"})
    private FacesContext defaultFacesContext;

    // -------------------------------------------------------------- Properties


    /**
     * <p><span class="changed_modified_2_0">Return</span> the {@link
     * Application} instance associated with this web application.</p>

     * <p class="changed_added_2_0">It is valid to call this method
     * during application startup.  If called during application
     * startup, returns the correct current {@link
     * javax.faces.application.Application} instance.</p>

     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract Application getApplication();
    
    
    /**
     * <p class="changed_added_2_0">Return a mutable <code>Map</code> 
     * representing the attributes associated wth this
     * <code>FacesContext</code> instance.  This <code>Map</code> is 
     * useful to store attributes that you want to go out of scope when the
     * Faces lifecycle for the current request ends, which is not always the same 
     * as the request ending, especially in the case of Servlet filters
     * that are invoked <strong>after</strong> the Faces lifecycle for this
     * request completes.  Accessing this <code>Map</code> does not cause any 
     * events to fire, as is the case with the other maps: for request, session, and 
     * application scope.  When {@link #release()} is invoked, the attributes
     * must be cleared.</p>
     * 
     * <div class="changed_added_2_0">
     * 
     * <p>The <code>Map</code> returned by this method is not associated with
     * the request.  If you would like to get or set request attributes,
     * see {@link ExternalContext#getRequestMap}.  
     * 
     * <p>The default implementation throws
     * <code>UnsupportedOperationException</code> and is provided
     * for the sole purpose of not breaking existing applications that extend
     * this class.</p>
     *
     * </div>
     * 
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */

    public Map<Object, Object> getAttributes() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.getAttributes();
        }
        throw new UnsupportedOperationException();
 
    }


    /**
     * <p>Return an <code>Iterator</code> over the client identifiers for
     * which at least one {@link javax.faces.application.FacesMessage} has been queued.  If there are no
     * such client identifiers, an empty <code>Iterator</code> is returned.
     * If any messages have been queued that were not associated with any
     * specific client identifier, a <code>null</code> value will be included
     * in the iterated values.  The elements in the <code>Iterator</code> must
     * be returned in the order in which they were added with {@link #addMessage}.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract Iterator<String> getClientIdsWithMessages();

    /**
     * <p>Return the <code>ELContext</code> instance for this
     * <code>FacesContext</code> instance.  This <code>ELContext</code>
     * instance has the same lifetime and scope as the
     * <code>FacesContext</code> instance with which it is associated,
     * and may be created lazily the first time this method is called
     * for a given <code>FacesContext</code> instance.  Upon creation of
     * the ELContext instance, the implementation must take the
     * following action: </p>
     *
     *	<ul>
     *
     *	  <li><p>Call the {@link ELContext#putContext} method on the
     *	  instance, passing in <code>FacesContext.class</code> and the
     *	  <code>this</code> reference for the <code>FacesContext</code>
     *    instance itself.</p></li>
     *
     *    <li><p>If the <code>Collection</code> returned by {@link
     *    javax.faces.application.Application#getELContextListeners} is
     *    non-empty, create an instance of {@link
     *    javax.el.ELContextEvent} and pass it to each {@link
     *    javax.el.ELContextListener} instance in the
     *    <code>Collection</code> by calling the {@link
     *    javax.el.ELContextListener#contextCreated} method.</p></li>
     *
     * </ul>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 1.2
     */ 

    public ELContext getELContext() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.getELContext();
        }

        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">Return a
     * <code>List</code> of client identifiers from the current request
     * with the request parameter name {@link #PARTIAL_EXECUTE_PARAM_NAME}.
     * If the value of the request parameter is {@link #NO_PARTIAL_PHASE_CLIENT_IDS},
     * or there is no such request parameter, return an empty <code>List</code>.
     * These client identifiers are used to identify components that
     * will be processed during the <code>execute</code> phase of the
     * request processing lifecycle.  The returned <code>List</code> is
     * mutable.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public List<String> getExecutePhaseClientIds() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.getExecutePhaseClientIds();
        }
        throw new UnsupportedOperationException();

    }
    
    /**
     * <p class="changed_added_2_0">Set the <code>List</code>
     * of client identifiers that will be used to identify components that
     * will be processed during the <code>execute</code> phase of the
     * request processing lifecycle.</p>
     *
     * @param executePhaseClientIds The <code>List</code> of client identifiers.
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public void setExecutePhaseClientIds(List<String>executePhaseClientIds) {

        if (defaultFacesContext != null) {
            defaultFacesContext.setExecutePhaseClientIds(executePhaseClientIds);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p><span class="changed_modified_2_0">Return</span> the {@link
     * ExternalContext} instance for this <code>FacesContext</code>
     * instance.</p>

     * <p class="changed_added_2_0">It is valid to call this method
     * during application startup.  If called during application
     * startup, this method returns an {@link ExternalContext} instance
     * with the special behaviors indicated in the javadoc for that
     * class.  Methods document as being valid to call during
     * application startup must be supported.</p>

     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract ExternalContext getExternalContext();


    /**
     * <p>Return the maximum severity level recorded on any
     * {@link javax.faces.application.FacesMessage}s that has been queued, whether or not they are
     * associated with any specific {@link UIComponent}.  If no such messages
     * have been queued, return <code>null</code>.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract Severity getMaximumSeverity();


    /**
     * <p>Return an <code>Iterator</code> over the {@link javax.faces.application.FacesMessage}s
     * that have been queued, whether or not they are associated with any
     * specific client identifier.  If no such messages have been queued,
     * return an empty <code>Iterator</code>.  The elements of the <code>Iterator</code>
     * must be returned in the order in which they were added with calls to {@link 
     * #addMessage}.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract Iterator<FacesMessage> getMessages();

    /**
     * <p class="changed_added_2_0">Like {@link getMessages}, but
     * returns a <code>List&lt;FacesMessage&gt;</code>,
     * enabling use from EL expressions.</p>
     *
     * <p>The default implementation throws
     * <code>UnsupportedOperationException</code> and is provided
     * for the sole purpose of not breaking existing applications that extend
     * this class.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     * @since 2.0
     */ 

    public List<FacesMessage> getMessageList() {
        if (defaultFacesContext != null) {
            return defaultFacesContext.getMessageList();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * <p class="changed_added_2_0">Like {@link
     * getMessages(java.lang.String)}, but returns a
     * <code>List&lt;FacesMessage&gt;</code> of messages for the
     * component with client id matching argument
     * <code>clientId</code>.</p>
     *
     * <p>The default implementation throws
     * <code>UnsupportedOperationException</code> and is provided
     * for the sole purpose of not breaking existing applications that extend
     * this class.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     * @since 2.0
     */ 

    public List<FacesMessage> getMessageList(String clientId) {
        if (defaultFacesContext != null) {
            return defaultFacesContext.getMessageList(clientId);
        }
        throw new UnsupportedOperationException();
    }




    /**
     * <p>Return an <code>Iterator</code> over the {@link javax.faces.application.FacesMessage}s that
     * have been queued that are associated with the specified client identifier
     * (if <code>clientId</code> is not <code>null</code>), or over the
     * {@link javax.faces.application.FacesMessage}s that have been queued that are not associated with
     * any specific client identifier (if <code>clientId</code> is
     * <code>null</code>).  If no such messages have been queued, return an
     * empty <code>Iterator</code>.  The elements of the <code>Iterator</code>
     * must be returned in the order in which they were added with calls to {@link 
     * #addMessage}.</p>
     *
     * @param clientId The client identifier for which messages are
     *  requested, or <code>null</code> for messages not associated with
     *  any client identifier
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract Iterator<FacesMessage> getMessages(String clientId);


    /**
     * <p>Return the {@link RenderKit} instance for the render kit identifier
     * specified on our {@link UIViewRoot}, if there is one.  If there is no
     * current {@link UIViewRoot}, if the {@link UIViewRoot} does not have a
     * specified <code>renderKitId</code>, or if there is no {@link RenderKit}
     * for the specified identifier, return <code>null</code> instead.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract RenderKit getRenderKit();

    /**
     * <p class="changed_added_2_0">Return a
     * <code>List</code> of client identifiers from the current request
     * with the request parameter name {@link #PARTIAL_RENDER_PARAM_NAME}.
     * If the value of the request parameter is {@link #NO_PARTIAL_PHASE_CLIENT_IDS},
     * or there is no such request parameter, return an empty <code>List</code>.
     * These client identifiers are used to identify components that
     * will be processed during the <code>render</code> phase of the
     * request processing lifecycle.  The returned <code>List</code> is 
     * mutable.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public List<String> getRenderPhaseClientIds() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.getRenderPhaseClientIds();
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">Set the <code>List</code>
     * of client identifiers that will be used to identify components that
     * will be processed during the <code>render</code> phase of the
     * request processing lifecycle.</p>
     *
     * @param renderPhaseClientIds The <code>List</code> of client identifiers.
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public void setRenderPhaseClientIds(List<String>renderPhaseClientIds) {

        if (defaultFacesContext != null) {
            defaultFacesContext.setRenderPhaseClientIds(renderPhaseClientIds);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>Return <code>true</code> if the <code>renderResponse()</code>
     * method has been called for the current request.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract boolean getRenderResponse();


    /**
     * <p>Return <code>true</code> if the <code>responseComplete()</code>
     * method has been called for the current request.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract boolean getResponseComplete();


    /**
     * <p>Return the {@link ResponseStream} to which components should
     * direct their binary output.  Within a given response, components
     * can use either the ResponseStream or the ResponseWriter,
     * but not both.
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract ResponseStream getResponseStream();


    /**
     * <p>Set the {@link ResponseStream} to which components should
     * direct their binary output.
     *
     * @param responseStream The new ResponseStream for this response
     *
     * @throws NullPointerException if <code>responseStream</code>
     *  is <code>null</code>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract void setResponseStream(ResponseStream responseStream);


    /**
     * <p>Return the {@link ResponseWriter} to which components should
     * direct their character-based output.  Within a given response,
     * components can use either the ResponseStream or the ResponseWriter,
     * but not both.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract ResponseWriter getResponseWriter();


    /**
     * <p>Set the {@link ResponseWriter} to which components should
     * direct their character-based output.
     *
     * @param responseWriter The new ResponseWriter for this response
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     * @throws NullPointerException if <code>responseWriter</code>
     *  is <code>null</code>
     */
    public abstract void setResponseWriter(ResponseWriter responseWriter);

    /**
     * <p class="changed_added_2_0">Return the {@link ResponseWriter} 
     * to which components should
     * direct their output for partial view rendering.  Within a given
     * response, components can use either the ResponseStream or the
     * ResponseWriter, but not both.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public ResponseWriter getPartialResponseWriter() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.getPartialResponseWriter();
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p><span class="changed_modified_2_0">Return</span> the root
     * component that is associated with the this request.  </p>

     * <p class="changed_added_2_0">It is valid to call this method
     * during application startup.  If called during application
     * startup, this method returns a new <code>UIViewRoot</code> with
     * its locale set to <code>Locale.getDefault()</code>.</p>

     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract UIViewRoot getViewRoot();


    /**
     * <p><span class="changed_modified_2_0">Set</span> the root
     * component that is associated with this request.  This method can
     * only be called by the application handler (or a class that the
     * handler calls), and only during the <em>Invoke Application</em>
     * phase of the request processing lifecycle.</p>

     * <p class="changed_added_2_0">If the current
     * <code>UIViewRoot</code> is non-<code>null</code>, and calling
     * <code>equals()</code> on the argument <code>root</code>, passing
     * the current <code>UIViewRoot</code> returns <code>false</code>,
     * the <code>clear</code> method must be called on the
     * <code>Map</code> returned from {@link UIViewRoot#getViewMap}.</p>
     *
     * @param root The new component {@link UIViewRoot} component
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     * @throws NullPointerException if <code>root</code>
     *  is <code>null</code>
     */
    public abstract void setViewRoot(UIViewRoot root);


    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Append a {@link javax.faces.application.FacesMessage} to the set of messages associated with
     * the specified client identifier, if <code>clientId</code> is
     * not <code>null</code>.  If <code>clientId</code> is <code>null</code>,
     * this {@link javax.faces.application.FacesMessage} is assumed to not be associated with any
     * specific component instance.</p>
     *
     * @param clientId The client identifier with which this message is
     *  associated (if any)
     * @param message The message to be appended
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     * @throws NullPointerException if <code>message</code>
     *  is <code>null</code>
     */
    public abstract void addMessage(String clientId, FacesMessage message);


    /**
     * <p><span class="changed_modified_2_0">Release</span> any
     * resources associated with this <code>FacesContext</code>
     * instance.  Faces implementations may choose to pool instances in
     * the associated {@link FacesContextFactory} to avoid repeated
     * object creation and garbage collection.  After
     * <code>release()</code> is called on a <code>FacesContext</code>
     * instance (until the <code>FacesContext</code> instance has been
     * recycled by the implementation for re-use), calling any other
     * methods will cause an <code>IllegalStateException</code> to be
     * thrown.</p>

     * <p class="changed_added_2_0">If a call was made to {@link
     * #getAttributes} during the processing for this request, the
     * implementation must call <code>clear()</code> on the
     * <code>Map</code> returned from <code>getAttributes()</code>, and
     * then de-allocate the data-structure behind that
     * <code>Map</code>.</p>

     * <p>The implementation must call {@link #setCurrentInstance}
     * passing <code>null</code> to remove the association between this
     * thread and this dead <code>FacesContext</code> instance.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract void release();


    /**
     * <p>Signal the JavaServer faces implementation that, as soon as the
     * current phase of the request processing lifecycle has been completed,
     * control should be passed to the <em>Render Response</em> phase,
     * bypassing any phases that have not been executed yet.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract void renderResponse();

    /**
     * <p class="changed_added_2_0">
     * This utility method simply returns the result of
     * {@link javax.faces.render.ResponseStateManager#isPostback(FacesContext)}.
     * </p>
     *
     * <p class="changed_added_2_0">The default implementation throws
     * <code>UnsupportedOperationException</code> and is provided
     * for the sole purpose of not breaking existing applications that extend
     * this class.</p>
     * 
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public boolean isPostback() {

        if (defaultFacesContext != null) {
            defaultFacesContext.isPostback();
        }

        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">
     * Return <code>true</code> if the request parameter
     * <code>javax.faces.partial.ajax</code> is present in the current
     * request.  Otherwise, return <code>false</code>.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public boolean isAjaxRequest() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.isAjaxRequest();
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">
     * Return <code>true</code> if the request parameter {@link #PARTIAL_EXECUTE_PARAM_NAME}
     * is present in the current request and the value of the parameter is
     * {@link #NO_PARTIAL_PHASE_CLIENT_IDS}. Otherwise, return <code>false</code>.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public boolean isExecuteNone() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.isExecuteNone();
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">
     * Return <code>true</code> if {@link #isAjaxRequest}
     * returns <code>true</code>, {@link #isRenderNone} returns
     * <code>false</code> and {@link #getRenderPhaseClientIds}
     * returns an <code>empty</code> list.
     * Otherwise, return <code>false</code>.  This method effectively
     * says "render everything" if {@link #PARTIAL_RENDER_PARAM_NAME}
     * is not present in the current request with the value
     * {@link #NO_PARTIAL_PHASE_CLIENT_IDS}.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public boolean isRenderAll() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.isRenderAll();
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">
     * Indicate the entire view must be rendered if
     * <code>renderAll</code> is <code>true</code>.</p>
     *
     * @param renderAll the value <code>true</code> indicates
     * the entire view must be rendered.
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public void setRenderAll(boolean renderAll) {

        if (defaultFacesContext != null) {
            defaultFacesContext.setRenderAll(renderAll);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p class="changed_added_2_0">
     * Return <code>true</code> if the request parameter
     * {@link #PARTIAL_RENDER_PARAM_NAME} is present in the current
     * request and the value of the parameter is {@link #NO_PARTIAL_PHASE_CLIENT_IDS}.
     * Otherwise, return <code>false</code>.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public boolean isRenderNone() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.isRenderNone();
        }
        throw new UnsupportedOperationException();

    }

    /**
     * <p class="changed_added_2_0">
     * Allow or prevent content from being written to the underlying response.</p>
     *
     * @param enable The value <code>true</code> will allow content to be
     * written to the response.  The value <code>false</code> will prevent
     * content from being written to the response.
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public void enableResponseWriting(boolean enable) {

        if (defaultFacesContext != null) {
            defaultFacesContext.enableResponseWriting(enable);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * <p>Signal the JavaServer Faces implementation that the HTTP response
     * for this request has already been generated (such as an HTTP redirect),
     * and that the request processing lifecycle should be terminated as soon
     * as the current phase is completed.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     */
    public abstract void responseComplete();
    

    /**
     * <p class="changed_added_2_0">Return the value last set on this
     * <code>FacesContext</code> instance when {@link #setCurrentPhaseId}
     * was called.</p>
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public PhaseId getCurrentPhaseId() {

        if (defaultFacesContext != null) {
            return defaultFacesContext.getCurrentPhaseId();
        }

        throw new UnsupportedOperationException();

    }
    
    /**
     * <p class="changed_added_2_0">The implementation must call this method
     * at the earliest possble point in time after entering into a new phase
     * in the request processing lifecycle.</p>
     * 
     * @param currentPhaseId The {@link javax.faces.event.PhaseId} for the 
     * current phase.
     *
     * @throws IllegalStateException if this method is called after
     *  this instance has been released
     *
     * @since 2.0
     */
    public void setCurrentPhaseId(PhaseId currentPhaseId) {

        if (defaultFacesContext != null) {
            defaultFacesContext.setCurrentPhaseId(currentPhaseId);
        } else {
            throw new UnsupportedOperationException();
        }

    }

    // ---------------------------------------------------------- Static Methods


    /**
     * <p>The <code>ThreadLocal</code> variable used to record the
     * {@link FacesContext} instance for each processing thread.</p>
     */
    private static ThreadLocal<FacesContext> instance = new ThreadLocal<FacesContext>() {
            protected FacesContext initialValue() { return (null); }
        };


    /**
     * <p class="changed_modified_2_0">Return the {@link FacesContext}
     * instance for the request that is being processed by the current
     * thread.  If called during application initialization, any method
     * documented as "valid to call this method during application
     * startup" must be supported during application startup time.  The
     * result of calling a method during application startup time that
     * does not have this designation is undefined.</p> 
     */
    public static FacesContext getCurrentInstance() {

        return (instance.get());

    }


    /**
     * <p>Set the {@link FacesContext} instance for the request that is
     * being processed by the current thread.</p>
     *
     * @param context The {@link FacesContext} instance for the current
     * thread, or <code>null</code> if this thread no longer has a
     * <code>FacesContext</code> instance.
     *
     */
    protected static void setCurrentInstance(FacesContext context) {

        if (context == null) {
            instance.remove();
        } else {
            instance.set(context);
        }

    }


}
