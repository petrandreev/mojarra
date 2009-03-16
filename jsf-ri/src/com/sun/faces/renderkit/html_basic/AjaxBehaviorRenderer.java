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

import com.sun.faces.util.FacesLogger;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.render.ClientBehaviorRenderer;

import com.sun.faces.renderkit.RenderKitUtils;

/*
 *<b>AjaxBehaviorRenderer</b> renders Ajax behavior for a component.
 * It also  
 */

public class AjaxBehaviorRenderer extends ClientBehaviorRenderer  {
    
    // Log instance for this class
    protected static final Logger logger = FacesLogger.RENDERKIT.getLogger();

    
    // ------------------------------------------------------ Rendering Methods

    @Override
    public String getScript(ClientBehaviorContext behaviorContext,
                            ClientBehavior behavior) {
        if (!(behavior instanceof AjaxBehavior)) {
            // TODO: use MessageUtils for this error message?
            throw new IllegalArgumentException(
                "Instance of javax.faces.component.behavior.AjaxBehavior required: " + behavior);
        }

        return buildAjaxCommand(behaviorContext, (AjaxBehavior)behavior);
    }


    @Override
    public void decode(FacesContext context,
                       UIComponent component,
                       ClientBehavior behavior) {
        if (null == context || null == component || null == behavior) {
            throw new NullPointerException();
        }

        if (!(behavior instanceof AjaxBehavior)) {
            // TODO: use MessageUtils for this error message?
            throw new IllegalArgumentException(
                "Instance of javax.faces.component.behavior.AjaxBehavior required: " + behavior);
        }

        AjaxBehavior ajaxBehavior = (AjaxBehavior)behavior;

        // First things first - if AjaxBehavior is disabled, we are done.
        if (ajaxBehavior.isDisabled()) {
            return;
        }        

        component.queueEvent(createEvent(context, component, ajaxBehavior));

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("This command resulted in form submission " +
                " AjaxBehaviorEvent queued.");
            logger.log(Level.FINE,
                "End decoding component {0}", component.getId());
        }


    }

    // Creates an AjaxBehaviorEvent for the specified component/behavior
    private static AjaxBehaviorEvent createEvent(FacesContext context,
                                                 UIComponent component,
                                                 AjaxBehavior ajaxBehavior) {

        AjaxBehaviorEvent event = new AjaxBehaviorEvent(component, ajaxBehavior);

        PhaseId phaseId = isImmediate(component, ajaxBehavior) ?
                              PhaseId.APPLY_REQUEST_VALUES :
                              PhaseId.INVOKE_APPLICATION;

        event.setPhaseId(phaseId);

        return event;
    }


    // Tests whether we should perform immediate processing.  Note
    // that we "inherit" immediate from the parent if not specified
    // on the behavior.
    private static boolean isImmediate(UIComponent component,
                                       AjaxBehavior ajaxBehavior) {

        boolean immediate = false;

        if (ajaxBehavior.isImmediateSet()) {
            immediate = ajaxBehavior.isImmediate();
        } else if (component instanceof EditableValueHolder) {
            immediate = ((EditableValueHolder)component).isImmediate();
        } else if (component instanceof ActionSource) {
            immediate = ((ActionSource)component).isImmediate();
        }

        return immediate;
    }
    private static String buildAjaxCommand(ClientBehaviorContext behaviorContext,
                                           AjaxBehavior ajaxBehavior) {

        FacesContext context = behaviorContext.getFacesContext();

        // First things first - if AjaxBehavior is disabled, we are done.
        if (ajaxBehavior.isDisabled()) {
            return null;
        }        

        UIComponent component = behaviorContext.getComponent();
        String eventName = behaviorContext.getEventName();

        StringBuilder ajaxCommand = new StringBuilder(256);
        Collection<String> execute = ajaxBehavior.getExecute();
        Collection<String> render = ajaxBehavior.getRender();
        String onevent = ajaxBehavior.getOnevent();
        String onerror = ajaxBehavior.getOnerror();
        String sourceId = behaviorContext.getSourceId();
        Collection<ClientBehaviorContext.Parameter> params = behaviorContext.getParameters();

        ajaxCommand.append("mojarra.ab(");

        if (sourceId == null) {
            ajaxCommand.append("this");
        } else {
            ajaxCommand.append("'");
            ajaxCommand.append(sourceId);
            ajaxCommand.append("'");
        }

        ajaxCommand.append(",event,'");
        ajaxCommand.append(eventName);
        ajaxCommand.append("',");

        appendIds(component, ajaxCommand, execute);
        ajaxCommand.append(",");
        appendIds(component, ajaxCommand, render);

        if ((onevent != null) || (onerror != null) || !params.isEmpty())  {

            ajaxCommand.append(",{");

            if (onevent != null) {
                RenderKitUtils.appendProperty(ajaxCommand, "onevent", onevent, false);
            }

            if (onerror != null) {
                RenderKitUtils.appendProperty(ajaxCommand, "onerror", onerror, false);
            }

            if (!params.isEmpty()) {
                for (ClientBehaviorContext.Parameter param : params) {
                    RenderKitUtils.appendProperty(ajaxCommand, 
                                                  param.getName(),
                                                  param.getValue());
                }
            }
             
            ajaxCommand.append("}");
        }

        ajaxCommand.append(")");

        return ajaxCommand.toString();
    }

    // Appends an ids argument to the ajax command
    private static void appendIds(UIComponent component,
                                  StringBuilder builder,
                                  Collection<String> ids) {

        if ((null == ids) || ids.isEmpty()) {
            builder.append('0');
            return;
        }

        builder.append("'");

        boolean first = true;

        for (String id : ids) {
            if (!first) {
                builder.append(' ');
            } else {
                first = false;
            }

            if (id.equals("@all") || id.equals("@none") ||
                id.equals("@form") || id.equals("@this")) {
                builder.append(id);
            } else {
                builder.append(getResolvedId(component, id));
            }
        }

        builder.append("'");
    }

    // Returns the resolved (client id) for a particular id.
    private static String getResolvedId(UIComponent component, String id) {

        UIComponent resolvedComponent = findComponent(component, id);
        if (resolvedComponent == null) {
            // RELEASE_PENDING  i18n
            throw new FacesException(
                "<f:ajax> contains an unknown id '"
                + id
                + "'");
        }

        return resolvedComponent.getClientId();
    }
    /**
     * Attempt to find the component assuming the ID is relative to the
     * nearest naming container.  If not found, then search for the component
     * using an absolute component expression.
     */
    private static UIComponent findComponent(UIComponent component,
                                             String exe) {

        // RELEASE_PENDING - perhaps only enable ID validation if ProjectStage
        // is development
        UIComponent resolvedComponent = component.findComponent(exe);
        if (resolvedComponent == null) {
            // not found using a relative search, try an absolute search
            resolvedComponent = component.findComponent(':' + exe);
        }
        return resolvedComponent;

    }
}
