/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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


package com.sun.faces.renderkit;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

import com.sun.faces.config.WebConfiguration;
import static com.sun.faces.config.WebConfiguration.WebContextInitParameter.StateSavingMethod;
import com.sun.faces.util.RequestStateManager;


/**
 * <p>A <code>ResonseStateManager</code> implementation
 * for the default HTML render kit.
 */
public class ResponseStateManagerImpl extends ResponseStateManager {

    private StateHelper helper;

    public ResponseStateManagerImpl() {

        WebConfiguration webConfig = WebConfiguration.getInstance();
        String stateMode =
              webConfig.getOptionValue(StateSavingMethod);
        helper = ((StateManager.STATE_SAVING_METHOD_CLIENT.equals(stateMode)
                   ? new ClientSideStateHelper()
                   : new ServerSideStateHelper()));

    }


    // --------------------------------------- Methods from ResponseStateManager


    /**
     * @see {@link ResponseStateManager#isPostback(javax.faces.context.FacesContext)}
     */
    @Override
    public boolean isPostback(FacesContext context) {

        return context.getExternalContext().getRequestParameterMap().
              containsKey(ResponseStateManager.VIEW_STATE_PARAM);

    }


    /**
     * @see {@link javax.faces.render.ResponseStateManager#getState(javax.faces.context.FacesContext, String)}
     */
    @Override
    public Object getState(FacesContext context, String viewId) {

        Object state =
              RequestStateManager.get(context, RequestStateManager.FACES_VIEW_STATE);
        if (state == null) {
            try {
                state = helper.getState(context, viewId);
                if (state != null) {
                    RequestStateManager.set(context,
                                            RequestStateManager.FACES_VIEW_STATE,
                                            state);
                }
            } catch (IOException e) {
                throw new FacesException(e);
            }
        }
        return state;

    }


    /**
     * @see {@link javax.faces.render.ResponseStateManager#writeState(javax.faces.context.FacesContext, Object)}
     */
    @Override
    public void writeState(FacesContext context, Object state)
          throws IOException {

        helper.writeState(context, state, null);

    }


    /**
     * @see {@link javax.faces.render.ResponseStateManager#getViewState(javax.faces.context.FacesContext, Object)}
     */
    @Override
    public String getViewState(FacesContext context, Object state) {

        StringBuilder sb = new StringBuilder(32);
        try {
            helper.writeState(context, state, sb);
        } catch (IOException e) {
            throw new FacesException(e);
        }
        return sb.toString();

    }


    @SuppressWarnings({"deprecation"})
    @Override
    public Object getTreeStructureToRestore(FacesContext context, String viewId) {

        Object[] state = (Object[]) getState(context, viewId);
        if (state != null) {
            return state[0];
        }
        return null;

    }
    
} 