/* 
 * $Id: TestViewHandlerImpl.java,v 1.3 2002/07/12 23:58:46 rkitain Exp $ 
 */ 


/* 
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved. 
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. 
 */ 


// TestViewHandlerImpl.java 


package com.sun.faces.lifecycle; 


import org.apache.cactus.WebRequest; 
import org.apache.cactus.JspTestCase; 


import org.mozilla.util.Assert; 
import org.mozilla.util.ParameterCheck; 


import javax.faces.FacesException; 
import javax.faces.FactoryFinder; 
import javax.faces.context.FacesContext; 
import javax.faces.context.FacesContextFactory; 
import javax.faces.lifecycle.Phase; 
import javax.faces.lifecycle.Lifecycle; 
import javax.faces.component.UIComponentBase; 
import javax.faces.component.UITextEntry; 
import javax.faces.validator.Validator; 
import javax.faces.component.AttributeDescriptor; 


import com.sun.faces.JspFacesTestCase; 
import com.sun.faces.FileOutputResponseWrapper; 
import com.sun.faces.RIConstants; 
import com.sun.faces.tree.SimpleTreeImpl;
import com.sun.faces.util.Util; 
import com.sun.faces.CompareFiles; 


import com.sun.faces.TestBean; 


import java.io.IOException; 


import java.util.Iterator; 
import java.util.ArrayList; 


import javax.servlet.jsp.PageContext; 


/** 
 * 
 * <B>TestViewHandlerImpl</B> is a class ... 
 * 
 * <B>Lifetime And Scope</B> <P> 
 * 
 * @version $Id: TestViewHandlerImpl.java,v 1.3 2002/07/12 23:58:46 rkitain Exp $ 
 * 
 * @see Blah 
 * @see Bloo 
 * 
 */ 


public class TestViewHandlerImpl extends JspFacesTestCase 
{ 
// 
// Protected Constants 
// 


public static final String TEST_URI = "/TestRenderResponsePhase.jsp";


public String getExpectedOutputFilename() { 
    return "RenderResponse_correct"; 
} 


public static final String ignore[] = {
    "<FORM METHOD=\"post\" ACTION=\"%2Ftest%2Ffaces%2Fform%2FbasicForm%2FTestRenderResponsePhase.jsp;jsessionid=80E0636212B5916924E002B6076365E7\">"
};
     
public String [] getLinesToIgnore() { 
    return ignore; 
} 


public boolean sendResponseToFile() 
{ 
    return true; 
} 


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


    public TestViewHandlerImpl() { 
        super("TestViewHandlerImpl"); 
    } 


    public TestViewHandlerImpl(String name) { 
        super(name); 
    } 


// 
// Class methods 
// 


// 
// General Methods 
// 



public void beginRender(WebRequest theRequest) 
{ 
    theRequest.setURL("localhost:8080", null, null, TEST_URI, null); 
   // theRequest.addParameter("tree", TEST_URI_XUL); 
} 


public void testRender() 
{ 
    boolean result = false; 
    int rc = Phase.GOTO_NEXT; 
    UIComponentBase root = null; 
    String value = null; 
    SimpleTreeImpl tree = null;
    LifecycleImpl lifecycle = new LifecycleImpl();

    root = new UIComponentBase() {
        public String getComponentType() { return "Root"; }
    };
    root.setComponentId("root");

    tree = new SimpleTreeImpl(config.getServletContext(), root, TEST_URI);
    getFacesContext().setRequestTree(tree);
    try { 
        ViewHandlerImpl viewHandler = new ViewHandlerImpl(); 
        viewHandler.renderView(getFacesContext()); 
    } catch (IOException e) { 
        System.out.println("ViewHandler IOException:"+e); 
        rc = -1; 
    } 
    assertTrue(Phase.GOTO_NEXT == rc); 


    assertTrue(verifyExpectedOutput()); 
} 


} // end of class TestViewHandlerImpl 
