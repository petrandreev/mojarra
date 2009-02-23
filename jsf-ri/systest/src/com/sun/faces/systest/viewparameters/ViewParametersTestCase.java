/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.faces.systest.viewparameters;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.sun.faces.htmlunit.AbstractTestCase;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test cases for Facelets functionality
 */
public class ViewParametersTestCase extends AbstractTestCase {


    // --------------------------------------------------------------- Test Init


    public ViewParametersTestCase() {
        this("FaceletsTestCase");
    }


    public ViewParametersTestCase(String name) {
        super(name);
    }


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(ViewParametersTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------------------ Test Methods


    /*
     * Added for issue 917.
     */
    public void testViewParameters() throws Exception {

        doTestExtensionMapped(0);
        
    }
    
    private void doTestExtensionMapped(int i) throws Exception {

        int storyNum = i + 1;
        HtmlPage page = null;
        
        page = fetchHomePageAndClickStoryLink(i);
        
        page = doRefreshButton(page, storyNum);
        
        page = doRefreshClearParamButton(page, storyNum);

        page = fetchHomePageAndClickStoryLink(i);
        
        page = doRefreshWithRedirectParamsButton(page, storyNum);
        
        page = fetchHomePageAndClickStoryLink(i);
        
        page = doRefreshWithoutRedirectParamsButton(page, storyNum);
        
        page = fetchHomePageAndClickStoryLink(i);

        page = doHomeButton(page, storyNum);
        
        page = fetchHomePageAndClickStoryLink(i);

        page = doHomeKeepSelectionButton(page, i);
        
        page = fetchHomePageAndClickStoryLink(i);

        page = doHomeKeepSelectionNavCaseButton(page, i);
        
        page = fetchHomePageAndClickStoryLink(i);

        page = doStory2Button(page, i);
        
        
    }
    
    private HtmlPage fetchHomePageAndClickStoryLink(int i) throws Exception {
        HtmlPage page = getPage("/viewParameters/page01.faces") ;
        String pageText = page.asText();

        assertOnHomePage(pageText);
        
        List<HtmlAnchor> anchors = new ArrayList<HtmlAnchor>();
        this.getAllElementsOfGivenClass(page, anchors, HtmlAnchor.class);
        HtmlAnchor toClick = anchors.get(i);
        page = (HtmlPage) toClick.click();
        
        int storyNum = i+1;
        
        // Assert some things about the content of the page
        pageText = page.asText();
        assertTrue(-1 != pageText.indexOf(getTitleContains(storyNum)));
        assertTrue(-1 != pageText.indexOf(getContentContains(storyNum)));
        
        return page;
    }
    
    private String getTitleContains(int storyNum) {
        String titleContains = "Story " + storyNum + " Headline:";
        return titleContains;
    }
    
    private String getContentContains(int storyNum) {
        String contentContains = "Story " + storyNum + " Content:";
        return contentContains;
    }
    
    private HtmlPage doRefreshButton(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        
        // Click the "refresh" button, ensure the page refreshes properly
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("refresh");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        assertTrue(-1 != pageText.indexOf(getTitleContains(storyNum)));
        assertTrue(-1 != pageText.indexOf(getContentContains(storyNum)));

        return page;
    }
    
    private HtmlPage doRefreshClearParamButton(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        // Click the "refreshClearParam" button, ensure you get back
        // to the home page
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("refreshClearParam");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        
        // no story content on home page
        assertTrue(-1 == pageText.indexOf(getContentContains(storyNum)));
        assertTrue(-1 != pageText.indexOf("You did not specify a headline. (The id parameter is missing)"));
        assertOnHomePage(pageText);
        return page;
    }
    
    private HtmlPage doRefreshWithRedirectParamsButton(HtmlPage page, int storyNum) throws Exception {
        // click the "refreshWithRedirectParams" button and make sure we're still
        // on the same page.
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("refreshWithRedirectParams");
        page = (HtmlPage) button.click();
        String pageText = page.asText();
        assertTrue(-1 != pageText.indexOf(getTitleContains(storyNum)));
        assertTrue(-1 != pageText.indexOf(getContentContains(storyNum)));
        
        return page;
    }
    
    private HtmlPage doRefreshWithoutRedirectParamsButton(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        // Click the "refreshWithRedirect" button, ensure you get back
        // to the home page
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("refreshWithRedirect");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        
        // no story content on home page
        assertTrue(-1 == pageText.indexOf(getContentContains(storyNum)));
        assertTrue(-1 != pageText.indexOf("The headline you requested does not exist."));
        assertOnHomePage(pageText);
        return page;
    }

    private HtmlPage doHomeButton(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        // Click the "home" button, ensure you get back
        // to the home page
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("home");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        
        // no story content on the page, and no messages either
        assertTrue(-1 == pageText.indexOf(getContentContains(storyNum)));
        assertTrue(-1 == pageText.indexOf("The headline you requested does not exist."));
        assertTrue(-1 == pageText.indexOf("You did not specify a headline. (The id parameter is missing)"));
        assertOnHomePage(pageText);

        return page;
    }
    
    private HtmlPage doHomeKeepSelectionButton(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        // Click the "homeKeepSelection" button, ensure you get back
        // to the home page with the proper story number
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("homeRememberSelection");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        
        assertOnHomePage(pageText);
        assertTrue(-1 == pageText.indexOf("You just looked at story #" + storyNum + "."));
        
        
        return page;
    }
    
    private HtmlPage doHomeKeepSelectionNavCaseButton(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        // Click the "homeKeepSelectionNavCase" button, ensure you get back
        // to the home page with the proper story number
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("homeRememberSelectionNavCase");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        
        assertOnHomePage(pageText);
        assertTrue(-1 == pageText.indexOf("You just looked at story #" + storyNum + "."));
        
        
        return page;
    }
    
    private HtmlPage doStory2Button(HtmlPage page, int storyNum) throws Exception {
        String pageText = null;
        // Click the "story2" button, ensure you get
        // to the story 2 page
        HtmlSubmitInput button = (HtmlSubmitInput) page.getHtmlElementById("story2RememberSelectionNavCase");
        page = (HtmlPage) button.click();
        pageText = page.asText();
        
        URL requestUrl = page.getWebResponse().getRequestUrl();
        // Assert that the queryString is in the reqeust URL.
        assertTrue(-1 != requestUrl.getQuery().indexOf("bar=foo"));
        
        assertTrue(-1 != pageText.indexOf("Story 2"));
        assertTrue(-1 != pageText.indexOf("bar is:foo"));
        
        return page;
    }
    
    private void assertOnHomePage(String pageText) throws Exception {
        assertTrue(-1 != pageText.indexOf("The big news stories of the day"));
    }

}
