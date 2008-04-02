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

package characterCombat;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.component.UIViewRoot;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import java.util.Iterator;

/**
 * <p>Backing bean for wizard style navigation.  This class provides
 * methods that you can point to from your wizard buttons that will
 * return true or false depending on the current page in the
 * application.</p>
 */
public class WizardButtons {

    /** 
     * <p>Check to see whether the current page should have a back button</p>
     *
     * @return true if the current page has a "back" page.
     */
    public boolean isHasBack() {
	FacesContext 
	    realContext = FacesContext.getCurrentInstance(),
	    copyContext = createShadowFacesContext(realContext);
	NavigationHandler nav = 
	    copyContext.getApplication().getNavigationHandler();
	nav.handleNavigation(copyContext, null, "back");
	return compareUIViewRoots(realContext.getViewRoot(),
				  copyContext.getViewRoot());
    }

    /** 
     * <p>Check to see whether the current page should have a next button</p>
     *
     * @return true if the current page has a "next" page.
     */
    public boolean isHasNext() {
	FacesContext 
	    realContext = FacesContext.getCurrentInstance(),
	    copyContext = createShadowFacesContext(realContext);
	NavigationHandler nav = 
	    copyContext.getApplication().getNavigationHandler();
	nav.handleNavigation(copyContext, null, "next");
	return compareUIViewRoots(realContext.getViewRoot(),
				  copyContext.getViewRoot());
    }

    /**
     * <p>Check to see whether the current page should have a finish button</p>
     *
     * @return true if the current page should have a "finish" button
     * instead of a "next" button
     */
    public boolean isFinishPage() {
	FacesContext 
	    realContext = FacesContext.getCurrentInstance(),
	    copyContext = createShadowFacesContext(realContext),
	    nextCopyContext = null;
	NavigationHandler nav = 
	    copyContext.getApplication().getNavigationHandler();
	// get the next outcome
	nav.handleNavigation(copyContext, null, "next");
	nextCopyContext = createShadowFacesContext(copyContext);
	nav.handleNavigation(nextCopyContext, null, "next");
	return compareUIViewRoots(copyContext.getViewRoot(),
				  nextCopyContext.getViewRoot());
    }

    /**
     * <p>Get the label for the "next" button.</p>
     *
     * @return String next button label
     */
    public String getNextLabel() {
	String result = "Next >";
	if (isFinishPage()) {
	    result = "Finish";
	}
	return result;
    }

    /**
     * <p>Take two View roots and compare them.</p>
     *
     * @param UIViewRoot the first ViewRoot
     * @param UIViewRoot the second ViewRoot
     * @return boolean the result of the comparison.
     */
    public boolean compareUIViewRoots(UIViewRoot one, UIViewRoot two) {
	if (null == one && null == two) {
	    return true;
	}
	if (null != one && null != two) {
	    if (null == one.getViewId() && null == two.getViewId()) {
		return true;
	    }
	    if (null != one.getViewId() && null != two.getViewId()) {
		return one.getViewId().equals(two.getViewId());
	    }
	    else {
		return false;
	    }
	}
	return false;
    }

    /**
     * <p>createShadowFacesContext creates a shallow copy of the 
     * argument FacesContext, but with a deep copy of the viewRoot 
     * property.  This allows us to call the NavigationHandler.handleNavigaton 
     * method without modifying the real FacesContext.</p>
     *
     * @param FacesContext the FacesContext to be copied
     * @return FacesContext shallow copy of FacesContext
     */
    public FacesContext createShadowFacesContext(FacesContext context) {
	final FacesContext oldContext = context;

	FacesContext result = new FacesContext() {
		private Application application = oldContext.getApplication();
		public Application getApplication() { return application; }

		public Iterator getClientIdsWithMessages() {
		    return oldContext.getClientIdsWithMessages();
		}

		public ExternalContext getExternalContext() {
		    return oldContext.getExternalContext();
		}
		
		public Severity getMaximumSeverity() {
		    return oldContext.getMaximumSeverity();
		}
		
		public Iterator getMessages() {
		    return oldContext.getMessages();
		}
		
		public Iterator getMessages(String clientId) {
		    return oldContext.getMessages(clientId);
		}
		
		public RenderKit getRenderKit() {
		    return oldContext.getRenderKit();
		}
		
		
		public boolean getRenderResponse() {
		    return oldContext.getRenderResponse();
		}
		
		
		public boolean getResponseComplete() {
		    return oldContext.getResponseComplete();
		}
		
		
		public ResponseStream getResponseStream() {
		    return oldContext.getResponseStream();
		}
		
		
		public void setResponseStream(ResponseStream responseStream) {
		    oldContext.setResponseStream(responseStream);
		}
		
		public ResponseWriter getResponseWriter() {
		    return oldContext.getResponseWriter();
		}
		
		
		public void setResponseWriter(ResponseWriter responseWriter) {
		    oldContext.setResponseWriter(responseWriter);
		}
		
		private UIViewRoot root = oldContext.getViewRoot();
		
		public UIViewRoot getViewRoot() { 
		    return root; 
		}
				
		public void setViewRoot(UIViewRoot root) {
		    this.root = root;
		}
		
		public void addMessage(String clientId, FacesMessage message) {
		    oldContext.addMessage(clientId, message);
		}
		
		public void release() {}
		
		public void renderResponse() {} 
		
		
		public void responseComplete() {}
	    };
	return result;
    }
}
