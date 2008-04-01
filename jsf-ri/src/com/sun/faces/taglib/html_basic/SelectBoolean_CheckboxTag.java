/*
 * $Id: SelectBoolean_CheckboxTag.java,v 1.10 2001/12/12 20:41:59 visvan Exp $
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

// SelectBoolean_CheckboxTag.java

package com.sun.faces.taglib.html_basic;

import org.mozilla.util.Assert;
import org.mozilla.util.Debug;
import org.mozilla.util.Log;
import org.mozilla.util.ParameterCheck;

import javax.faces.Constants;
import javax.faces.FacesException;
import javax.faces.RenderContext;
import javax.faces.Renderer;
import javax.faces.RenderKit;
import javax.faces.WForm;
import javax.faces.WSelectBoolean;
import javax.faces.ObjectTable;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 *  <B>SelectBoolean_CheckboxTag</B> is a class ...
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: SelectBoolean_CheckboxTag.java,v 1.10 2001/12/12 20:41:59 visvan Exp $
 * 
 * @see	Blah
 * @see	Bloo
 *
 */

public class SelectBoolean_CheckboxTag extends TagSupport {
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

    private String checked = null;
    private String name = null;
    private String value = null;
    private String label = null;
    private String model = null;
    private String scope = null;
    private String valueChangeListener = null;

    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //

    public SelectBoolean_CheckboxTag() {
        super();
        // ParameterCheck.nonNull();
        this.init();
    }

    protected void init() {
        // super.init();
    }

    //
    // Class methods
    //

    //
    // General Methods
    //
    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the value of valueChangeListener attribute
     *
     * @return String value of valueChangeListener attribute
     */
    public String getValueChangeListener() {
        return this.valueChangeListener;
    }

    /**
     * Sets valueChangeListener attribute
     * @param change_listener value of formListener attribute
     */
    public void setValueChangeListener(String change_listener) {
        this.valueChangeListener = change_listener;
    }

    /**
     * Returns the value of the scope attribute
     *
     * @return String value of scope attribute
     */
    public String getScope() {
        return this.scope;
    }

    /**
     * Sets scope attribute
     * @param scope value of scope attribute
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the value of the model attribute
     *
     * @return String value of model attribute
     */
    public String getModel() {
        return this.model;
    }

    /**
     * Sets the model attribute
     * @param model value of model attribute
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Process the start of this tag.
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        Assert.assert_it( pageContext != null );
        ObjectTable ot = (ObjectTable) pageContext.getServletContext().
                getAttribute(Constants.REF_OBJECTTABLE);
        Assert.assert_it( ot != null );
        RenderContext renderContext = 
            (RenderContext)ot.get(pageContext.getSession(),
            Constants.REF_RENDERCONTEXT);
        Assert.assert_it( renderContext != null );

        if (name != null) {

            // 1. Get or create the component instance.
            //
            WSelectBoolean wSelectBoolean = (WSelectBoolean) 
                ot.get(pageContext.getRequest(), name);
            if ( wSelectBoolean == null ) {
                wSelectBoolean = createComponent(renderContext);
                addToScope(wSelectBoolean, ot);
            }

            // 2. Get a RenderKit and associated Renderer for this
            //    component.
            //
            RenderKit renderKit = renderContext.getRenderKit();
            if (renderKit == null) {
                throw new JspException("Can't determine RenderKit!");
            }

            Renderer renderer = null;
            try {
                renderer = renderKit.getRenderer(
                    "com.sun.faces.renderkit.html_basic.CheckboxRenderer");
            } catch (FacesException e) {
                throw new JspException(
                    "FacesException!!! " + e.getMessage());
            }

            if (renderer == null) {
                throw new JspException(
                    "Could not determine 'renderer' for component");
            }

            // 3. Render the component. (Push the component on
            //    the render stack first).
            //
            try {
                renderContext.pushChild(wSelectBoolean);
                renderer.renderStart(renderContext, wSelectBoolean);
//PENDING(rogerk) complet/pop should be done in doEndTag
//
                renderer.renderComplete(renderContext, wSelectBoolean);
                renderContext.popChild();
            } catch (java.io.IOException e) {
                throw new JspException("Problem rendering component: "+
                    e.getMessage());
            } catch (FacesException f) {
                throw new JspException("Problem rendering component: "+
                    f.getMessage());
            }
        }
        return (EVAL_BODY_INCLUDE);
    }

    /**
     * Creates a TextEntry component and sets renderer specific
     * properties.
     *
     * @param rc renderContext client information
     */
    protected WSelectBoolean createComponent(RenderContext renderContext) 
            throws JspException {

        WSelectBoolean wSelectBoolean = new WSelectBoolean();

        // set renderer specific properties
        wSelectBoolean.setAttribute(renderContext, "name", getName());
        wSelectBoolean.setAttribute(renderContext, "value", getValue());
        wSelectBoolean.setAttribute(renderContext, "label", getLabel());

        // If model attribute is not found get it
        // from parent form if it exists. If not
        // set text as an attribute so that it can be
        // used during rendering.

        // PENDING ( visvan )
        // make sure that the model object is registered
        if ( model != null ) {
            wSelectBoolean.setModel(model);
        } else {
            // PENDING ( visvan ) all tags should implement a common
            // interface ??
            FormTag ancestor = null;
            try {
                ancestor = (FormTag) findAncestorWithClass(this,
                    FormTag.class);
               String model_str = ancestor.getModel();
               if ( model_str != null ) {
                   model = "$" + model_str + "." + name;
                   wSelectBoolean.setModel(model);
               }
            } catch ( Exception e ) {
                // If form tag cannot be found then model is null
            }
        }
        if ( checked != null ) {
             boolean state = (Boolean.valueOf(checked)).booleanValue();
             wSelectBoolean.setSelected(renderContext, state);
        }
        return wSelectBoolean;
    }

    /** Adds the component and listener to the ObjectTable
     * in the appropriate scope
     *
     * @param c WComponent to be stored in namescope
     * @param ot Object pool
     */
    public void addToScope(WSelectBoolean c, ObjectTable ot) {
   
        // PENDING ( visvan ) right now, we are not saving the state of the
        // components. So if the scope is specified as reques, when the form
        // is resubmitted we would't be able to retrieve the state of the
        // components. So to get away with that we are storing in session
        // scope. This should be fixed later.
        ot.put(pageContext.getSession(), name, c);

        if ( valueChangeListener != null ) {
            String lis_name = name.concat(Constants.REF_VALUECHANGELISTENERS);
            Vector listeners = (Vector) ot.get(pageContext.getRequest(), lis_name);
            if ( listeners == null) {
                listeners = new Vector();
            }
            // this vector contains only the name of the listeners. The
            // listener itself is stored in the objectTable. We do this
            // because if the listeners are stored in the components, then
            // they have to exist for the event listeners to be dispatched
            // at the time we process the events.
            // According to the spec, listeners should be dispatched
            // independent of components.
            listeners.add(valueChangeListener);
            ot.put(pageContext.getSession(),lis_name, listeners);
        }
    }

    /**
     * End Tag Processing
     */
    public int doEndTag() throws JspException{

        return EVAL_PAGE;
    }

} // end of class SelectBoolean_CheckboxTag
