/*
 * $Id: ActionSource2.java,v 1.1 2005/05/05 20:51:00 edburns Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.component;

import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;



/**
 * <p><strong>ActionSource2</strong> extends {@link ActionSource} and
 * provides a JavaBeans property analogous to the "<code>action</code>"
 * property on <code>ActionSource</code>.  The difference is the type of
 * this property is a {@link MethodExpression} rather than a
 * <code>MethodBinding</code>.  This allows the
 * <code>ActionSource</code> concept to leverage the new Unified EL
 * API.</p>
 *
 * @since 1.2
 */

public interface ActionSource2 extends ActionSource {


    // -------------------------------------------------------------- Properties

    /**
     * <p>Return the {@link MethodExpression} pointing at the application
     * action to be invoked, if this {@link UIComponent} is activated by
     * the user, during the <em>Apply Request Values</em> or <em>Invoke
     * Application</em> phase of the request processing lifecycle,
     * depending on the value of the <code>immediate</code>
     * property.</p>
     *
     * <p>Note that it's possible that the returned
     * <code>MethodExpression</code> is just a wrapper around a
     * <code>MethodBinding</code> instance whith was set by a call to
     * {@link ActionSource#setAction}.  This makes it possible for the
     * default {@link ActionListener} to continue to work properly with
     * older components.</p>
     */
    public MethodExpression getActionExpression();

    /**
     * <p>Set the {@link MethodExpression} pointing at the appication
     * action to be invoked, if this {@link UIComponent} is activated by
     * the user, during the <em>Apply Request Values</em> or <em>Invoke
     * Application</em> phase of the request processing lifecycle,
     * depending on the value of the <code>immediate</code>
     * property.</p>
     *
     * <p>Any method referenced by such an expression must be public, with
     * a return type of <code>String</code>, and accept no parameters.</p>
     *
     * @param action The new method expression
     */
    public void setActionExpression(MethodExpression action);

    
}
