/*
 * $Id: ButtonRenderer.java,v 1.72 2004/02/03 00:52:23 jvisvanathan Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// ButtonRenderer.java

package com.sun.faces.renderkit.html_basic;

import com.sun.faces.util.Util;

import java.util.Map;
import java.util.MissingResourceException;

import javax.faces.component.UIComponent;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import java.io.IOException;

import com.sun.faces.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <B>ButtonRenderer</B> is a class that renders the current value of 
 * <code>UICommand<code> as a Button.
 */

public class ButtonRenderer extends HtmlBasicRenderer {
    //
    // Protected Constants
    //
    // Log instance for this class
     protected static Log log = LogFactory.getLog(ButtonRenderer.class);

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

    //
    // Class methods
    //

    //
    // General Methods
    //

    /** Follow the UE Spec for Button:
     * http://javaweb.sfbay.sun.com/engineering/jsue/j2ee/WebServices/
     * JavaServerFaces/uispecs/UICommand_Button.html
     */
    protected String padLabel(String label) {
	if (label.length() == 3) {
            label = "\u00a0\u00a0" + label + "\u00a0\u00a0";
	} else if (label.length() == 2) {
	    label = "\u00a0\u00a0\u00a0" + label + "\u00a0\u00a0\u00a0";
	}
	return label;
    }

    //
    // Methods From Renderer
    //

    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null) {
	    throw new NullPointerException(Util.getExceptionMessage(
				    Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
        if (log.isTraceEnabled()) {
            log.trace("Begin decoding component " + component.getId());
        }
        
        // If the component is disabled, do not change the value of the
        // component, since its state cannot be changed.
        if (Util.componentIsDisabledOnReadonly(component)) {
            if (log.isTraceEnabled()) {
                log.trace("No decoding necessary since the component " + 
                    component.getId() + " is disabled");
            }
            return;
        } 


        // Was our command the one that caused this submission?
        // we don' have to worry about getting the value from request parameter
        // because we just need to know if this command caused the submission. We
        // can get the command name by calling currentValue. This way we can 
        // get around the IE bug.
        String clientId = component.getClientId(context);
        Map requestParameterMap = context.getExternalContext().getRequestParameterMap();
        String value = (String)requestParameterMap.get(clientId);
        if (value == null) {
            if (requestParameterMap.get(clientId+".x") == null &&
                requestParameterMap.get(clientId+".y") == null) {
                return;
            }
        }

        String type = (String) component.getAttributes().get("type");
        if ((type != null) && (type.toLowerCase().equals("reset")) ) {
            return;
        }
        ActionEvent actionEvent = new ActionEvent(component);
	component.queueEvent(actionEvent);
        
        if (log.isDebugEnabled()) {
            log.debug("This command resulted in form submission " +
                    " ActionEvent queued " + actionEvent);
        }
        if (log.isTraceEnabled()) {
            log.trace("End decoding component " + component.getId());
        }
        return;
    }
    
    public void encodeBegin(FacesContext context, UIComponent component) 
             throws IOException  {
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(
                    Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
        if (log.isTraceEnabled()) {
            log.trace("Begin encoding component " + component.getId());
        }
        // suppress rendering if "rendered" property on the component is
        // false.
        if (!component.isRendered()) {
            if (log.isTraceEnabled()) {
                log.trace("End encoding component " + component.getId() + 
                " since rendered attribute is set to false ");
            }
            return;
        }
        
        // Which button type (SUBMIT, RESET, or BUTTON) should we generate?
        String type = (String) component.getAttributes().get("type");
	    String styleClass = null;
        if (type == null) {
            type = "submit";
	    // This is needed in the decode method
	    component.getAttributes().put("type", type);
        }

        ResponseWriter writer = context.getResponseWriter();
        Util.doAssert(writer != null );
        
        String label = "";
        Object value = ((UICommand) component).getValue();
        if (value != null) {
              label = value.toString();
        } 
        String imageSrc = (String) component.getAttributes().get("image");
        writer.startElement("input", component);
	writeIdAttributeIfNecessary(context, writer, component);
        if (imageSrc != null) {
            writer.writeAttribute("type", "image", "type");
            writer.writeURIAttribute("src", imageSrc, "image");
            writer.writeAttribute("name", component.getClientId(context), "clientId");
         } else {
            writer.writeAttribute("type", type.toLowerCase(), "type");
            writer.writeAttribute("name", component.getClientId(context), "clientId");
            writer.writeAttribute("value", padLabel(label), "value");
         }

        Util.renderPassThruAttributes(writer, component);
        Util.renderBooleanPassThruAttributes(writer, component);

        if (null != (styleClass = (String) 
            component.getAttributes().get("styleClass"))) {
            writer.writeAttribute("class", styleClass, "styleClass");
	}
        writer.endElement("input");
        if (log.isTraceEnabled()) {
            log.trace("End encoding component " + component.getId());
        }
    }
    
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(
                    Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) 
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException(Util.getExceptionMessage(
                    Util.NULL_PARAMETERS_ERROR_MESSAGE_ID));
        }
    }

    //
    // General Methods
    //


} // end of class ButtonRenderer
