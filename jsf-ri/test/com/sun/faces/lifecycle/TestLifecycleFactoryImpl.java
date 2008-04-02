/*
 * $Id: TestLifecycleFactoryImpl.java,v 1.4 2002/10/07 22:58:01 jvisvanathan Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// TestLifecycleFactoryImpl.java

package com.sun.faces.lifecycle;

import org.apache.cactus.ServletTestCase;

import org.mozilla.util.Assert;
import org.mozilla.util.ParameterCheck;

import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.lifecycle.Lifecycle;
import com.sun.faces.RIConstants;
import java.util.Iterator;

/**
 *
 *  <B>TestLifecycleFactoryImpl</B> is a class ...
 *
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: TestLifecycleFactoryImpl.java,v 1.4 2002/10/07 22:58:01 jvisvanathan Exp $
 * 
 * @see	Blah
 * @see	Bloo
 *
 */

public class TestLifecycleFactoryImpl extends ServletTestCase
{
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

    public TestLifecycleFactoryImpl() {super("TestLifecycleFactoryImpl");}
    public TestLifecycleFactoryImpl(String name) {super(name);}

//
// Class methods
//

//
// General Methods
//

public void testDefault()
{
    LifecycleFactoryImpl factory = new LifecycleFactoryImpl();
    Lifecycle life = null, life2 = null;

    assertTrue(factory != null);

    // Make sure the default instance exists
    life = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    assertTrue(null != life);

    // Make sure multiple requests for the same name give the same
    // instance.
    life2 = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    assertTrue(life == life2);
}

public void testIdIterator()
{
    LifecycleFactoryImpl factory = new LifecycleFactoryImpl();

    String 
	l1 = "l1", 
	l2 = "l2", 
	l3 = "l3";
    LifecycleImpl 
	life1 = new LifecycleImpl(),
	life2 = new LifecycleImpl(),
	life3 = new LifecycleImpl();
    int i = 0;
    Iterator iter = null;

    factory.addLifecycle(l1, life1);
    factory.addLifecycle(l2, life2);
    factory.addLifecycle(l3, life3);

    iter = factory.getLifecycleIds();
    while (iter.hasNext()) {
	iter.next();
	i++;
    }

    assertTrue(4 == i);
}

public void testIllegalStateException()
{
    LifecycleFactoryImpl factory = new LifecycleFactoryImpl();
    Lifecycle life = null;
    boolean exceptionThrown = false;

    assertTrue(factory != null);

    // try to register before create
    try {
	factory.registerBefore(LifecycleFactory.DEFAULT_LIFECYCLE,
			       LifecycleFactoryImpl.LAST_PHASE, 
			       new GenericPhaseImpl(null, RIConstants.RENDER_RESPONSE_PHASE));
    }
    catch (IllegalStateException e) {
	exceptionThrown = true;
    }
    catch (UnsupportedOperationException e) {
	exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
    

    // Make sure the default instance exists
    life = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    assertTrue(null != life);

    exceptionThrown = false;
    // test for IllegalStateException exception
    try {
	factory.registerBefore(LifecycleFactory.DEFAULT_LIFECYCLE,
			       LifecycleFactoryImpl.LAST_PHASE, 
			       new GenericPhaseImpl(life, RIConstants.RENDER_RESPONSE_PHASE));
    }
    catch (IllegalStateException e) {
	exceptionThrown = true;
    }
    catch (UnsupportedOperationException e) {
	exceptionThrown = true;
    }
    assertTrue(exceptionThrown);

}

public void testIllegalArgumentException()
{
    LifecycleFactoryImpl factory = new LifecycleFactoryImpl();
    Lifecycle life = null;
    boolean exceptionThrown = false;

    assertTrue(factory != null);

    // Try to get an IllegalArgumentException
    try {
	factory.registerBefore("BOGUS",
			       LifecycleFactoryImpl.LAST_PHASE, 
			       new GenericPhaseImpl(null, RIConstants.RENDER_RESPONSE_PHASE));
    }
    catch (IllegalArgumentException e) {
	exceptionThrown = true;
    }
    catch (UnsupportedOperationException e) {
	exceptionThrown = true;
    }
    assertTrue(exceptionThrown);

    exceptionThrown = false;
    // Try to get an IllegalArgumentException
    try {
	factory.registerBefore(LifecycleFactory.DEFAULT_LIFECYCLE,
			       LifecycleFactoryImpl.LAST_PHASE + 1, 
			       new GenericPhaseImpl(null, RIConstants.RENDER_RESPONSE_PHASE));
    }
    catch (IllegalArgumentException e) {
	exceptionThrown = true;
    }
    catch (UnsupportedOperationException e) {
	exceptionThrown = true;
    }
    assertTrue(exceptionThrown);

    exceptionThrown = false;
    // Try to get an IllegalArgumentException
    try {
	LifecycleImpl lifecycle = new LifecycleImpl();
	factory.addLifecycle("bogusId", lifecycle);
	factory.addLifecycle("bogusId", lifecycle);
    }
    catch (IllegalArgumentException e) {
	exceptionThrown = true;
    }
    catch (UnsupportedOperationException e) {
	assertTrue(false);
    }
    assertTrue(exceptionThrown);
}


} // end of class TestLifecycleFactoryImpl
