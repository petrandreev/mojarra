/*
 * $Id: FacesTestCaseService.java,v 1.1 2002/06/11 21:47:17 eburns Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// FacesTestCaseService.java

package com.sun.faces;

import javax.servlet.http.HttpServletResponse;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.servlet.jsp.PageContext;

import com.sun.faces.util.Util;
import com.sun.faces.RIConstants;

import org.mozilla.util.Assert;

import java.util.ArrayList;

import java.io.IOException;

/**
 *

 * Subclasses of ServletTestCase and JspTestCase use an instance of this
 * class to handle behavior specific to Faces TestCases.  You may
 * recognize this as using object compositition vs multiple inheritance.
 * <P>

 *
 * <B>Lifetime And Scope</B> <P> Same as the JspTestCase or
 * ServletTestCase instance that uses it.
 *
 * @version $Id: FacesTestCaseService.java,v 1.1 2002/06/11 21:47:17 eburns Exp $
 * 
 * @see	com.sun.faces.context.FacesContextFactoryImpl
 * @see	com.sun.faces.context.FacesContextImpl
 *
 */

public class FacesTestCaseService extends Object
{
//
// Protected Constants
//

/**

* Things used as names and values in the System.Properties table.

*/

public static final String ENTER_CALLED = "enterCalled";
public static final String EXIT_CALLED = "exitCalled";
public static final String EMPTY = "empty";



//
// Class Variables
//

//
// Instance Variables
//

// Attribute Instance Variables

// Relationship Instance Variables

protected FacesTestCase facesTestCase = null;

protected FacesContextFactory facesContextFactory = null;

protected FacesContext facesContext = null;

//
// Constructors and Initializers    
//

public FacesTestCaseService(FacesTestCase newFacesTestCase) 
{
    facesTestCase = newFacesTestCase;
}


//
// Class methods
//

//
// General Methods
//

public FacesContext getFacesContext() { return facesContext; }

public FacesContextFactory getFacesContextFactory() 
    { return facesContextFactory; }

public void setUp()
{
    HttpServletResponse response = null;
    Util.verifyFactoriesAndInitDefaultRenderKit(facesTestCase.getConfig().getServletContext());
    
    facesContextFactory = (FacesContextFactory) 
	FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
    Assert.assert_it(null != facesContextFactory);

    // See if the testcase wants to have its output sent to a file.

    if (facesTestCase.sendResponseToFile()) {
	response = new FileOutputResponseWrapper(facesTestCase.getResponse());
    }
    else {
	response = facesTestCase.getResponse();
    }
    
    facesContext = 
	facesContextFactory.createFacesContext(facesTestCase.getConfig().
					       getServletContext(),
					       facesTestCase.getRequest(), 
					       response);
    Assert.assert_it(null != facesContext);
    TestBean testBean = new TestBean();
    (facesContext.getHttpSession()).setAttribute("TestBean", testBean);
    System.setProperty(RIConstants.DISABLE_RENDERERS, 
		       RIConstants.DISABLE_RENDERERS);

    PageContext pageContext = null;
    
    if (null != (pageContext = facesTestCase.getPageContext())) {
	pageContext.setAttribute(FacesContext.FACES_CONTEXT_ATTR, facesContext,
				 PageContext.REQUEST_SCOPE);
    }
}

public void tearDown()
{
    Util.releaseFactoriesAndDefaultRenderKit(facesTestCase.getConfig().getServletContext());
    (facesContext.getHttpSession()).removeAttribute("TestBean");

    PageContext pageContext = null;
    
    if (null != (pageContext = facesTestCase.getPageContext())) {
	pageContext.removeAttribute(FacesContext.FACES_CONTEXT_ATTR);
    }
}

public boolean verifyExpectedOutput()
{
    boolean result = false;
    CompareFiles cf = new CompareFiles();
    String errorMessage = null;
    
    // If this testcase doesn't participate in file comparison
    if (!facesTestCase.sendResponseToFile() || 
	(null == facesTestCase.getExpectedOutputFilename())) {
	return true;
    }
    
    errorMessage = "File Comparison failed: diff -u " +
	FileOutputResponseWrapper.FACES_RESPONSE_FILENAME + " " + 
	facesTestCase.getExpectedOutputFilename();
    
    ArrayList ignoreList = null;
    String [] ignore = null;
    
    if (null != (ignore = facesTestCase.getLinesToIgnore())) {
	ignoreList = new ArrayList();
	for (int i = 0; i < ignore.length; i++) {
	    ignoreList.add(ignore[i]);
	}
    }
    
    try {
	result = 
	   cf.filesIdentical(FileOutputResponseWrapper.FACES_RESPONSE_FILENAME,
			     FileOutputResponseWrapper.FACES_RESPONSE_ROOT +
			     facesTestCase.getExpectedOutputFilename(), 
			     ignoreList);
    }
    catch (IOException e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
    }

    if (!result) {
	System.out.println(errorMessage);
    }
    return result;
}

} // end of class FacesTestCaseService
