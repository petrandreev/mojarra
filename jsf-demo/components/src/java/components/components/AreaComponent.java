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

package components.components;


import components.model.ImageArea;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import java.io.IOException;


/**
 * <p>{@link AreaComponent} is a JavaServer Faces component that represents
 * a particular hotspot in a client-side image map defined by our parent
 * {@link MapComponent}.  The <code>valueRef</code> property (if present)
 * must point at a JavaBean of type <code>components.model.ImageArea</code>;
 * if not present, an <code>ImageArea</code> instance will be synthesized
 * from the values of the <code>alt</code>, <code>coords</code>, and
 * <code>shape</code> properties, and assigned to the <code>value</code>
 * property.</p>
 */

public class AreaComponent extends UIOutput {


    // ------------------------------------------------------ Instance Variables


    private String alt = null;
    private String coords = null;
    private String shape = null;
    private String targetImage = null;



    // -------------------------------------------------------------- Properties


    /**
     * <p>Return the alternate text for our synthesized {@link ImageArea}.</p>
     */
    public String getAlt() {
        return (this.alt);
    }


    /**
     * <p>Set the alternate text for our synthesized {@link ImageArea}.</p>
     *
     * @param alt The new alternate text
     */
    public void setAlt(String alt) {
        this.alt = alt;
    }


    /**
     * <p>Return the hotspot coordinates for our synthesized {@link ImageArea}.
     * </p>
     */
    public String getCoords() {
        return (this.coords);
    }


    /**
     * <p>Set the hotspot coordinates for our synthesized {@link ImageArea}.</p>
     *
     * @param coords The new coordinates
     */
    public void setCoords(String coords) {
        this.coords = coords;
    }


    /**
     * <p>Return the shape for our synthesized {@link ImageArea}.</p>
     */
    public String getShape() {
        return (this.shape);
    }


    /**
     * <p>Set the shape for our synthesized {@link ImageArea}.</p>
     *
     * @param shape The new shape (default, rect, circle, poly)
     */
    public void setShape(String shape) {
        this.shape = shape;
    }


    /**
     * <p>Set the image that is the target of this <code>AreaComponent</code>.</p>
     *
     * @return the target image of this area component.
     */
    public String getTargetImage() {
        return targetImage;
    }


    /**
     * <p>Set the image that is the target of this <code>AreaComponent</code>.</p>
     *
     * @param targetImage the ID of the target of this <code>AreaComponent</code>
     */
    public void setTargetImage(String targetImage) {
        this.targetImage = targetImage;
    }


    /**
     * <p>Return the component family for this component.</p>
     */
    public String getFamily() {

        return ("Area");

    }

    // -------------------------------------------------------- UIOutput Methods


    /**
     * <p>Synthesize and return an {@link ImageArea} bean for this hotspot,
     * if there is no <code>valueRef</code> property on this component.</p>
     */
    public Object getValue() {

        if (super.getValue() == null) {
            setValue(new ImageArea(getAlt(), getCoords(), getShape()));
        }
        return (super.getValue());

    }


    // ----------------------------------------------------- StateHolder Methods


    /**
     * <p>Return the state to be saved for this component.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     */

    public Object saveState(FacesContext context) {
        Object values[] = new Object[5];
        values[0] = super.saveState(context);
        values[1] = alt;
        values[2] = coords;
        values[3] = shape;
        values[4] = targetImage;
        return (values);
    }


    /**
     * <p>Restore the state for this component.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param state   State to be restored
     *
     * @throws IOException if an input/output error occurs
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        alt = (String) values[1];
        coords = (String) values[2];
        shape = (String) values[3];
        targetImage = (String) values[4];
    }


}
