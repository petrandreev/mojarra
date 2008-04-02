/*
 * $Id: FacetComponent.java,v 1.5 2004/02/04 23:42:32 ofung Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.systest;


import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIOutput;


/**
 * <p>Test <code>UIComponent</code> for sys tests.</p>
 */

public class FacetComponent extends UIOutput {


    public FacetComponent() {
        super();
    }

    public FacetComponent(String id) {
        super();
        setId(id);
    }


    public void encodeEnd(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIOutput facet = (UIOutput) getFacet("header");
        if (facet != null) {
            writer.write("[" + facet.getValue() + "] ");
        } else {
            writer.write("[] ");
        }
        writer.write("[");
        writer.write((String) getValue());
        writer.write("] ");
        facet = (UIOutput) getFacet("footer");
        if (facet != null) {
            writer.write("[" + facet.getValue() + "] ");
        } else {
            writer.write("[] ");
        }
    }


}
