/*
 * $Id: ByteConverter.java,v 1.4 2003/08/13 18:42:47 craigmcc Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.convert;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


/**
 * <p>{@link Converter} implementation for <code>java.lang.Byte</code>
 * (and byte primitive) values.</p>
 */

public class ByteConverter implements Converter {


    // ------------------------------------------------------- Converter Methods


    public Object getAsObject(FacesContext context, UIComponent component,
                              String value) throws ConverterException {

        try {
            return (Byte.valueOf(value));
        } catch (Exception e) {
            throw new ConverterException(e);
        }


    }


    public String getAsString(FacesContext context, UIComponent component,
                              Object value) throws ConverterException {

        try {
            return (Byte.toString(((Byte) value).byteValue()));
        } catch (Exception e) {
            throw new ConverterException(e);
        }

    }


}
