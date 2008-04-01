/*
 * $Id: EventQueueFactory.java,v 1.3 2002/04/11 22:51:20 eburns Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// EventQueueFactory.java

package javax.faces;


/**
 *
 *
 *  <B>EventQueueFactory</B> Creates EventQueue instances using
 *  the pluggable implementation scheme defined in the spec. <P>
 *
 * <B>Lifetime And Scope</B> <P>
 *
 *
 * @version $Id: EventQueueFactory.java,v 1.3 2002/04/11 22:51:20 eburns Exp $
 * 
 * @see	javax.faces.EventQueue
 *
 */

public abstract class EventQueueFactory extends Object
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

protected EventQueueFactory()
{
    super();
}

//
// Class methods
//

public static EventQueueFactory newInstance() throws FactoryConfigurationError  {
    return (EventQueueFactory) FactoryFinder.find(
	   /* The default property name according to the JSFaces spec */
	   "javax.faces.EventQueueFactory",
	   /* The fallback implementation class name */
	   "com.sun.faces.EventQueueFactoryImpl");
}


//
// Abstract Methods 
//

 /**
  * Internal constructor which initializes a <code>EventQueue</code>.
  * @throws FacesException if any of these objects could not be
  *         created.
  */

public abstract EventQueue newEventQueue() throws FacesException;

} // end of class EventQueueFactory
