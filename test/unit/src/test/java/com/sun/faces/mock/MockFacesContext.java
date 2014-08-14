/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2014 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.faces.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import javax.faces.FactoryFinder;
import javax.faces.webapp.PreJsf2ExceptionHandlerFactory;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ExceptionHandler;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.el.ELContext;
import com.sun.faces.renderkit.RenderKitUtils;

// Mock Object for FacesContext
public class MockFacesContext extends FacesContext {

    private static final String POST_BACK_MARKER
            = MockFacesContext.class.getName() + "_POST_BACK";

    private Severity maxSeverity;

    private Map<Object, Object> attributes = null;
    private PartialViewContext partialView = new MockPartialViewContext();

    private boolean released;

    // ------------------------------------------------------------ Constructors
    public MockFacesContext() {
        super();
        setCurrentInstance(this);
    }

    public MockFacesContext(ExternalContext externalContext) {
        setExternalContext(externalContext);
        setCurrentInstance(this);
        elContext = new MockELContext(new MockELResolver());
        elContext.putContext(FacesContext.class, this);
    }

    public MockFacesContext(ExternalContext externalContext, Lifecycle lifecycle) {
        this(externalContext);
    }

    // -------------------------------------------------------------- Properties
    // application
    private Application application = null;

    @Override
    public Application getApplication() {
        return (this.application);
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public Map<Object, Object> getAttributes() {

        if (null == attributes) {
            attributes = new HashMap<Object, Object>();
        }

        return attributes;
    }

    // clientIdsWithMessages
    @Override
    public Iterator getClientIdsWithMessages() {
        return (messages.keySet().iterator());
    }

    private PhaseId currentPhaseId = PhaseId.RESTORE_VIEW;

    @Override
    public PhaseId getCurrentPhaseId() {
        return currentPhaseId;
    }

    @Override
    public void setCurrentPhaseId(PhaseId currentPhaseId) {
        this.currentPhaseId = currentPhaseId;
    }

    private ELContext elContext = null;

    @Override
    public ELContext getELContext() {
        return (this.elContext);
    }

    public void setELContext(ELContext elContext) {
        this.elContext = elContext;
    }

    // externalContext
    private ExternalContext externalContext = null;

    @Override
    public ExternalContext getExternalContext() {
        return (this.externalContext);
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }

    // locale
    private Locale locale = null;

    public Locale getLocale() {
        return (this.locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    // maximumSeverity
    @Override
    public Severity getMaximumSeverity() {
        return maxSeverity;
    }

    // messages
    private Map<String, List<FacesMessage>> messages = new HashMap();

    @Override
    public Iterator getMessages() {
        List<FacesMessage> results = getMessageList();
        return (results.iterator());
    }

    @Override
    public Iterator getMessages(String clientId) {
        List<FacesMessage> list = getMessageList(clientId);
        return (list.iterator());
    }

    @Override
    public List<FacesMessage> getMessageList() {
        ArrayList results = new ArrayList<FacesMessage>();
        Iterator clientIds = messages.keySet().iterator();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            results.addAll((List<FacesMessage>) messages.get(clientId));
        }
        return results;
    }

    @Override
    public List<FacesMessage> getMessageList(String clientId) {
        List<FacesMessage> list = messages.get(clientId);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        return list;
    }

    // renderKit
    @Override
    public RenderKit getRenderKit() {
        UIViewRoot vr = getViewRoot();
        if (vr == null) {
            return (null);
        }
        String renderKitId = vr.getRenderKitId();
        if (renderKitId == null) {
            return (null);
        }
        RenderKitFactory rkFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        return (rkFactory.getRenderKit(this, renderKitId));
    }

    // renderResponse
    private boolean renderResponse = false;

    @Override
    public boolean getRenderResponse() {
        return (this.renderResponse);
    }

    public void setRenderResponse(boolean renderResponse) {
        this.renderResponse = renderResponse;
    }

    // responseComplete
    private boolean responseComplete = false;

    @Override
    public boolean getResponseComplete() {
        return (this.responseComplete);
    }

    public void setResponseComplete(boolean responseComplete) {
        this.responseComplete = responseComplete;
    }

    // responseStream
    private ResponseStream responseStream = null;

    @Override
    public ResponseStream getResponseStream() {
        return (this.responseStream);
    }

    @Override
    public void setResponseStream(ResponseStream responseStream) {
        this.responseStream = responseStream;
    }

    // responseWriter
    private ResponseWriter responseWriter = null;

    @Override
    public ResponseWriter getResponseWriter() {
        return (this.responseWriter);
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    // viewRoot
    private UIViewRoot root = null;

    @Override
    public UIViewRoot getViewRoot() {
        return (this.root);
    }

    @Override
    public void setViewRoot(UIViewRoot root) {
        this.root = root;
    }

    @Override
    public boolean isPostback() {

        Boolean postback = (Boolean) this.getAttributes().get(POST_BACK_MARKER);
        if (postback == null) {
            RenderKit rk = this.getRenderKit();
            if (rk != null) {
                postback = rk.getResponseStateManager().isPostback(this);
            } else {
                // ViewRoot hasn't been set yet, so calculate the RK
                ViewHandler vh = this.getApplication().getViewHandler();
                String rkId = vh.calculateRenderKitId(this);
                postback = RenderKitUtils.getResponseStateManager(this, rkId).isPostback(this);
            }
            this.getAttributes().put(POST_BACK_MARKER, postback);
        }

        return postback.booleanValue();

    }

    @Override
    public boolean isReleased() {
        return released;
    }

    private ExceptionHandler exceptionHandler
            = new PreJsf2ExceptionHandlerFactory().getExceptionHandler();

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    // ---------------------------------------------------------- Public Methods
    @Override
    public void addMessage(String clientId, FacesMessage message) {
        if (message == null) {
            throw new NullPointerException();
        }
        if (maxSeverity == null) {
            maxSeverity = message.getSeverity();
        } else {
            Severity sev = message.getSeverity();
            if (sev.getOrdinal() > maxSeverity.getOrdinal()) {
                maxSeverity = sev;
            }
        }
        List list = (List) messages.get(clientId);
        if (list == null) {
            list = new ArrayList();
            messages.put(clientId, list);
        }
        list.add(message);
    }

    @Override
    public void release() {
        released = true;
        application = null;
        externalContext = null;
        locale = null;
        messages.clear();
        renderResponse = false;
        responseComplete = false;
        responseStream = null;
        responseWriter = null;
        if (null != attributes) {
            attributes.clear();
            attributes = null;
        }
        root = null;
        setCurrentInstance(null);
    }

    @Override
    public void renderResponse() {
        this.renderResponse = true;
    }

    @Override
    public void responseComplete() {
        this.responseComplete = true;
    }

    @Override
    public PartialViewContext getPartialViewContext() {
        return partialView;
    }

    boolean validationFailed = false;

    @Override
    public void validationFailed() {
        validationFailed = true;
    }

    @Override
    public boolean isValidationFailed() {
        return validationFailed;
    }
}
