/*
 * $Id: RadioRenderer.java,v 1.43 2003/08/08 16:20:23 rkitain Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// RadioRenderer.java

package com.sun.faces.renderkit.html_basic;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.FacesException;
import javax.faces.component.UISelectOne;
import javax.faces.component.SelectItem;
import javax.faces.context.ResponseWriter;

import com.sun.faces.util.Util;
import com.sun.faces.util.SelectItemWrapper;

import java.io.IOException;

import org.mozilla.util.Assert;

/**
 * <B>ReadoRenderer</B> is a class that renders the current value of 
 * <code>UISelectOne<code> or <code>UISelectMany<code> component as a list of 
 * radio buttons
 */

public class RadioRenderer extends HtmlBasicInputRenderer {
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

    public RadioRenderer() {
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

   protected void getEndTextToRender(FacesContext context, UIComponent component,
            String currentValue) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Assert.assert_it(writer != null );
        UISelectOne uiSelectOne = (UISelectOne) component;
	String alignStr = null;
	String borderStr = null;
	String selectoneClass = null;
	boolean alignVertical = false;
	int border = 0;

        // cast component to UISelectOne.
        Object curValue = uiSelectOne.currentValue(context);
        Iterator items = Util.getSelectItemWrappers(context, uiSelectOne);
	SelectItem curItem = null;
        SelectItemWrapper curItemWrapper = null;
        if ( items == null ) {
            return;
        }
        
        if (null != (alignStr = (String) uiSelectOne.getAttribute("layout"))) {
	    alignVertical = alignStr.equalsIgnoreCase("PAGE_DIRECTION") ? 
		true : false;
	}
	if (null != (borderStr = (String) uiSelectOne.getAttribute("border"))){
	    try {
		border = Integer.valueOf(borderStr).intValue();
	    }
	    catch (Throwable e) {
		border = 0;
	    }
	}
	if (null != (selectoneClass = (String) 
		     component.getAttribute("selectoneClass"))) {
	    writer.startElement("span");
	    writer.writeAttribute("class", selectoneClass);
	}
	
        writer.startElement("table");
        writer.writeAttribute("border", new Integer(border));
        writer.writeText('\n');

	if (!alignVertical) {
            writer.writeText('\t');
	    writer.startElement("tr");
	    writer.writeText('\n');
	}

	while (items.hasNext()) {
	    curItemWrapper = (SelectItemWrapper) items.next();
            curItem = curItemWrapper.getSelectItem();
	    if (alignVertical) {
                writer.writeText('\t');
		writer.startElement("tr");
		writer.writeText('\n');
	    }
	    writer.startElement("td");
	    writer.startElement("input");
	    writer.writeAttribute("type", "radio");
            if (null != curItem.getValue() &&
		curItem.getValue().equals(curValue)){
		writer.writeAttribute("checked", new Boolean("true"));
            }
	    writer.writeAttribute("name", uiSelectOne.getClientId(context));
	    writer.writeAttribute("value",(getFormattedValue(context, component,
	        curItem.getValue())));

	    Util.renderPassThruAttributes(writer, curItemWrapper.getUISelectItem());
	    Util.renderBooleanPassThruAttributes(writer, curItemWrapper.getUISelectItem());

            String itemLabel = curItem.getLabel();
            if (itemLabel != null) {
                writer.writeText(" ");
		writer.writeText(itemLabel);
            }
	    writer.endElement("td");
	    writer.writeText('\n');
	    if (alignVertical) {
	        writer.writeText('\t');
		writer.startElement("tr");
		writer.writeText('\n');
	    }
        }

	if (!alignVertical) {
	    writer.writeText('\t');
	    writer.endElement("tr");
	    writer.writeText('\n');
	}
        writer.endElement("table");

	if (null != selectoneClass) {
	    writer.endElement("span");
	}
    }

} // end of class RadioRenderer
