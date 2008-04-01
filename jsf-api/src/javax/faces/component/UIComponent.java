/*
 * $Id: UIComponent.java,v 1.4 2002/05/08 01:11:46 craigmcc Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.component;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.context.FacesContext;
import javax.faces.render.AttributeDescriptor;
import javax.faces.render.Renderer;


/**
 * <p><strong>UIComponent</strong> is the base class for all user interface
 * components in JavaServer Faces.  The set of <code>UIComponent</code>
 * instances associated with a particular request or response are typically
 * organized into trees (with the root node, and all intervening nodes,
 * being instances of {@link UIContainer}).</p>
 *
 * <h3>Properties</h3>
 *
 * <p>Each <code>UIComponent</code> instance supports the following
 * JavaBean properties to describe its render-independent characteristics:</p>
 * <ul>
 * <li><strong>componentId</strong> (java.lang.String) - An identifier for this
 *     component, which must be unique across all children of the parent
 *     {@link UIContainer}.  Identifiers may be composed of letters, digits,
 *     dashes ('-'), and underscores ('_').  To minimize the size of
 *     responses rendered by JavaServer Faces, it is recommended that
 *     identifiers be as short as possible.</li>
 * <li><strong>componentType</strong> - The canonical name of the component
 *     type represented by this <code>UIComponent</code> instance.  For all
 *     standard component types, this value is represented by a manifest
 *     constant String named <code>TYPE</code> in the implementation class.
 *     To facilitate introspection by tools, it is recommended that user
 *     defined <code>UIComponent</code> subclasses follow the same
 *     convention.</li>
 * <li><strong>compoundId</strong> (java.lang.String) - A unique (within
 *     the component tree containing this component) identifier for the
 *     current node, which begins with a slash character ('/'), followed by
 *     the <code>id</code> of each parent of the current component (from the
 *     top down) followed by a slash character ('/'), and ending with the
 *     <code>id</code> of this component.  [READ-ONLY]</li>
 * <li><strong>model</strong> (java.lang.STring) - A symbolic expression
 *     used to attach this component to <em>model</em> data in the underlying
 *     application (typically a JavaBean property).  The syntax of this
 *     expression corresponds to the expression language described in
 *     Appendix A of the <em>JavaServer Pages Standard Tag Library</em>
 *     (version 1.0) specification.</li>
 * <li><strong>parent</strong> (javax.faces.component.UIContainer) - The
 *     parent {@link UIContainer} in which this <code>UIComponent</code> is
 *     nested.  The root {@link UIContainer} will not have a parent.</li>
 * <li><strong>rendererType</strong> (java.lang.String) - Logical identifier
 *     of the type of {@link Renderer} to use when rendering this component
 *     to a response.  If not specified, this component must render itself
 *     directly in the <a href="#render(javax.faces.context.FacesContext)">
 *     render()</a> method.</li>
 * <li><strong>value</strong> - The local value of this
 *     <code>UIComponent</code>, which represents a server-side cache of the
 *     value most recently entered by a user.  <strong>FIXME</strong> -
 *     discussions about when this value is cleared, how validation and
 *     caching of converted values works, and so on.</li>
 * </ul>
 *
 * <h3>Attributes</h3>
 *
 * <p>Each <code>UIComponent</code> instance supports a set of dynamically
 * defined <em>attributes</em>, normally used to describe the render-dependent
 * characteristics of the component.  The set of supported attribute names
 * (and types) for a particular <code>UIComponent</code> subclass can be
 * introspected (for example, by development tools), through a call to the
 * <code>getAttributeNames()</code> and <code>getAttributeDescriptor</code>
 * methods.  However, if the <code>rendererType</code></p>
 *
 * <h3>Component Trees and Navigation</h3>
 *
 * <p>A component whose implementation class extends {@link UIContainer}
 * instead of <code>UIComponent</code> supports the association of child
 * components with that container.  When applied recursively, a set of
 * related components can be assembled into a <em>tree</em> with a single
 * parent component (which must be a {@link UIContainer} if it has children)
 * and an arbitrary number of child components at each level.</p>
 *
 * <p>Further, a unique (within a component tree) identifier, accessible
 * via the <code>compoundId</code> read-only property, can be calculated
 * for each component in the tree.  The syntax and semantics of compound
 * identifiers match the corresponding notions in operating system filesystems,
 * as well as URL schemes that support hierarchical identifiers (such as
 * <code>http</code>), where a leading slash character ('/') identifies
 * the root of the component tree, and subordinate nodes of the tree are
 * selected by their <code>id</code> property followed by a slash.</p>
 *
 * <p><code>UIComponent</code> supports navigation from one component to
 * another, within the component tree containing this component, using
 * absolute and relative path expressions.  See
 * <a href="#findComponent(java.lang.String)">findComponent()</a> for
 * more information.</p>
 *
 * <h3>Other Stuff</h3>
 *
 * <p><strong>FIXME</strong> - Lots more about lifecycle, etc.</p>
 *
 * <p><strong>FIXME</strong> - Should all standard implementations of
 * <code>UIComponent</code> be required to be able to render themselves
 * in the absence of a {@link RenderKit}?  How about custom components?</p>
 */

public abstract class UIComponent {


    /**
     * <p>The component type of this <code>UIComponent}</code>.</p>
     */
    public static final String TYPE = "Component";


    // ------------------------------------------------------------- Attributes


    /**
     * <p>Return the value of the attribute with the specified name,
     * which may be <code>null</code>.</p>
     *
     * @param name Name of the requested attribute
     *
     * @exception IllegalArgumentException if <code>name</code> does not
     *  identify an attribute supported by this component
     * @exception NullPointerException if <code>name</code> is
     *  <code>null</code>
     */
    public abstract Object getAttribute(String name);


    /**
     * <p>Return an {@link AttributeDescriptor} describing a
     * render-dependent attribute that is supported by this component,
     * or <code>null</code> if no attribute by this name is supported
     * by this component.</p>
     *
     * <p><strong>FIXME</strong> - The set of attributes supported by a
     * component is dependent on the RenderKit (if any) in use.</p>
     *
     * @param name Name of the requested attribute
     *
     * @exception NullPointerException if <code>name</code> is
     *  <code>null</code>
     */
    public abstract AttributeDescriptor getAttributeDescriptor(String name);


    /**
     * <p>Return an <code>Iterator</code> over the names of all
     * render-dependent attributes supported by this <code>UIComponent</code>.
     * For each such attribute, descriptive information can be retrieved
     * via <code>getAttributeDescriptor()</code>, and the current value
     * can be retrieved via <code>getAttribute()</code>.</p>
     */
    public abstract Iterator getAttributeNames();


    /**
     * <p>Set the new value of the attribute with the specified name,
     * replacing any existing value for that name.</p>
     *
     * @param name Name of the requested attribute
     * @param value New value (which may be <code>null</code>)
     *
     * @exception IllegalArgumentException if <code>name</code> does not
     *  identify an attribute supported by this component
     * @exception NullPointerException if <code>name</code>
     *  is <code>null</code>
     */
    public abstract void setAttribute(String name, Object value);


    // ------------------------------------------------------------- Properties


    /**
     * <p>Return the identifier of this <code>UIComponent</code>.</p>
     */
    public abstract String getComponentId();


    /**
     * <p>Set the identifier of this <code>UIComponent</code>.
     *
     * @param id The new identifier
     *
     * @exception IllegalArgumentException if <code>id</code> is zero length
     *  or contains invalid characters
     * @exception NullPointerException if <code>id</code> is <code>null</code>
     */
    public abstract void setComponentId(String id);


    /**
     * <p>Return the component type of this <code>UIComponent</code>.</p>
     */
    public abstract String getComponentType();


    /**
     * <p>Return the <em>compound identifier</em> of this component.</p>
     */
    public abstract String getCompoundId();


    /**
     * <p>Return the symbolic model reference expression of this
     * <code>UIComponent</code>, if any.</p>
     */
    public abstract String getModel();


    /**
     * <p>Set the symbolic model reference expression of this
     * <code>UIComponent</code>.</p>
     *
     * @param model The new symbolic model reference expression, or
     *  <code>null</code> to disconnect this component from any model data
     */
    public abstract void setModel(String model);


    /**
     * <p>Return the parent {@link UIContainer} of this
     * <code>UIComponent</code>, if any.</p>
     */
    public abstract UIContainer getParent();


    /**
     * <p>Set the parent {@link UIContainer} of this
     * <code>UIComponent</code>.</p>
     *
     * @param parent The new parent, or <code>null</code> for the root node
     *  of a component tree
     */
    public abstract void setParent(UIContainer parent);


    /**
     * <p>Return the {@link Renderer} type for this <code>UIComponent</code>
     * (if any).</p>
     */
    public abstract String getRendererType();


    /**
     * <p>Set the {@link Renderer} type for this <code>UIComponent</code>,
     * or <code>null</code> for components that render themselves.</p>
     *
     * @param rendererType Logical identifier of the type of
     *  {@link Renderer} to use, or <code>null</code> for components
     *  that render themselves
     */
    public abstract void setRendererType(String rendererType);


    /**
     * <p>Return the local value of this <code>UIComponent</code>.</p>
     */
    public abstract Object getValue();


    /**
     * <p>Set the local value of this <code>UIComponent</code>.</p>
     *
     * @param value The new local value
     */
    public abstract void setValue(Object value);


    // ----------------------------------------------------- Navigation Methods


    /**
     * <p>Find a related component in the current component tree by evaluating
     * the specified navigation expression (which may be absolute or relative)
     * to locate the requested component, which is then returned.
     * Valid expression values are:</p>
     * <ul>
     * <li><em>Absolute Path</em> (<code>/a/b/c</code>) - Expressions that
     *     start with a slash begin at the root component of the current tree,
     *     and match exactly against the <code>compoundId</code> of the
     *     selected component.</li>
     * <li><em>Root Component</em> - (<code>/</code>) - An expression with
     *     only a slash selects the root component of the current tree.</li>
     * <li><em>Relative Path</em> - (<code>a/b</code>) - Start at the current
     *     component (rather than the root), and navigate downward.</li>
     * <li><em>Special Path Elements</em> - A path element with a single
     *     period (".") selects the current component, while a path with two
     *     periods ("..") selects the parent of the current node.</li>
     * </ul>
     *
     * @param expr Navigation expression to interpret
     *
     * @exception IllegalArgumentException if the syntax of <code>expr</code>
     *  is invalid
     * @exception IllegalArgumentException if <code>expr</code> attempts to
     *  cause navigation to a component that does not exist
     * @exception NullPointerException if <code>expr</code>
     *  is <code>null</code>
     */
    public abstract UIComponent findComponent(String expr);



    // ------------------------------------------- Lifecycle Processing Methods


    /**
     * <p>Extract new values for this <code>UIComponent</code> (if any) from
     * the specified {@link FacesContext}.  This method is called during the
     * <em>Apply Request Values</em> phase of {@link Lifecycle} processing of
     * the curernt request.</p>
     *
     * <p><strong>FIXME</strong> - Specify how components can queue up
     * application events to be processed by components during the next phase.
     * </p>
     *
     * <p><strong>FIXME</strong> - Any need for exceptions here?</p>
     *
     * @param context FacesContext for the current request being processed
     */
    public abstract void applyRequestValues(FacesContext context);


    /**
     * <p>Process all events queued for this <code>UIComponent</code> during
     * the <em>Apply Request Values</em> phase that was performed previously.
     * This method is called during the <em>Handle Request Events</em>
     * phase of the {@link Lifecycle} processing of the current request.</p>
     *
     * <p>If so desired, a component can signal that lifecycle control should
     * be transferred directly to the <em>Render Response</em> phase
     * (<strong>FIXME</strong> - specify the mechanism for this), once
     * all event processing on all components has been completed.</p>
     *
     * <p><strong>FIXME</strong> - Specify how a component gets access to the
     * events that have been queued to it.</p>
     *
     * @param context FacesContext for the current request being processed
     */
    public abstract void handleRequestEvents(FacesContext context);


    /**
     * <p>Perform all validations that have been registered for this
     * <code>UIComponent</code>.  In general, component validation should
     * include an attempt to convert the local value to the data type that
     * will ultimately be required, if appropriate.</p>
     *
     * <p>If a component detects one or move validation errors, it can
     * enqueue a set of message objects to a message queue that can be used
     * in the rendered response (<p><strong>FIXME</strong> - Specify the
     * mechanism for doing this).</p>
     *
     * @param context FacesContext for the current request being processed
     */
    public abstract void processValidations(FacesContext context);


    /**
     * <p>Render this component to the response we are creating.  This
     * method will be called in the <em>Render Response</em> phase of the
     * request processing lifecycle, byt <strong>only</strong> for components
     * that have no value set for the <code>rendererType</code> property.</p>
     *
     * <p><strong>FIXME</strong> - Not sufficient for components with children,
     * but that will be defined in {@link UIContainer} instead of here</p>
     *
     * @param context FacesContext for the current request being processed
     *
     * @exception IOException if an input/output error occurs while rendering
     * @exception NullPointerException if <code>context</code>
     *  is <code>null</code>
     */
    public abstract void render(FacesContext context) throws IOException;


    /**
     * <p>Update any model data associated with this <code>UIComponent</code>
     * via the <code>model</code> property, and clear the local value.  If
     * there is no model data associated with this component, no action
     * is performed.</p>
     *
     * @param context FacesContext for the current request being processed
     */
    public abstract void updateModelValues(FacesContext context);


}
