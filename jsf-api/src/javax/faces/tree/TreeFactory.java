/*
 * $Id: TreeFactory.java,v 1.5 2002/05/29 19:54:06 craigmcc Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.tree;

import java.util.Iterator;
import javax.faces.FacesException;    // FIXME - subpackage?
import javax.servlet.ServletContext;


/**
 * <p><strong>TreeFactory</strong> is a factory object that creates and
 * returns new {@link Tree} instances.  The component tree associated with
 * the newly created {@link Tree} may be configured and prepopulated based
 * on metadata associated with the tree identifier of the requested tree,
 * in a manner specific to a particular <code>TreeFactory</code>
 * implementation.</p>
 *
 * <p>Implementations of <code>TreeFactory</code> may take advantage of
 * calls to the <code>release()</code> method of the allocated
 * {@link Tree} instances to pool and recycle them, rather than creating
 * a new instance every time.</p>
 *
 * <p>There shall be one <code>TreeFactory</code> instance per web
 * application that is utilizing JavaServer Faces.  This instance can be
 * acquired, in a portable manner, by calling:</p>
 * <pre>
 *   TreeFactory factory = (TreeFactory)
 *    FactoryFinder.createFactory(FactoryFinder.TREE_FACTORY);
 * </pre>
 */

public abstract class TreeFactory {


    /**
     * <p>Construct and return a {@link Tree} that is initialized with the
     * components (and associated properties) for the metadata associated
     * with the specified tree identifier.</p>
     *
     * @param context ServletContext for this web application
     * @param treeId Tree identifier of the tree to be constructed and
     *  returned
     *
     * @exception FacesException if a {@link Tree} cannot be
     *  constructed for the specified parameters
     */
    public abstract Tree createTree(ServletContext context,
                                    String treeId) throws FacesException;


    /**
     * <p>Return an <code>Iterator</code> over the set of tree identifiers
     * of all {@link Tree} instances that can be created by this factory,
     * for this web application.
     * If no trees are supported, an empty <code>Iterator</code> must be
     * returned.</p>
     *
     * @param context ServletContext for this web application
     */
    public abstract Iterator getTreeIds(ServletContext context);


}

