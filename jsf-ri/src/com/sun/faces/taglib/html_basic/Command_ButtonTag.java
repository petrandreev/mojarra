/*
 * $Id: Command_ButtonTag.java,v 1.19 2002/01/25 18:45:18 visvan Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// Command_ButtonTag.java

package com.sun.faces.taglib.html_basic;

import com.sun.faces.util.Util;

import org.mozilla.util.Assert;
import org.mozilla.util.Debug;
import org.mozilla.util.Log;
import org.mozilla.util.ParameterCheck;

import javax.faces.Constants;
import javax.faces.FacesException;
import javax.faces.RenderContext;
import javax.faces.Renderer;
import javax.faces.RenderKit;
import javax.faces.UICommand;
import javax.faces.ObjectManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 *  <B>Command_ButtonTag</B> is a class ...
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: Command_ButtonTag.java,v 1.19 2002/01/25 18:45:18 visvan Exp $
 * 
 * @see	Blah
 * @see	Bloo
 *
 */

public class Command_ButtonTag extends TagSupport {
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

    private String image = null;
    private String id = null;
    private String label = null;
    private String commandName = null;
    private String scope = null;
    private String commandListener = null;
    
    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //

    public Command_ButtonTag()
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Returns the value of commandListener attribute
     *
     * @return String value of commandListener attribute
     */
    public String getCommandListener() {
        return this.commandListener;
    }

    /**
     * Sets commandListener attribute
     * @param command_listener value of commandListener attribute
     */
    public void setCommandListener(String command_listener) {
        this.commandListener = command_listener;
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
     * Returns the value of the commandName attribute
     *
     * @return String value of commandName attribute
     */
    public String getCommandName() {
        return this.commandName;
    }

    /**
     * Sets commandName attribute
     * @param cmd_name value of commandName attribute
     */
    public void setCommandName(String cmd_name) {
        this.commandName = cmd_name;
    }

    /**
     * Process the start of this tag.
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        Assert.assert_it( pageContext != null );
        ObjectManager objectManager = (ObjectManager) pageContext.getServletContext().
                getAttribute(Constants.REF_OBJECTMANAGER);
        Assert.assert_it( objectManager != null );
        RenderContext renderContext = 
            (RenderContext)objectManager.get(pageContext.getSession(),
            Constants.REF_RENDERCONTEXT);
        Assert.assert_it( renderContext != null );
        
        UICommand uiCommand = null;

        // 1. if we don't have an "id" generate one
        //
        if (id == null) {
            String gId = Util.generateId();
            setId(gId);
        }

        // 2. Get or create the component instance.
        //
        uiCommand = (UICommand) objectManager.get(pageContext.getRequest(), id);
        if ( uiCommand == null ) {
            uiCommand = new UICommand();
            addToScope(uiCommand, objectManager);
        }

        uiCommand.setId(getId());
        uiCommand.setAttribute("image", getImage());
        uiCommand.setAttribute("label", getLabel());
        
        try {
            uiCommand.addCommandListener(commandListener);    
        } catch (FacesException fe) {
            throw new JspException("Listener + " + commandListener +
                " doesn not exist or does not implement commandListener " + 
                " interface" );
        }

        // 3. Render the component.
        //
        try {
            uiCommand.setRendererType("ButtonRenderer");
            uiCommand.render(renderContext);
        } catch (java.io.IOException e) {
            throw new JspException("Problem rendering component: "+
                e.getMessage());
        } catch (FacesException f) {
            throw new JspException("Problem rendering component: "+
                f.getMessage());
        }
        return (EVAL_BODY_INCLUDE);
    }
    
    /**
     * End Tag Processing
     */
    public int doEndTag() throws JspException{

        Assert.assert_it( pageContext != null );
        // get ObjectManager from ServletContext.
        ObjectManager objectManager = (ObjectManager)pageContext.getServletContext().
                 getAttribute(Constants.REF_OBJECTMANAGER);
        Assert.assert_it( objectManager != null );
        RenderContext renderContext = 
            (RenderContext)objectManager.get(pageContext.getSession(),
            Constants.REF_RENDERCONTEXT);
        Assert.assert_it( renderContext != null );

//PENDING(rogerk)can we eliminate this extra get if wCommand is instance
//variable? If so, threading issue?
//
        UICommand wCommand = (UICommand) objectManager.get(pageContext.getRequest(), id);
        Assert.assert_it( wCommand != null );

        // Complete the rendering process
        //
        try {
            wCommand.renderComplete(renderContext);
        } catch (java.io.IOException e) {
            throw new JspException("Problem completing rendering: "+
                e.getMessage());
        } catch (FacesException f) {
            throw new JspException("Problem completing rendering: "+
                f.getMessage());
        }

        return EVAL_PAGE;
    }

    /**
     * Tag cleanup method.
     */
    public void release() {

        super.release();

        image = null;
        id = null;
        label = null;
        commandName = null;
        scope = null;
        commandListener = null;
    }

    /** Adds the component and listener to the ObjectManager
     * in the appropriate scope
     *
     * @param c UIComponent to be stored in namescope
     * @param objectManager Object pool
     */
    public void addToScope(UICommand c, ObjectManager objectManager) {
   
        // PENDING ( visvan ) right now, we are not saving the state of the
        // components. So if the scope is specified as reques, when the form
        // is resubmitted we would't be able to retrieve the state of the
        // components. So to get away with that we are storing in session
        // scope. This should be fixed later.
        objectManager.put(pageContext.getSession(), id, c);
    }

    
 
} // end of class Command_ButtonTag
