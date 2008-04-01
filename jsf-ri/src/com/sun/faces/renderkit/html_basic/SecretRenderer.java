/*
 * $Id: SecretRenderer.java,v 1.2 2001/11/17 01:33:00 edburns Exp $
 *
 * Copyright 2000-2001 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

// SecretRenderer.java

package com.sun.faces.renderkit.html_basic;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.OutputMethod;
import javax.faces.RenderContext;
import javax.faces.Renderer;
import javax.faces.WTextEntry;
import javax.faces.WComponent;

import org.mozilla.util.Assert;
import org.mozilla.util.Debug;
import org.mozilla.util.Log;
import org.mozilla.util.ParameterCheck;

/**
 *
 *  <B>SecretRenderer</B> is a class ...
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: SecretRenderer.java,v 1.2 2001/11/17 01:33:00 edburns Exp $
 * 
 * @see	Blah
 * @see	Bloo
 *
 */

public class SecretRenderer extends Object implements Renderer
{
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

    public SecretRenderer()
    {
        super();
        // ParameterCheck.nonNull();
        this.init();
    }

    protected void init()
    {
        // super.init();
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
    
    public Iterator getSupportedAttributeNames(String componentType) {
        return null;
    }

    public void renderStart(RenderContext rc, WComponent c )
            throws IOException { 
        try {
            WTextEntry textField = null;
            if ( c instanceof WTextEntry ) {
                textField = (WTextEntry) c;
            }
            OutputMethod outputMethod = rc.getOutputMethod();
            StringBuffer output = new StringBuffer();
            output.append("<input type=\"password\"");
            
            // render name of the component if specified
            String textField_name = (String)textField.getAttribute(rc, "name");
            if ( textField_name != null ) {
                output.append(" name=\"");
                output.append(textField_name);
                output.append("\"");
            }
            // render default text specified
            String textField_value = (String)textField.getValue();
            if ( textField_value != null ) {
                output.append(" value=\"");
                output.append(textField_value);
                output.append("\"");
            }
            //render size if specified
            String textField_size = (String)textField.getAttribute(rc, "size");
            if ( textField_size != null ) {
                output.append(" size=\"");
                output.append(textField_size);
                output.append("\"");
            }
            //render maxlength if specified 
            String textField_ml = (String)textField.getAttribute(rc, "maxlength");
            if ( textField_ml != null ) {
                output.append(" maxlength=\"");
                output.append(textField_ml);
                output.append("\"");
            }
            output.append(">");
            outputMethod.writeText(output.toString());
            outputMethod.flush();
            
        } catch(IOException ioe) {
            System.err.println("Error rendering Password field: " + ioe);
        }
        return;
    }

    public void renderChildren(RenderContext rc, WComponent c) 
            throws IOException {
        return;
    }

    public void renderEnd(RenderContext rc, WComponent c) 
            throws IOException {
        return;
    }

    public boolean supportsType(String componentType) {
        return true;
    }
    
    public boolean supportsType(WComponent c) {
        return true;
    }



} // end of class SecretRenderer
