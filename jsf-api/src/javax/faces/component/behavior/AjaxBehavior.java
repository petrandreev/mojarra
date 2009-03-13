/*
 * $Id: AjaxBehavior.java,v 1.0 2008/11/03 18:51:29 rogerk Exp $
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
 
package javax.faces.component.behavior;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;


/**
 * <p class="changed_added_2_0">An instance of this class is added
 * as a {@link ClientBehavior} to a component using the 
 * {@link javax.faces.component.behavior.ClientBehaviorHolder#addClientBehavior} 
 * contract that components implement.  The presence of this 
 * {@link ClientBehavior} will cause the rendering of JavaScript that 
 * produces an <code>Ajax</code> request using the 
 * specification public JavaScript API when the component is 
 * rendered.</p> 
 *
 * @since 2.0
 */
@SuppressWarnings("serial")
public class AjaxBehavior extends ClientBehaviorBase implements Serializable {

    /**
     * <p class="changed_added_2_0">The key that when added to a 
     * component's attributes Map will cause the rendering of 
     * JavaScript to perform an Ajax request.</p> 
     *
     * @since 2.0
     */
    public static final String AJAX_BEHAVIOR = "javax.faces.behavior.AjaxBehavior";

    /**
     * <p class="changed_added_2_0">The identifier for Ajax value change events.</p> 
     *
     * @since 2.0
     */
    public static final String AJAX_VALUE_CHANGE = "valueChange";

    /**
     * <p class="changed_added_2_0">The identifier for Ajax action events.</p> 
     *
     * @since 2.0
     */
    public static final String AJAX_ACTION = "action";

    private static final Set<ClientBehaviorHint> HINTS = 
        Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));

    private String event;
    private ValueExpression onerrorExpression;
    private ValueExpression oneventExpression;
    private ValueExpression executeExpression;
    private ValueExpression renderExpression;
    private ValueExpression disabledExpression;
    private ValueExpression immediateExpression;


    // ------------------------------------------------------------ Constructors


    /**
     * <p class="changed_added_2_0">Construct a new <code>AjaxBehavior</code> 
     * instance from the specified parameters.</p>
     *
     * @param event the event associated with this behavior.  In most cases, this is 
     * the event that triggered this behavior.
     * @param onevent the JavaScript function name that will be used to identify the
     * client callback function that should be run on the occurance
     * of a client-side event. 
     * @param onerror the JavaScript function name that will be used to identify
     * the client callback function that should be run in the event of
     * an error. 
     * @param execute component identifiers that will be used to identify 
     * components that should be processed during the <code>execute</code> 
     * phase of the request processing lifecycle.
     * @param render component identifiers that will be used to identify 
     * components that should be processed during the <code>render</code> 
     * phase of the request processing lifecycle.
     * @param disabled the disabled status of this behavior. 
     * @param immediate if true, events generated by this behavior are
     *   broadcast during the apply request values phase.  Otherwise,
     *   events are broadcast during the invoke application phase.
     *
     * @throws IllegalArgumentException if <code>component</code> is
     *  <code>null</code>
     *
     * @since 2.0
     */
    public AjaxBehavior(String event,
                        ValueExpression onevent,
                        ValueExpression onerror,
                        ValueExpression execute,
                        ValueExpression render,
                        ValueExpression disabled,
                        ValueExpression immediate) {

        this.onerrorExpression = onerror;
        this.oneventExpression = onevent;
        this.event = event;
        this.executeExpression = execute;
        this.renderExpression = render;
        this.disabledExpression = disabled;
        this.immediateExpression = immediate;
    }


    @Override
    public String getRendererType() {
       return AJAX_BEHAVIOR;
    }

    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }

    // ---------------------------------------------------------- Public Methods

    
    /**
     * <p class="changed_added_2_0">Return the event associated with
     * this instance.</p>
     *
     * @since 2.0
     */
    public String getEvent() {

        return event;

    }

    /**
     * <p class="changed_added_2_0">Return the <code>String</code> of
     * JavaScript function name that will be used to identify
     * the client callback function that should be run in the event of
     * an error.
     *
     * @param context the {@link FacesContext} for the current request
     *
     * @since 2.0
     */
    public String getOnError(FacesContext context) {

        return (String) eval(context, onerrorExpression);

    }

    /**
     * <p class="changed_added_2_0">Return the <code>String</code> of
     * JavaScript function name that will be used to identify the
     * client callback function that should be run on the occurance
     * of a client-side event.
     *
     * @param context the {@link FacesContext} for the current request
     *
     * @since 2.0
     */
    public String getOnEvent(FacesContext context) {

        return (String) eval(context, oneventExpression);

    }

    /**
     * <p class="changed_added_2_0">Return a
     * <code>Collection&lt;String&gt;</code> of component
     * identifiers that will be used to identify components that should be
     * processed during the <code>execute</code> phase of the request
     * processing lifecycle.</p>
     *
     * @param context the {@link FacesContext} for the current request
     *
     * @since 2.0
     */
    public Collection<String> getExecute(FacesContext context) {

        return getCollectionValue("execute", context, executeExpression);

    }

    /**
     * <p class="changed_added_2_0">Return a
     * <code>Collection&lt;String&gt;</code> of component
     * identifiers that will be used to identify components that should be
     * processed during the <code>render</code> phase of the request
     * processing lifecycle.</p>
     *
     * @param context the {@link FacesContext} for the current request
     *
     * @since 2.0
     */
    public Collection<String> getRender(FacesContext context) {

        return getCollectionValue("render", context, renderExpression);

    }

    /**
     * <p class="changed_added_2_0">Return the disabled status of this component.</p>
     *
     * @param context the {@link FacesContext} for the current request
     *
     * @since 2.0
     */
    public Boolean isDisabled(FacesContext context) {

        // RELEASE_PENDING why not return boolean instead of Boolean?
        Boolean result = (Boolean) eval(context, disabledExpression);
        return ((result != null) ? result : false);

    }

    /**
     * <p class="changed_added_2_0">Return the immediate status of this component.</p>
     *
     * @param context the {@link FacesContext} for the current request
     *
     * @since 2.0
     */
    public Boolean isImmediate(FacesContext context) {
        return (Boolean) eval(context, immediateExpression);
    }

    /**
     * <p class="changed_added_2_0">Add the specified {@link AjaxBehaviorListener}
     * to the set of listeners registered to receive event notifications
     * from this {@link AjaxBehavior}.</p>
     *
     * @param listener The {@link AjaxBehaviorListener} to be registered
     *
     * @throws NullPointerException if <code>listener</code>
     *  is <code>null</code>
     *
     * @since 2.0
     */
    public void addAjaxBehaviorListener(AjaxBehaviorListener listener) {
        addBehaviorListener(listener);
    }

    /**
     * <p class="changed_added_2_0">Remove the specified {@link AjaxBehaviorListener}
     * from the set of listeners registered to receive event notifications
     * from this {@link AjaxBehavior}.</p>
     *
     * @param listener The {@link AjaxBehaviorListener} to be removed
     *
     * @throws NullPointerException if <code>listener</code>
     *  is <code>null</code>
     *
     * @since 2.0
     */
    public void removeAjaxBehaviorListener(AjaxBehaviorListener listener) {
        removeBehaviorListener(listener);
    }

    // --------------------------------------------------------- Private Methods

    private static Object eval(FacesContext ctx, ValueExpression expression) {

        return ((expression != null)
                ? expression.getValue(ctx.getELContext())
                : null);

    }


    private static Collection<String> getCollectionValue(String name,
                                                         FacesContext ctx,
                                                         ValueExpression expression) {

        Collection<String> result = null;
        Object tempAttr = eval(ctx, expression);
        if (tempAttr != null) {
            if (tempAttr instanceof String) {
                // split into separate strings, add these into a new Collection
                // RELEASE_PENDING String.split() isn't cheap.  It recreates the Pattern
                // each time it's called.
                result = new LinkedHashSet<String>(Arrays.asList(((String) tempAttr).split(" ")));
            } else if (tempAttr instanceof Collection) {
                //noinspection unchecked
                result = (Collection<String>) tempAttr;
            } else {
                // RELEASE_PENDING  i18n ;
                throw new FacesException(expression.toString()
                                         + " : '"
                                         + name
                                         + "' attribute value must be either a String or a Collection");
            }
        }
        return result;

    }


}
