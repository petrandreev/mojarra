/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package javax.faces.webapp;


import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p><strong class="changed_modified_2_0 changed_modified_2_0_rev_a
 * changed_modified_2_1">FacesServlet</strong> is a servlet that manages
 * the request processing lifecycle for web applications that are
 * utilizing JavaServer Faces to construct the user interface.</p>
 *
 * <div class="changed_added_2_1">
 *
 * <p>The runtime must support the following additional behavior when
 * running in a Servlet 3.0 (and beyond) container.  The runtime must
 * provide an implementation of {@link
 * javax.servlet.ServletContainerInitializer} whose
 * <code>onStartup()</code> method takes following actions.</p>

 * <p>Adds mappings <em>/faces</em>, <em>*.jsf</em>, and
 * <em>*.faces</em> for the <code>FacesServlet</code> (if it hasn't
 * already been mapped) if the following conditions are met:</p>
 *
 * <ul>
 *    <li>
 *       The <code>Set</code> of classes passed to this initializer is not
 *       empty, or
 *    </li>
 *    <li>
 *       /WEB-INF/faces-config.xml exists.
 *    </li>
 * </ul>

 * </div>
 */

public final class FacesServlet implements Servlet {


    /**
     * <p>Context initialization parameter name for a comma delimited list
     * of context-relative resource paths (in addition to
     * <code>/WEB-INF/faces-config.xml</code> which is loaded automatically
     * if it exists) containing JavaServer Faces configuration information.</p>
     */
    public static final String CONFIG_FILES_ATTR =
        "javax.faces.CONFIG_FILES";


    /**
     * <p>Context initialization parameter name for the lifecycle identifier
     * of the {@link Lifecycle} instance to be utilized.</p>
     */
    public static final String LIFECYCLE_ID_ATTR =
        "javax.faces.LIFECYCLE_ID";


    /**
     * The <code>Logger</code> for this class.
     */
    private static final Logger LOGGER =
          Logger.getLogger("javax.faces.webapp", "javax.faces.LogStrings");


    /**
     * <p>Factory for {@link FacesContext} instances.</p>
     */
    private FacesContextFactory facesContextFactory = null;


    /**
     * <p>The {@link Lifecycle} instance to use for request processing.</p>
     */
    private Lifecycle lifecycle = null;


    /**
     * <p>The <code>ServletConfig</code> instance for this servlet.</p>
     */
    private ServletConfig servletConfig = null;


    /**
     * <p>Release all resources acquired at startup time.</p>
     */
    public void destroy() {

        facesContextFactory = null;
        lifecycle = null;
        servletConfig = null;

    }


    /**
     * <p>Return the <code>ServletConfig</code> instance for this servlet.</p>
     */
    public ServletConfig getServletConfig() {

        return (this.servletConfig);

    }


    /**
     * <p>Return information about this Servlet.</p>
     */
    public String getServletInfo() {

        return (this.getClass().getName());

    }


    /**
     * <p>Acquire the factory instances we will require.</p>
     *
     * @throws ServletException if, for any reason, the startup of
     * this Faces application failed.  This includes errors in the
     * config file that is parsed before or during the processing of
     * this <code>init()</code> method.
     */
    public void init(ServletConfig servletConfig) throws ServletException {

        // Save our ServletConfig instance
        this.servletConfig = servletConfig;

        // Acquire our FacesContextFactory instance
        try {
            facesContextFactory = (FacesContextFactory)
                FactoryFinder.getFactory
                (FactoryFinder.FACES_CONTEXT_FACTORY);
        } catch (FacesException e) {
            ResourceBundle rb = LOGGER.getResourceBundle();
            String msg = rb.getString("severe.webapp.facesservlet.init_failed");
            Throwable rootCause = (e.getCause() != null) ? e.getCause() : e;
            LOGGER.log(Level.SEVERE, msg, rootCause);
            throw new UnavailableException(msg);
        }

        // Acquire our Lifecycle instance
        try {
            LifecycleFactory lifecycleFactory = (LifecycleFactory)
                FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            String lifecycleId ;

            // First look in the servlet init-param set
            if (null == (lifecycleId = servletConfig.getInitParameter(LIFECYCLE_ID_ATTR))) {
                // If not found, look in the context-param set 
                lifecycleId = servletConfig.getServletContext().getInitParameter
                    (LIFECYCLE_ID_ATTR);
            }

            if (lifecycleId == null) {
                lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
            lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
        } catch (FacesException e) {
            Throwable rootCause = e.getCause();
            if (rootCause == null) {
                throw e;
            } else {
                throw new ServletException(e.getMessage(), rootCause);
            }
        }

    }


    /**
     * <p class="changed_modified_2_0">Process an incoming request, and create the
     * corresponding response according to the following
     * specification.</p>
     * 
     * <div class="changed_modified_2_0">
     *
     * <p>If the <code>request</code> and <code>response</code>
     * arguments to this method are not instances of
     * <code>HttpServletRequest</code> and
     * <code>HttpServletResponse</code>, respectively, the results of
     * invoking this method are undefined.</p>
     *
     * <p>This method must respond to requests that start with the
     * following strings by invoking the <code>sendError</code> method
     * on the response argument (cast to
     * <code>HttpServletResponse</code>), passing the code
     * <code>HttpServletResponse.SC_NOT_FOUND</code> as the
     * argument. </p>
     *
     * <ul>
     *
<pre><code>
/WEB-INF/
/WEB-INF
/META-INF/
/META-INF
</code></pre>
     *
     * </ul>
     *
     
     * <p>If none of the cases described above in the specification for
     * this method apply to the servicing of this request, the following
     * action must be taken to service the request.</p>

     * <p>Acquire a {@link FacesContext} instance for this request.</p>

     * <p>Acquire the <code>ResourceHandler</code> for this request by
     * calling {@link
     * javax.faces.application.Application#getResourceHandler}.  Call
     * {@link
     * javax.faces.application.ResourceHandler#isResourceRequest}.  If
     * this returns <code>true</code> call {@link
     * javax.faces.application.ResourceHandler#handleResourceRequest}.
     * If this returns <code>false</code>, call {@link
     * javax.faces.lifecycle.Lifecycle#execute} followed by {@link
     * javax.faces.lifecycle.Lifecycle#render}.  If a {@link
     * javax.faces.FacesException} is thrown in either case, extract the
     * cause from the <code>FacesException</code>.  If the cause is
     * <code>null</code> extract the message from the
     * <code>FacesException</code>, put it inside of a new
     * <code>ServletException</code> instance, and pass the
     * <code>FacesException</code> instance as the root cause, then
     * rethrow the <code>ServletException</code> instance.  If the cause
     * is an instance of <code>ServletException</code>, rethrow the
     * cause.  If the cause is an instance of <code>IOException</code>,
     * rethrow the cause.  Otherwise, create a new
     * <code>ServletException</code> instance, passing the message from
     * the cause, as the first argument, and the cause itself as the
     * second argument.</p>

     * <p class="changed_modified_2_0_rev_a">The implementation must
     * make it so {@link javax.faces.context.FacesContext#release} is
     * called within a finally block as late as possible in the
     * processing for the JSF related portion of this request.</p>

     * </div>
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @throws IOException if an input/output error occurs during processing
     * @throws ServletException if a servlet error occurs during processing

     */
    public void service(ServletRequest request,
                        ServletResponse response)
        throws IOException, ServletException {

        requestStart(((HttpServletRequest) request).getRequestURI()); // V3 Probe hook

        // If prefix mapped, then ensure requests for /WEB-INF are
        // not processed.
        String pathInfo = ((HttpServletRequest) request).getPathInfo();
        if (pathInfo != null) {
            pathInfo = pathInfo.toUpperCase();
            if (pathInfo.startsWith("/WEB-INF/")
                || pathInfo.equals("/WEB-INF")
                || pathInfo.startsWith("/META-INF/")
                || pathInfo.equals("/META-INF")) {
                ((HttpServletResponse) response).
                      sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }    
        
        // Acquire the FacesContext instance for this request
        FacesContext context = facesContextFactory.getFacesContext
              (servletConfig.getServletContext(), request, response, lifecycle);

        // Execute the request processing lifecycle for this request
        try {
            ResourceHandler handler =
                  context.getApplication().getResourceHandler();
            if (handler.isResourceRequest(context)) {
                handler.handleResourceRequest(context);
            } else {
                lifecycle.execute(context);
                lifecycle.render(context);
            }
        } catch (FacesException e) {
            Throwable t = e.getCause();
            if (t == null) {
                throw new ServletException(e.getMessage(), e);
            } else {
                if (t instanceof ServletException) {
                    throw ((ServletException) t);
                } else if (t instanceof IOException) {
                    throw ((IOException) t);
                } else {
                    throw new ServletException(t.getMessage(), t);
                }
            }
        }
        finally {
            // Release the FacesContext instance for this request
            context.release();
        }

        requestEnd(); // V3 Probe hook
    }


    // --------------------------------------------------------- Private Methods


    /**
     * DO NOT REMOVE. Necessary for V3 probe monitoring.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    private void requestStart(String requestUri) { }


    /**
     * DO NOT REMOVE. Necessary for V3 probe monitoring.
     */
    private void requestEnd() { }

}
