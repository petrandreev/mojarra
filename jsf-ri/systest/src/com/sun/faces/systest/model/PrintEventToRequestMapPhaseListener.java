/*
 * $Id: PrintEventToRequestMapPhaseListener.java,v 1.1 2004/12/08 15:10:27 edburns Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.systest.model;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseEvent;

import javax.faces.context.FacesContext;

public class PrintEventToRequestMapPhaseListener extends Object implements PhaseListener {

    public void afterPhase(PhaseEvent event) {
	String attr = (String) event.getFacesContext().getExternalContext().getRequestMap().get("afterPhaseEvent");
	if (null == attr) {
	    attr = "";
	}
	event.getFacesContext().getExternalContext().getRequestMap().put("afterPhaseEvent", attr + " afterPhase: " + event.getPhaseId());
    }
    
    
    public void beforePhase(PhaseEvent event) {
	String attr = (String) event.getFacesContext().getExternalContext().getRequestMap().get("beforePhaseEvent");
	if (null == attr) {
	    attr = "";
	}
	event.getFacesContext().getExternalContext().getRequestMap().put("beforePhaseEvent", attr + " beforePhase: " + event.getPhaseId());
    }

    private String phaseIdString = null;

    public void setPhaseIdString(String phaseIdString) {
	this.phaseIdString = phaseIdString;
    }

    /**
     * <p>Look at our phaseIdString ivar.  If non-null, use this as the
     * phaseId for the substring search below.  If null, look in the
     * request map for an attribute called "phaseId".  If not found,
     * return <code>PhaseId.ANY_PHASE</code>.  If found, see if it is a
     * substring of any of the known phases, if so, return the
     * corresponding phase Id.  If not return
     * <code>PhaseId.ANY_PHASE</code>.</p>
     */

    public PhaseId getPhaseId() {

	String phaseId = 
	    (null != phaseIdString) ? phaseIdString : (String)
	    FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("phaseId");
	PhaseId result = PhaseId.ANY_PHASE;
	if (null == phaseId) {
	    return result;
	}
	if (-1 != PhaseId.ANY_PHASE.toString().indexOf(phaseId)) {
	    result = PhaseId.ANY_PHASE;
	}
	else if(-1 !=PhaseId.APPLY_REQUEST_VALUES.toString().indexOf(phaseId)){
	    result = PhaseId.APPLY_REQUEST_VALUES;
	}
	else if(-1 != PhaseId.PROCESS_VALIDATIONS.toString().indexOf(phaseId)){
	    result = PhaseId.PROCESS_VALIDATIONS;
	}
	else if(-1 != PhaseId.UPDATE_MODEL_VALUES.toString().indexOf(phaseId)){
	    result = PhaseId.UPDATE_MODEL_VALUES;
	}
	else if(-1 != PhaseId.INVOKE_APPLICATION.toString().indexOf(phaseId)){
	    result = PhaseId.INVOKE_APPLICATION;
	}
	return result;
    }

    public PhaseListener getInstance() {
	return this;
    }

    protected PhaseListener otherListener = null;
    public PhaseListener getOtherListener() {
	return otherListener;
    }

    public void setOtherListener(PhaseListener newOtherListener) {
	otherListener = newOtherListener;
    }


}
