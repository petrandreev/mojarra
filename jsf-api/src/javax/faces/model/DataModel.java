/*
 * $Id: DataModel.java,v 1.18 2005/12/05 16:42:58 edburns Exp $
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

package javax.faces.model;


import java.util.ArrayList;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.UIData;


/**
 * <p><strong>DataModel</strong> is an abstraction around arbitrary data
 * binding technologies that can be used to adapt a variety of data sources
 * for use by JavaServer Faces components that support per-row processing
 * for their child components (such as {@link UIData}.</p>
 *
 * <p>The data collection underlying a {@link DataModel} instance is
 * modeled as a collection of row objects that can be accessed by
 * a zero-relative cursor (row index).  The APIs provide mechanisms to
 * position to a specified zero-relative row index, and to retrieve an
 * object that represents the data that corresponds to the current
 * row index.</p>
 *
 * <p>A concrete {@link DataModel} instance is attached to a particular
 * collection of underlying data by calling the <code>setWrappedData()</code>
 * method.  It can be detached from that underlying data collection by
 * passing a <code>null</code> parameter to this method.</p>
 *
 * <p>Concrete {@link DataModel} implementations must provide a public
 * zero-arguments constructor that calls <code>setWrappedData(null)</code>.
 * A convenience constructor that takes a wrapped object of the appropriate
 * type (and passes it on via a call to <code>setWrappedData()</code>,
 * should also be provided.</p>
 *
 * <p>Event listeners may be registered to receive notifications
 * of when a new row index is selected.</p>
 */

public abstract class DataModel {


    // -------------------------------------------------------------- Properties


    /**
     * <p>Return a flag indicating whether there is <code>rowData</code>
     * available at the current <code>rowIndex</code>.  If no
     * <code>wrappedData</code> is available, return <code>false</code>.</p>
     *
     * @throws FacesException if an error occurs getting the row availability
     */
    public abstract boolean isRowAvailable();


    /**
     * <p>Return the number of rows of data objects represented by this
     * {@link DataModel}.  If the number of rows is unknown, or no
     * <code>wrappedData</code> is available, return -1.</p>
     *
     * @throws FacesException if an error occurs getting the row count
     */
    public abstract int getRowCount();


    /**
     * <p>Return an object representing the data for the currenty selected
     * row index.  If no <code>wrappedData</code> is available, return
     * <code>null</code>.</p>
     *
     * @throws FacesException if an error occurs getting the row data
     * @throws IllegalArgumentException if now row data is available
     *  at the currently specified row index
     */
    public abstract Object getRowData();


    /**
     * <p>Return the zero-relative index of the currently selected row.  If
     * we are not currently positioned on a row, or no <code>wrappedData</code>
     * is available, return -1.</p>
     *
     * @throws FacesException if an error occurs getting the row index
     */
    public abstract int getRowIndex();


    /**
     * <p>Set the zero-relative index of the currently selected row, or -1
     * to indicate that we are not positioned on a row.  It is
     * possible to set the row index at a value for which the underlying data
     * collection does not contain any row data.  Therefore, callers may
     * use the <code>isRowAvailable()</code> method to detect whether row data
     * will be available for use by the <code>getRowData()</code> method.</p>
     *
     * <p>If there is no <code>wrappedData</code> available when this method
     * is called, the specified <code>rowIndex</code> is stored (and may be
     * retrieved by a subsequent call to <code>getRowData()</code>), but no
     * event is sent.  Otherwise, if the currently selected row index is
     * changed by this call, a {@link DataModelEvent} will be sent to the
     * <code>rowSelected()</code> method of all registered
     * {@link DataModelListener}s.</p>
     *
     * @param rowIndex The new zero-relative index (must be non-negative)
     *
     * @throws FacesException if an error occurs setting the row index
     * @throws IllegalArgumentException if <code>rowIndex</code>
     *  is less than -1
     */
    public abstract void setRowIndex(int rowIndex);


    /**
     * <p>Return the object representing the data wrapped by this
     * {@link DataModel}, if any.</p>
     */
    public abstract Object getWrappedData();


    /**
     * <p>Set the object representing the data collection wrapped by this
     * {@link DataModel}.  If the specified <code>data</code> is
     * <code>null</code>, detach this {@link DataModel} from any previously
     * wrapped data collection instead.</p>
     *
     * <p>If <code>data</code> is non-<code>null</code>, the currently selected
     * row index must be set to zero, and a {@link DataModelEvent} must be sent
     * to the <code>rowSelected()</code> method of all registered
     * {@link DataModelListener}s indicating that this row is now selected.</p>
     *
     * @param data Data collection to be wrapped, or <code>null</code> to
     *  detach from any previous data collection
     *
     * @throws ClassCastException if <code>data</code> is not of the
     *  appropriate type for this {@link DataModel} implementation
     */
    public abstract void setWrappedData(Object data);



    // ------------------------------------------------------ Instance Variables


    /**
     * <p>The list of registered {@link DataModelListener}s for this
     * {@link DataModel}.  This variable will be <code>null</code> unless
     * there is at least one registered listener.</p>
     */
    private List<DataModelListener> listeners = null;


    // --------------------------------------------- Event Listener Registration


    /**
     * <p>Add a new {@link DataModelListener} to the set interested in
     * notifications from this {@link DataModel}.</p>
     *
     * @param listener The new {@link DataModelListener} to be registered
     *
     * @throws NullPointerException if <code>listener</code>
     *  is <code>null</code>
     */
    public void addDataModelListener(DataModelListener listener) {

        if (listener == null) {
            throw new NullPointerException();
        }
        if (listeners == null) {
            listeners = new ArrayList<DataModelListener>();
        }
        listeners.add(listener);

    }


    /**
     * <p>Return the set of {@link DataModelListener}s interested in
     * notifications from this {@link DataModel}.  If there are no such
     * listeners, an empty array is returned.</p>
     */
    public DataModelListener[] getDataModelListeners() {

	if (listeners == null) {
	    return (new DataModelListener[0]);
	} else {
	    return ((DataModelListener[]) listeners.toArray
		    (new DataModelListener[listeners.size()]));
	}

    }


    /**
     * <p>Remove an existing {@link DataModelListener} from the set
     * interested in notifications from this {@link DataModel}.</p>
     *
     * @param listener The old {@link DataModelListener} to be deregistered
     *
     * @throws NullPointerException if <code>listener</code>
     *  is <code>null</code>
     */
    public void removeDataModelListener(DataModelListener listener) {

        if (listener == null) {
            throw new NullPointerException();
        }
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.size() == 0) {
                listeners = null;
            }
        }

    }


}
