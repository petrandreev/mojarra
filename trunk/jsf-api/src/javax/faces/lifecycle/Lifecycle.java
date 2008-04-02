/*
 * $Id: Lifecycle.java,v 1.21 2003/03/13 01:12:22 craigmcc Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.lifecycle;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;


/**
 * <p><strong>Lifecycle</strong> manages the
 * processing of the entire lifecycle of a particular JavaServer Faces
 * request.  It is responsible for executing all of the phases that have
 * been defined by the JavaServer Faces Specification, in the specified
 * order, unless otherwise directed by activities that occurred during
 * the execution of each phase.</p>
 *
 * <p>An instance of <code>Lifecycle</code> is created by calling the
 * <code>getLifecycle()</code> method of {@link LifecycleFactory}, for
 * a specified lifecycle identifier.  Because this instance is
 * shared across multiple simultaneous requests, it must be implemented
 * in a thread-safe manner.</p>
 */

public abstract class Lifecycle {


    // ------------------------------------------------------------- Properties


    /**
     * <p>Return the {@link ViewHandler} instance that will be utilized
     * during the <em>Render Response</em> phase of the request processing
     * lifecycle.</p>
     */
    public abstract ViewHandler getViewHandler();


    /**
     * <p>Set the {@link ViewHandler} instance that will be utilized
     * during the <em>Render Response</em> phase of the request processing
     * lifecycle.</p>
     *
     * @param handler The new {@link ViewHandler} instance
     *
     * @exception IllegalStateException if this method is called after at least
     *  one request has been processed by this <code>Lifecycle</code> instance
     * @exception NullPointerException if <code>handler</code>
     *  is <code>null</code>
     */
    public abstract void setViewHandler(ViewHandler handler);


    // --------------------------------------------------------- Public Methods


    /**
     * <p>Execute all of the phases of the request processing lifecycle,
     * as described in the JavaServer Faces Specification, in the specified
     * order.  The processing flow can be affected (by the application,
     * by components, or by event listeners) by calls to the
     * <code>renderResponse()</code> or <code>responseComplete()</code>
     * methods of the {@link FacesContext} instance associated with
     * the current request.</p>
     *
     * @param context FacesContext for the request to be processed
     *
     * @exception FacesException if thrown during the execution of the
     *  request processing lifecycle
     * @exception NullPointerException if <code>context</code>
     *  is <code>null</code>
     */
    public abstract void execute(FacesContext context) throws FacesException;


}
