/*
 * $Id:
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

package com.sun.faces.renderkit.html_basic;

import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.renderkit.Attribute;
import com.sun.faces.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutcomeTarget;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public abstract class OutcomeTargetRenderer extends HtmlBasicRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {}

    // ------------------------------------------------------- Protected Methods
    

    protected void renderPassThruAttributes(FacesContext ctx,
                                            ResponseWriter writer,
                                            UIComponent component,
                                            Attribute[] attributes)
    throws IOException {
        RenderKitUtils.renderPassThruAttributes(ctx, writer, component, attributes);
        RenderKitUtils.renderXHTMLStyleBooleanAttributes(writer, component);

        
    }

    protected String getLabel(UIComponent component) {

        Object value = ((UIOutcomeTarget) component).getValue();
        return value != null ? value.toString() : "";
        
    }

    protected String getFragment(UIComponent component) {

        String fragment = (String) component.getAttributes().get("fragment");
        fragment = (fragment != null ? fragment.trim() : "");
        if (fragment.length() > 0) {
            fragment = "#" + fragment;
        }
        return fragment;

    }

    @Override
    protected Object getValue(UIComponent component) {

        return ((UIOutcomeTarget) component).getValue();

    }

    protected boolean isIncludeViewParams(UIComponent component) {

        return ((UIOutcomeTarget) component).isIncludeViewParams();

    }

    /**
     * Invoke the {@link NavigationHandler} preemptively to resolve a {@link NavigationCase}
     * for the outcome declared on the {@link UIOutcomeTarget} component. The current view id
     * is used as the from-view-id when matching navigation cases and the from-action is
     * assumed to be null.
     *
     * @param context the {@link FacesContext} for the current request
     * @param component the target {@link UIComponent}
     *
     * @return the NavigationCase represeting the outcome target
     */
    protected NavigationCase getNavigationCase(FacesContext context, UIComponent component) {
        NavigationHandler navHandler = context.getApplication().getNavigationHandler();
        if (!(navHandler instanceof ConfigurableNavigationHandler)) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                    "NavigationHandler must be an instance of ConfigurableNavigationHandler to use a UIOutputTarget component {0}",
                    component.getId());
            }
            return null;
        }

        String outcome = ((UIOutcomeTarget) component).getOutcome();
        if (outcome == null) {
            outcome = context.getViewRoot().getViewId();
            // QUESTION should we avoid the call to getNavigationCase() and instead instantiate one explicitly?
            //String viewId = context.getViewRoot().getViewId();
            //return new NavigationCase(viewId, null, null, null, viewId, false, false);
        }
        NavigationCase navCase = ((ConfigurableNavigationHandler) navHandler).getNavigationCase(context, null, outcome);
        if (navCase == null) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING,
                           "Navigation case not resolved for component {0}",
                           component.getId());
            }
        }
        return navCase;
    }

    /**
     * <p>Resolve the target view id and then delegate to
     * {@link ViewHandler#getRedirectURL(javax.faces.context.FacesContext, String, java.util.Map, boolean)}
     * to produce a redirect URL, which will add the page parameters if necessary
     * and properly prioritizing the parameter overrides.</p>
     *
     * @param context the {@link FacesContext} for the current request
     * @param component the target {@link UIComponent}
     * @param navCase the target navigation case
     *
     * @return an encoded URL for the provided navigation case
     */
    protected String getEncodedTargetURL(FacesContext context, UIComponent component, NavigationCase navCase) {
        // FIXME getNavigationCase doesn't resolve the target viewId (it is part of CaseStruct)
        String toViewId = navCase.getToViewId(context);
        return Util.getViewHandler(context).getRedirectURL(context,
                                                           toViewId,
                                                           getParamOverrides(component),
                                                           isIncludeViewParams(component));
    }

    protected Map<String, List<String>> getParamOverrides(UIComponent component) {
        Map<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        Param[] declaredParams = getParamList(component);
        for (Param candidate : declaredParams) {
            // QUESTION shouldn't the trimming of name should be done elsewhere?
            // null value is allowed as a way to suppress page parameter
            if (candidate.name != null && candidate.name.trim().length() > 0) {
                candidate.name = candidate.name.trim();
                List<String> values = params.get(candidate.name);
                if (values == null) {
                    values = new ArrayList<String>();
                    params.put(candidate.name, values);
                }
                values.add(candidate.value);
            }
        }

        return params;
    }

}
