/*
 * $Id: ValueChangeListenerBean.java,v 1.5 2006/03/29 23:04:02 rlubke Exp $
 */

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * https://javaserverfaces.dev.java.net/CDDL.html or
 * legal/CDDLv1.0.txt. 
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.    
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.faces.systest.model;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.AbortProcessingException;


public class ValueChangeListenerBean extends Object {

    public ValueChangeListenerBean() {
    }

    protected String textAResult;
    public String getTextAResult() {
	return textAResult;
    }

    public void setTextAResult(String newTextAResult) {
	textAResult = newTextAResult;
    }

    protected String textBResult;
    public String getTextBResult() {
	return textBResult;
    }

    public void setTextBResult(String newTextBResult) {
	textBResult = newTextBResult;
    }
    
    public void textAChanged(ValueChangeEvent event) throws AbortProcessingException {
	setTextAResult("Received valueChangeEvent for textA: " + 
		       event.hashCode());
    }

    public void textBChanged(ValueChangeEvent event) throws AbortProcessingException {
	setTextBResult("Received valueChangeEvent for textB: " + 
		       event.hashCode());
    }
}
