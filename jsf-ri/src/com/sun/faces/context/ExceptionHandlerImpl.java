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

package com.sun.faces.context;

import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.event.PhaseId;
import javax.el.ELException;

import com.sun.faces.util.FacesLogger;
import com.sun.faces.renderkit.RenderKitUtils;


/**
 * <p>
 * The default implementation of {@link ExceptionHandler} for JSF 2.0.
 * </p>
 *
 * <p>
 * As an implementation note, if changes going forward are required here,
 * review the <code>ExceptionHandler</code> implementation within
 * <code>javax.faces.webapp.PreJsf2ExceptionHandlerFactory</code>.  The code
 * is, in most cases, quite similar.
 * </p>
 *
 */
public class ExceptionHandlerImpl extends ExceptionHandler {

    private static final Logger LOGGER = FacesLogger.CONTEXT.getLogger();
    private static final String LOG_BEFORE_KEY =
          "jsf.context.exception.handler.log_before";
    private static final String LOG_AFTER_KEY =
          "jsf.context.exception.handler.log_after";
    private static final String LOG_KEY =
          "jsf.context.exception.handler.log";

    
    private LinkedList<ExceptionQueuedEvent> unhandledExceptions;
    private LinkedList<ExceptionQueuedEvent> handledExceptions;
    private ExceptionQueuedEvent handled;
    private boolean errorPagePresent;


    // ------------------------------------------------------------ Constructors


    public ExceptionHandlerImpl() {

        this.errorPagePresent = true;

    }

    
    public ExceptionHandlerImpl(boolean errorPagePresent) {

        this.errorPagePresent = errorPagePresent;
        
    }


    // ------------------------------------------- Methods from ExceptionHandler


    /**
     * @see ExceptionHandler@getHandledExceptionQueuedEvent()
     */
    public ExceptionQueuedEvent getHandledExceptionQueuedEvent() {

        return handled;

    }


    /**
     * @see javax.faces.context.ExceptionHandler#handle()
     */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public void handle() throws FacesException {

        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext(); ) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            try {
                Throwable t = context.getException();
                if (isRethrown(t)) {
                    handled = event;
                    Throwable unwrapped = getRootCause(t);
                    if (unwrapped != null) {
                        throwIt(context.getContext(),
                                new FacesException(unwrapped.getMessage(), unwrapped));
                    } else {
                        if (t instanceof FacesException) {
                            throwIt(context.getContext(), (FacesException) t);
                        } else {
                            throwIt(context.getContext(),
                                    new FacesException(t.getMessage(), t));
                        }
                    }
                } else {
                    log(context);
                }

            } finally {
                if (handledExceptions == null) {
                    handledExceptions = new LinkedList<ExceptionQueuedEvent>();
                }
                handledExceptions.add(event);
                i.remove();               
            }
        }

    }


    /**
     * @see javax.faces.context.ExceptionHandler#isListenerForSource(Object)
     */
    public boolean isListenerForSource(Object source) {

        return (source instanceof ExceptionQueuedEventContext);

    }


    /**
     * @see javax.faces.context.ExceptionHandler#processEvent(javax.faces.event.SystemEvent)
     */
    public void processEvent(SystemEvent event) throws AbortProcessingException {

        if (event != null) {
            if (unhandledExceptions == null) {
                unhandledExceptions = new LinkedList<ExceptionQueuedEvent>();
            }
            unhandledExceptions.add((ExceptionQueuedEvent) event);
        }

    }


    /**
     * @see ExceptionHandler#getRootCause(Throwable)
     */
    public Throwable getRootCause(Throwable t) {

        if (t == null) {
            return null;
        }
        if (shouldUnwrap(t.getClass())) {
            Throwable root = t.getCause();
            if (root != null) {
                Throwable tmp = getRootCause(root);
                if (tmp == null) {
                    return root;
                } else {
                    return tmp;
                }
            } else {
                return t;
            }
        }
        return t;
        
    }


    /**
     * @see javax.faces.context.ExceptionHandler#getUnhandledExceptionQueuedEvents()
     */
    public Iterable<ExceptionQueuedEvent> getUnhandledExceptionQueuedEvents() {

        return ((unhandledExceptions != null)
                    ? unhandledExceptions
                    : Collections.<ExceptionQueuedEvent>emptyList());

    }


    /**
     * @see javax.faces.context.ExceptionHandler#getHandledExceptionQueuedEvents()
     * @return
     */
    public Iterable<ExceptionQueuedEvent> getHandledExceptionQueuedEvents() {

        return ((handledExceptions != null)
                    ? handledExceptions
                    : Collections.<ExceptionQueuedEvent>emptyList());
        
    }


    // --------------------------------------------------------- Private Methods


    private void throwIt(FacesContext ctx, FacesException fe) {

        if (ProjectStage.Development.equals(ctx.getApplication().getProjectStage()) && !errorPagePresent) {
            // RELEASE_PENDING what about other device types?
            RenderKitUtils.renderHtmlErrorPage(ctx, fe);
        } else {
            throw fe;
        }
    }


    /**
     * @param c <code>Throwable</code> implementation class
     * @return <code>true</code> if <code>c</code> is FacesException.class or
     *  ELException.class
     */
    private boolean shouldUnwrap(Class<? extends Throwable> c) {

        return (FacesException.class.equals(c) || ELException.class.equals(c));

    }

    
    private boolean isRethrown(Throwable t) {

        return (!(t instanceof AbortProcessingException));

    }

    private void log(ExceptionQueuedEventContext exceptionContext) {

        UIComponent c = exceptionContext.getComponent();
        boolean beforePhase = exceptionContext.inBeforePhase();
        boolean afterPhase = exceptionContext.inAfterPhase();
        PhaseId phaseId = exceptionContext.getPhaseId();
        Throwable t = exceptionContext.getException();
        String key = getLoggingKey(beforePhase, afterPhase);
        if (LOGGER.isLoggable(Level.SEVERE)) {
            LOGGER.log(Level.SEVERE,
                       key,
                       new Object[] { t.getClass().getName(),
                                      phaseId.toString(),
                                      ((c != null) ? c.getClientId(exceptionContext.getContext()) : ""),
                                      t.getMessage()});
            LOGGER.log(Level.SEVERE, t.getMessage(), t);
        }

    }

    private String getLoggingKey(boolean beforePhase, boolean afterPhase) {
        if (beforePhase) {
            return LOG_BEFORE_KEY;
        } else if (afterPhase) {
            return LOG_AFTER_KEY;
        } else {
            return LOG_KEY;
        }
    }

}
