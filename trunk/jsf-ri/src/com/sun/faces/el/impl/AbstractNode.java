/* Generated By:JJTree: Do not edit this line. SimpleNode.java */

package com.sun.faces.el.impl;

public abstract class AbstractNode implements Node, JsfParserTreeConstants
{
    protected Node[] children;

    protected int id;

    protected String image;

    protected Node parent;

    public AbstractNode(int i)
    {
        id = i;
    }

    /** Accept the visitor. * */
    public Object childrenAccept(JsfParserVisitor visitor, Object data)
            throws javax.faces.el.EvaluationException
    {
        if (children != null)
        {
            for (int i = 0; i < children.length; ++i)
            {
                children[i].jjtAccept(visitor, data);
            }
        }
        return data;
    }

    /*
     * Override this method if you want to customize how the node dumps out its
     * children.
     */

    public void dump(String prefix)
    {
        System.out.println(toString(prefix));
        if (children != null)
        {
            for (int i = 0; i < children.length; ++i)
            {
                AbstractNode n = (AbstractNode) children[i];
                if (n != null)
                {
                    n.dump(prefix + "  ");
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj instanceof AbstractNode)
        {
            return this.hashCode() == obj.hashCode();
        }
        return super.equals(obj);
    }

    /**
     * @return Returns the image.
     */
    public String getImage()
    {
        return image;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (this.image != null) return this.image.hashCode();
        return super.hashCode();
    }

    /** Accept the visitor. * */
    public abstract Object jjtAccept(JsfParserVisitor visitor, Object data)
            throws javax.faces.el.EvaluationException;

    public void jjtAddChild(Node n, int i)
    {
        if (children == null)
        {
            children = new Node[i + 1];
        }
        else if (i >= children.length)
        {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.faces.el.implNode#jjtClose()
     */
    public void jjtClose()
    {
    }

    public Node jjtGetChild(int i)
    {
        return children[i];
    }

    public int jjtGetNumChildren()
    {
        return (children == null) ? 0 : children.length;
    }

    public Node jjtGetParent()
    {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sun.faces.el.implNode#jjtOpen()
     */
    public void jjtOpen()
    {
    }

    public void jjtSetParent(Node n)
    {
        parent = n;
    }

    /**
     * @param image
     *            The image to set.
     */
    public void setImage(String image)
    {
        this.image = image;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */

    public String toString()
    {
        return JsfParserTreeConstants.jjtNodeName[id]
                + ((this.image != null) ? "[" + this.image + "]" : "");
    }

    public String toString(String prefix)
    {
        return prefix + toString();
    }
}

