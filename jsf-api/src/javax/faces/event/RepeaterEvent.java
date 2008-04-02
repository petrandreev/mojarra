/*
 * Copyright 2002, 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *    
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *  
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

package javax.faces.event;


import javax.faces.component.Repeater;
import javax.faces.component.UIComponent;


/**
 * <p><strong>RepeaterEvent</strong> is a wrapper for events that are fired by
 * a child component of a {@link Repeater}, for a particular row's
 * instantiation of that component.</p>
 */

public class RepeaterEvent extends FacesEvent {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct a new {@link RepeaterEvent} wrapping the specified
     * source component, event, and row index.</p>
     *
     * @param component Source {@link UIComponent} for this event
     * @param event {@link FacesEvent} being wrapped
     * @param rowIndex Zero-relative row index of the current row for this event
     *
     * @exception IllegalArgumentException if <code>row</code> is negative
     * @exception NullPointerException if <code>component</code> or
     *  <code>event</code> is <code>null</code>
     */
    public RepeaterEvent(UIComponent component, FacesEvent event,
			 int rowIndex) {

	super(component);
	if (event == null) {
	    throw new NullPointerException();
	}
	if (rowIndex < 0) {
	    throw new IllegalArgumentException();
	}
	this.event = event;
	this.rowIndex = rowIndex;

    }


    private FacesEvent event = null;


    private int rowIndex = 0;


    // ---------------------------------------------------------- Public Methods


    /**
     * <p>Return the {@link FacesEvent} wrapped by this {@link RepeaterEvent}.
     * </p>
     */
    public FacesEvent getFacesEvent() {

	return (this.event);

    }


    /**
     * <p>Return the zero-relative row index for this {@link RepeaterEvent}.</p>
     */
    public int getRowIndex() {

	return (this.rowIndex);

    }


    // ------------------------------------------------- Event Broadcast Methods


    public boolean isAppropriateListener(FacesListener listener) {

        return (getFacesEvent().isAppropriateListener(listener));

    }

    /**
     * @exception AbortProcessingException {@inheritDoc}
     */ 
    public void processListener(FacesListener listener) {

        getFacesEvent().processListener(listener);

    }


}
