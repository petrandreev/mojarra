/*
 * $Id: DataRenderer.java,v 1.7 2003/04/29 20:51:50 eburns Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.renderkit.html_basic;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import com.sun.faces.util.Util;
/**
 *
 *  DataRenderer is an arbitrary grouping "renderer" with no actual 
 *  output functionality
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: DataRenderer.java,v 1.7 2003/04/29 20:51:50 eburns Exp $
 *  
 */

public class DataRenderer extends HtmlBasicRenderer {
    
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

    public DataRenderer() {
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
             throws IOException{
        // "panel_data" component is just a holder for an Iterator 
        // over a set of model beans.  It doesn't have any rendering behavior of 
        // its own -- that responsibility belongs to the surrounding panel.  
        // See ListRenderer.         
    }   

    public void encodeChildren(FacesContext context, UIComponent component)
        throws IOException {
    }


    public void encodeEnd(FacesContext context, UIComponent component)
        throws IOException {
    }
}
