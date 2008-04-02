/*
 * $Id: Node.java,v 1.3 2004/02/05 16:22:48 rlubke Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *    
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *  
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *  
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

package components.model;


import java.util.ArrayList;
import java.util.Iterator;


/**
 * <p>Node is a JavaBean representing a node in a tree control or menu.</p>
 */

public class Node {


    // ----------------------------------------------------------- Constructors


    // No-args constructor
    public Node() {
        super();
    }


    // Full-up constructor
    public Node(String name, String label, String action, String icon,
                boolean enabled, boolean expanded) {
        setName(name);
        setLabel(label);
        setAction(action);
        setIcon(icon);
        setEnabled(enabled);
        setExpanded(expanded);
    }


    // ----------------------------------------------------- Instance Variables

    /**
     * Maintains a list of all the child nodes of this node.
     */
    private ArrayList children = new ArrayList();


    // ------------------------------------------------------------- Properties


    /**
     * The <code>Graph</code> instance representing the
     * entire tree.
     */
    protected Graph graph = null;


    void setGraph(Graph graph) {
        this.graph = graph;
    }


    public Graph getGraph() {
        return graph;
    }


    /*
     * Node action (context-relative URL triggered when node selected)
     */
    private String action = null;


    public String getAction() {
        return (this.action);
    }


    public void setAction(String action) {
        this.action = action;
    }


    private String icon = null;


    /*
     * Icon for this node if any.
     */
    public String getIcon() {
        return (this.icon);
    }


    public void setIcon(String icon) {
        this.icon = icon;
    }


    /*
     * Returns the number of children of this Node.
     */
    public int getChildCount() {
        return (children.size());
    }


    /*
     * The nesting depth of this Node
     */
    private int depth = 1;


    public int getDepth() {
        return (this.depth);
    }


    /*
     * Is this node currently enabled (available for use by the user)?
     */
    private boolean enabled = false;


    public boolean isEnabled() {
        return (this.enabled);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    /*
     * Is this node currently expanded (in a tree control) or open (in a menu)?
     */
    private boolean expanded = false;


    public boolean isExpanded() {
        return (this.expanded);
    }


    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }


    /*
     * Node label (visible representation)
     */
    private String label = null;


    public String getLabel() {
        return (this.label);
    }


    public void setLabel(String label) {
        this.label = label;
    }


    /*
     * Node name
     */
    private String name = null;


    public String getName() {
        return (this.name);
    }


    public void setName(String name) {
        this.name = name;
    }


    /*
     * The parent Node
     */
    private Node parent = null;


    public Node getParent() {
        return (this.parent);
    }


    void setParent(Node parent) {
        this.parent = parent;
        if (parent == null) {
            depth = 1;
        } else {
            depth = parent.getDepth() + 1;
        }
    }


    /*
     * Returns the absolute path of this node
     */
    public String getPath() {

        Node parent = getParent();
        if (parent == null) {
            return ("/");
        }

        ArrayList list = new ArrayList();
        list.add(getName());
        while (parent != null) {
            list.add(0, parent.getPath());
            parent = parent.getParent();
        }

        StringBuffer sb = new StringBuffer();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            if (i != 1) {
                sb.append("/");
            }
            if (i > 0) {
                sb.append((String) list.get(i));
            }
        }
        return (sb.toString());

    }


    /*
     * Is this node the currently selected one in the entire tree?
     */
    private boolean selected = false;


    public boolean isSelected() {
        return (this.selected);
    }


    void setSelected(boolean selected) {
        this.selected = selected;
    }


    /**
     * Is this the last node in the set of children for our parent node?
     */
    protected boolean last = false;


    public boolean isLast() {
        return (this.last);
    }


    void setLast(boolean last) {
        this.last = last;
    }


    /**
     * Is this a "leaf" node (i.e. one with no children)?
     */
    public boolean isLeaf() {
        synchronized (children) {
            return (children.size() < 1);
        }
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Adds the specified node as a child of this node and sets this node
     * as its parent.
     */
    public void addChild(Node child) {
        if (child.getParent() != null) {
            throw new IllegalArgumentException("Child already has a parent");
        }
        // if graph is created after all the nodes are created, then
        // graph will be null.
        if (graph != null) {
            graph.addNode(child);
        }
        synchronized (children) {
            children.add(child);
        }
        child.setParent(this);

        int n = children.size();
        if (n > 0) {
            Node node = (Node) children.get(n - 1);
            node.setLast(false);
        }
        child.setLast(true);
    }


    /**
     * Adds the specified node as a child of this node at the
     * specifed offset and sets this node as its parent.
     */
    public void addChild(int offset, Node child) {
        if (child.getParent() != null) {
            throw new IllegalArgumentException("Child already has a parent");
        }
        if (graph != null) {
            graph.addNode(child);
        }
        synchronized (children) {
            children.add(offset, child);
        }
        child.setParent(this);
    }


    /**
     * Returns the node with the specified name by looking up
     * by child list. If node is not found returns <code>null</code>
     */
    public Node findChild(String name) {
        int n = children.size();
        for (int i = 0; i < n; i++) {
            Node kid = (Node) children.get(i);
            if (name.equals(kid.getName())) {
                return (kid);
            }
        }
        return (null);
    }


    /**
     * Returns and <code>iterator</code> over the children of this node.
     */
    public Iterator getChildren() {
        return (children.iterator());
    }


    /**
     * Removes the specified node from the child list of this node.
     */
    public void removeChild(Node child) {
        if (child.getParent() != this) {
            throw new IllegalArgumentException(
                "Child not related to this node");
        }
        synchronized (children) {
            children.remove(child);
        }
        graph.removeNode(child);
        child.setParent(null);
    }


    /**
     * Returns a string representing a description of this node.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Node[name=");
        sb.append(name);
        if (label != null) {
            sb.append(",label=");
            sb.append(label);
        }
        if (action != null) {
            sb.append(",action=");
            sb.append(action);
        }
        sb.append(",enabled=");
        sb.append(enabled);
        sb.append(",expanded=");
        sb.append(expanded);
        sb.append("]");
        return sb.toString();
    }


}
