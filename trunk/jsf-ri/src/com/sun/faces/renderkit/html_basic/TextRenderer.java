/*
 * $Id: TextRenderer.java,v 1.29 2002/08/20 20:00:52 eburns Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// TextRenderer.java

package com.sun.faces.renderkit.html_basic;

import com.sun.faces.util.Util;

import java.util.Iterator;

import javax.faces.component.AttributeDescriptor;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.mozilla.util.Assert;
import org.mozilla.util.Debug;
import org.mozilla.util.Log;
import org.mozilla.util.ParameterCheck;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import com.sun.faces.RIConstants;

/**
 *
 *  <B>TextRenderer</B> is a class ...
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: TextRenderer.java,v 1.29 2002/08/20 20:00:52 eburns Exp $
 * 
 * @see	Blah
 * @see	Bloo
 *
 */

public class TextRenderer extends HtmlBasicRenderer {
    //
    // Protected Constants
    //

    //
    // Class Variables
    //

    //
    // Instance Variables
    //

    // Attribute Instance Variables


    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //

    public TextRenderer() {
        super();
    }

    //
    // Class methods
    //

    //
    // General Methods
    //

    //
    // Methods From Renderer
    //
    public boolean supportsComponentType(String componentType) {
        if ( componentType == null ) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }    
        return (componentType.equals(UIInput.TYPE) ||
            componentType.equals(UIOutput.TYPE));
    }

    public void decode(FacesContext context, UIComponent component) 
            throws IOException {
        Object convertedValue = null;
        Class modelType = null;
        
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
        
        if (!(component instanceof UIInput)) {
            // do nothing in output case
            return;
        }

        String compoundId = component.getCompoundId();
        Assert.assert_it(compoundId != null );
        
        String newValue = context.getServletRequest().getParameter(compoundId);
       
        component.setValue(newValue);
        component.setValid(true);
        
    }

    public void encodeBegin(FacesContext context, UIComponent component) 
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
    }

    public void encodeChildren(FacesContext context, UIComponent component) {
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) 
            throws IOException {
        String currentValue = null;
	StringBuffer buffer = null;
        ResponseWriter writer = null;
        
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
       
        Object currentObj = component.currentValue(context);
        if ( currentObj != null) {
            if (currentObj instanceof String) {
                currentValue = (String)currentObj;
            } else {
                currentValue = currentObj.toString();
            }
        }

        if (currentValue == null) {
            currentValue = "";
        }    

	buffer = new StringBuffer();
        
        if (UIInput.TYPE == component.getComponentType()) {
            buffer.append("<input type=\"text\"");
            buffer.append(" name=\"");
            buffer.append(component.getCompoundId());
            buffer.append("\"");

            // render default text specified
            if ( currentValue != null ) {
                buffer.append(" value=\"");
                buffer.append(currentValue);
                buffer.append("\"");
            }
            buffer.append(Util.renderPassthruAttributes(context, component));
            buffer.append(Util.renderBooleanPassthruAttributes(context, 
                component));
            buffer.append(">");            
        } else if (UIOutput.TYPE == component.getComponentType()) {
            if (currentValue == null || currentValue == "") {
                try {
                    currentValue = getKeyAndLookupInBundle(context, component,
                                                       "key");
                } catch (java.util.MissingResourceException e) {
                    // Do nothing since the absence of a resource is not an
                    // error.
                    return;
                }
            }

            if (currentValue != null) {
                buffer.append(currentValue);
            }
        }

	// find out if we're nested inside a UIInput
	if (component.getParent().getComponentType() == UIInput.TYPE) {
	    // if so, save our content in the
	    // RIConstants.RENDERED_CONTENT attribute
	    component.setAttribute(RIConstants.RENDERED_CONTENT, 
				   buffer.toString());
	}
	else {
	    writer = context.getResponseWriter();
	    Assert.assert_it(writer != null );
	    writer.write(buffer.toString());
	}
    }
    
    // The testcase for this class is TestRenderers_2.java 

} // end of class TextRenderer


