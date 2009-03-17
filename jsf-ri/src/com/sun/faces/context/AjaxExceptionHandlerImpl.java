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

import java.io.IOException;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.event.PhaseId;
import javax.el.ELException;

import com.sun.faces.util.FacesLogger;


/**
 * <p>
 * A specialized implementation of {@link ExceptionHandler} for JSF 2.0 that
 * handles exceptions by writing error information to the 
 * partial response.
 * </p>
 *
 */
public class AjaxExceptionHandlerImpl extends ExceptionHandlerWrapper {

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
    private ExceptionHandler exceptionHandler = null;


    public AjaxExceptionHandlerImpl(ExceptionHandler handler) {
        this.exceptionHandler = handler;
    }

    public ExceptionHandler getWrapped() {
        return this.exceptionHandler;
    }

    /**
     * @see ExceptionHandlerWrapper@getHandledExceptionQueuedEvent()
     */
    public ExceptionQueuedEvent getHandledExceptionQueuedEvent() {

        return handled;

    }

    /**
     * @see javax.faces.context.ExceptionHandlerWrapper#handle()
     */
    public void handle() throws FacesException {

        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext(); ) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
            try {
                Throwable t = context.getException();
                if (isRethrown(t)) {
                    handled = event;
                    t.printStackTrace();
                    Throwable unwrapped = getRootCause(t);
                    if (unwrapped != null) {
                        handlePartialResponseError(context.getContext(), unwrapped); 
                    } else {
                        if (t instanceof FacesException) {
                            handlePartialResponseError(context.getContext(), t); 
                        } else {
                            handlePartialResponseError(context.getContext(), 
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
     * @see javax.faces.context.ExceptionHandlerWrapper#processEvent(javax.faces.event.SystemEvent)
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
     * @see javax.faces.context.ExceptionHandlerWrapper#getUnhandledExceptionQueuedEvents()
     */
    public Iterable<ExceptionQueuedEvent> getUnhandledExceptionQueuedEvents() {

        return ((unhandledExceptions != null)
                    ? unhandledExceptions
                    : Collections.<ExceptionQueuedEvent>emptyList());

    }


    /**
     * @see javax.faces.context.ExceptionHandlerWrapper#getHandledExceptionQueuedEvents()
     * @return
     */
    public Iterable<ExceptionQueuedEvent> getHandledExceptionQueuedEvents() {

        return ((handledExceptions != null)
                    ? handledExceptions
                    : Collections.<ExceptionQueuedEvent>emptyList());

    }



    // --------------------------------------------------------- Private Methods

     private void handlePartialResponseError(FacesContext context, Throwable t) {
         try {
             PartialResponseWriter writer = context.getPartialViewContext().getPartialResponseWriter();
             writer.startDocument();
             writer.startError(t.getClass().toString());
             if (t.getCause() != null) {
                 writer.write(t.getCause().getMessage());
             } else {
                 writer.write(t.getMessage());
             }
             writer.endError();
             writer.endDocument();
             context.responseComplete();
         } catch (IOException ioe) {
            if (LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.log(Level.SEVERE,
                           ioe.toString(),
                           ioe);
            }
         }
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
