/*
 * $Id: ValueExpressionValueBindingAdapter.java,v 1.1 2005/05/05 21:55:41 edburns Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package @package@;

import javax.el.ValueExpression;
import javax.el.ELException;
import javax.el.ELContext;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import javax.faces.component.StateHolder;

import java.io.Serializable;

/**
 * <p>Wrap a ValueBinding instance and expose it as a
 * ValueExpression.</p>
 */

@protection@ class ValueExpressionValueBindingAdapter extends ValueExpression implements Serializable, StateHolder {

    public ValueExpressionValueBindingAdapter() {}

    private ValueBinding binding = null;

    @protection@ ValueExpressionValueBindingAdapter(ValueBinding binding) {
	assert(null != binding);
	this.binding = binding;
    }

    //
    // Methods from ValueExpression
    //

    public Object getValue(ELContext context) throws ELException {
	assert(null != binding);
	Object result = null;
	FacesContext facesContext = (FacesContext) 
	    context.getContext(FacesContext.class);
	assert(null != facesContext);
	try {
	    result = binding.getValue(facesContext);
	}
	catch (Throwable e) {
	    throw new ELException(e);
	}
	return result;
    }

    public void setValue(ELContext context, Object value) throws ELException {
	assert(null != binding);
	FacesContext facesContext = (FacesContext) 
	    context.getContext(FacesContext.class);
	assert(null != facesContext);
	try {
	    binding.setValue(facesContext, value);
	}
	catch (Throwable e) {
	    throw new ELException(e);
	}
    }


    public boolean isReadOnly(ELContext context) throws ELException {
	assert(null != binding);
	boolean result = false;
	FacesContext facesContext = (FacesContext) 
	    context.getContext(FacesContext.class);
	assert(null != facesContext);
	try {
	    result = binding.isReadOnly(facesContext);
	}
	catch (Throwable e) {
	    throw new ELException(e);
	}
	return result;
    }

    public Class getType(ELContext context) throws ELException {
	assert(null != binding);
	Class result = null;
	FacesContext facesContext = (FacesContext) 
	    context.getContext(FacesContext.class);
	assert(null != facesContext);
	try {
	    result = binding.getType(facesContext);
	}
	catch (Throwable e) {
	    throw new ELException(e);
	}
	return result;
    }

    /**
     * <p>Always return <code>false</code> since we can't possibly know
     * if this is a literal text binding or not.</p>
     */

    public boolean isLiteralText() {
	return false;
    }
    
    public Class getExpectedType() {
	assert(null != binding);
	Class result = null;
	FacesContext context = FacesContext.getCurrentInstance();
	try {
	    Object value = binding.getValue(context);
	    result = value.getClass();
	}
	catch (Throwable e) {
	    result = null;
	}
	return result;
    }

    public String getExpressionString() {
	assert(null != binding);
	return binding.getExpressionString();
	
    }

    public boolean equals(Object other) {
	assert(null != binding);
	boolean result = false;
	
	// don't bother even trying to compare, if we're not assignment
	// compatabile with "other"
	if (ValueExpression.class.isAssignableFrom(other.getClass())) {
	    ValueExpression otherVE = (ValueExpression) other;
	    result = this.getExpressionString().equals(otherVE.getExpressionString());
	}
	return result;
    }

    public int hashCode() {
	assert(null != binding);

	return binding.hashCode();
    }
    
    public String getDelimiterSyntax() {
        // PENDING (visvan) Implementation
        return "";
    }
    
    // 
    // Methods from StateHolder
    //

    

    public Object saveState(FacesContext context) {
	Object result = null;
	if (!tranzient) {
	    if (binding instanceof StateHolder) {
		Object [] stateStruct = new Object[2];
		
		// save the actual state of our wrapped binding
		stateStruct[0] = ((StateHolder)binding).saveState(context);
		// save the class name of the binding impl
		stateStruct[1] = binding.getClass().getName();

		result = stateStruct;
	    }
	    else {
		result = binding;
	    }
	}

	return result;
    }

    public void restoreState(FacesContext context, Object state) {
	// if we have state
	if (null == state) {
	    return;
	}
	
	if (!(state instanceof ValueBinding)) {
	    Object [] stateStruct = (Object []) state;
	    Object savedState = stateStruct[0];
	    String className = stateStruct[1].toString();
	    ValueBinding result = null;
	    
	    Class toRestoreClass = null;
	    if (null != className) {
		try {
		    toRestoreClass = loadClass(className, this);
		}
		catch (ClassNotFoundException e) {
		    throw new IllegalStateException(e.getMessage());
		}
		
		if (null != toRestoreClass) {
		    try {
			result = 
			    (ValueBinding) toRestoreClass.newInstance();
		    }
		    catch (InstantiationException e) {
			throw new IllegalStateException(e.getMessage());
		    }
		    catch (IllegalAccessException a) {
			throw new IllegalStateException(a.getMessage());
		    }
		}
		
		if (null != result && null != savedState) {
		    // don't need to check transient, since that was
		    // done on the saving side.
		    ((StateHolder)result).restoreState(context, savedState);
		}
		binding = result;
	    }
	}
	else {
	    binding = (ValueBinding) state;
	}
    }

    private boolean tranzient = false;

    public boolean isTransient() {
	return tranzient;
    }

    public void setTransient(boolean newTransientValue) {
	tranzient = newTransientValue;
    }

    //
    // Helper methods for StateHolder
    //

    private static Class loadClass(String name, 
            Object fallbackClass) throws ClassNotFoundException {
        ClassLoader loader =
            Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = fallbackClass.getClass().getClassLoader();
        }
        return loader.loadClass(name);
    }
 

    // 
    // methods used by classes aware of this class's wrapper nature
    //

    public ValueBinding getWrapped() {
	return binding;
    }

}
