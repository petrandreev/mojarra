/*
 * $Id: TestRenderKit.java,v 1.26 2007/08/08 16:38:46 youngm Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

// TestRenderKit.java

package com.sun.faces.renderkit;

import com.sun.faces.cactus.FileOutputResponseWriter;
import com.sun.faces.cactus.ServletFacesTestCase;
import com.sun.faces.renderkit.html_basic.FormRenderer;
import com.sun.faces.renderkit.html_basic.TextRenderer;
import com.sun.faces.renderkit.html_basic.HtmlBasicInputRenderer;
import com.sun.faces.renderkit.html_basic.HiddenRenderer;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.ConfigManager;
import com.sun.faces.config.DbfFactory;
import com.sun.faces.config.processor.ConfigProcessor;
import com.sun.faces.config.processor.FactoryConfigProcessor;
import com.sun.faces.config.processor.LifecycleConfigProcessor;
import com.sun.faces.config.processor.ApplicationConfigProcessor;
import com.sun.faces.config.processor.ComponentConfigProcessor;
import com.sun.faces.config.processor.ConverterConfigProcessor;
import com.sun.faces.config.processor.ValidatorConfigProcessor;
import com.sun.faces.config.processor.ManagedBeanConfigProcessor;
import com.sun.faces.config.processor.RenderKitConfigProcessor;
import com.sun.faces.config.processor.NavigationConfigProcessor;
import com.sun.faces.config.processor.BehaviorConfigProcessor;
import com.sun.faces.util.Util;
import org.apache.cactus.ServletTestCase;

import javax.faces.FactoryFinder;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.ResponseStateManager;
import javax.faces.render.RenderKitWrapper;

import javax.servlet.ServletResponse;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Iterator;

import org.apache.cactus.WebRequest;
import org.w3c.dom.Document;

/**
 * <B>TestRenderKit</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: TestRenderKit.java,v 1.26 2007/08/08 16:38:46 youngm Exp $
 */

public class TestRenderKit extends ServletFacesTestCase {

//
// Protected Constants
//

    public static final String OUTPUT_FILENAME =
        FileOutputResponseWriter.FACES_RESPONSE_ROOT + "TestRenderKit_out";

    public static final String CORRECT_OUTPUT_FILENAME =
        FileOutputResponseWriter.FACES_RESPONSE_ROOT + "TestRenderKit_correct";



//
// Class Variables
//

//
// Instance Variables
//
    private RenderKit renderKit = null;

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

    public TestRenderKit() {
        super("TestRenderKit");
    }


    public TestRenderKit(String name) {
        super(name);
    }
//
// Class methods
//

//
// General Methods
//

    public void testGetRenderer() {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                  RenderKitFactory.HTML_BASIC_RENDER_KIT);

        // 1. Verify "getRenderer()" returns a Renderer instance
        //  
        Renderer renderer = renderKit.getRenderer("javax.faces.Form",
                                                  "javax.faces.Form");
        assertTrue(renderer instanceof FormRenderer);

        // 2. Verify "getRenderer()" returns null
        //
        renderer = renderKit.getRenderer("Foo", "Bar");
        assertTrue(renderer == null);

        // 3. Verify NPE
        //
        boolean exceptionThrown = false;
        try {
            renderer = renderKit.getRenderer(null, null);
        } catch (NullPointerException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }


    public void testAddRenderer() {
        boolean bool = false;
        FormRenderer formRenderer = new FormRenderer();
        TextRenderer textRenderer = new TextRenderer();

        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                  RenderKitFactory.HTML_BASIC_RENDER_KIT);
        // Test to see if addRenderer replaces the renderer if given
        // the same rendererType.
        //
        renderKit.addRenderer("Form", "Form", formRenderer);
        assertTrue(
            renderKit.getRenderer("Form", "Form") instanceof FormRenderer);
        renderKit.addRenderer("Form", "Form", textRenderer);
        assertTrue(
            renderKit.getRenderer("Form", "Form") instanceof TextRenderer);

        bool = false;
        try {
            renderKit.addRenderer("BlahFamily", null, formRenderer);
        } catch (NullPointerException e) {
            bool = true;
        }
        assertTrue(bool);

        bool = false;
        try {
            renderKit.addRenderer(null, "BlahRenderer", formRenderer);
        } catch (NullPointerException e) {
            bool = true;
        }
        assertTrue(bool);

        bool = false;
        try {
            renderKit.addRenderer("BlahFamily", "BlahRenderer", null);
        } catch (NullPointerException e) {
            bool = true;
        }
        assertTrue(bool);

    }


    public void testCreateResponseStream() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                  RenderKitFactory.HTML_BASIC_RENDER_KIT);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ResponseStream stream = renderKit.createResponseStream(out);
        stream.write('a');
        stream.write((byte) 'b');
        stream.write(new byte[]{(byte) 'c', (byte) 'd', (byte) 'e'}, 1, 2);
        stream.flush();
        String result = out.toString();
        assertTrue(result.equals("abde"));
        try {
            stream.close();
        } catch (IOException ioe) {
            ; // ignore
        }
    }


    public void testCreateResponseWriter() throws Exception {

        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
	// use an invalid encoding
        try {
            renderKit.createResponseWriter(new StringWriter(), null, "foo");

            fail("IllegalArgumentException Should Have Been Thrown!");

        } catch (IllegalArgumentException iae) {
        }
	
	ResponseWriter writer = null;

	// see that the proper content type is picked up based on the
	// contentTypeList param
	writer = renderKit.createResponseWriter(new StringWriter(), 
						"application/xhtml+xml,text/html", 
						"ISO-8859-1");
	assertEquals(writer.getContentType(), "application/xhtml+xml");
	writer = renderKit.createResponseWriter(new StringWriter(), 
						"text/html,application/xhtml+xml",
						"ISO-8859-1");
	assertEquals(writer.getContentType(), "text/html");

	// see that IAE is thrown if the content type isn't known
	try {
	    writer = renderKit.createResponseWriter(new StringWriter(), 
						    "application/pdf",
						    "ISO-8859-1");
	    
            fail("IllegalArgumentException Should Have Been Thrown!");

        } catch (IllegalArgumentException iae) {
        }

    }

    public void beginCreateResponseWriterAllMedia(WebRequest theRequest) {
        theRequest.addHeader("Accept", "*/*");
    }

    public void testCreateResponseWriterAllMedia() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;

        // see that the proper content type is picked up based on the
        // accept header  
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "text/html");
        
    	//Ensure we correctly support */* in content type ranges
    	writer = renderKit.createResponseWriter(new StringWriter(), 
    			"application/xhtml+xml,*/*", 
    			"ISO-8859-1");
    	assertEquals(writer.getContentType(), "application/xhtml+xml");

    	writer = renderKit.createResponseWriter(new StringWriter(), 
    			"text/css,*/*",
    			"ISO-8859-1");
    	assertEquals(writer.getContentType(), "text/html");

    	writer = renderKit.createResponseWriter(new StringWriter(), 
    			"*/*",
    			"ISO-8859-1");
    	assertEquals(writer.getContentType(), "text/html");
    }

    public void beginCreateResponseWriter1(WebRequest theRequest) {
        theRequest.addHeader("Accept", "text/html; q=0.2, application/xhtml+xml; q=0.8, application/xml; q=0.5, */*");
    }

    public void testCreateResponseWriter1() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;

        // see that the proper content type is picked up based on the
        // accept header  
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "application/xhtml+xml");
    }

    public void beginCreateResponseWriter2(WebRequest theRequest) {
        theRequest.addHeader("Accept", "text/html; q=0.2, application/xhtml+xml; q=0.8, application/xml; q=0.9, */*");
    }

    public void testCreateResponseWriter2() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;
                                                                                                                   
        // see that the proper content type is picked up based on the
        // accept header
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "application/xhtml+xml");
    }

    public void beginCreateResponseWriter3(WebRequest theRequest) {
        theRequest.addHeader("Accept", "text/html, application/xhtml+xml; q=0.8, application/xml; q=0.9, */*");
    }
                                                                                                                   
    public void testCreateResponseWriter3() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;
                                                                                                                   
        // see that the proper content type is picked up based on the
        // accept header
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "text/html");
    }

    public void beginCreateResponseWriter4(WebRequest theRequest) {
        theRequest.addHeader("Accept", "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, */*");
        theRequest.addHeader("Accept", "text/html; level=1");
    }
                                                                                                                           
    public void testCreateResponseWriter4() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;
                                                                                                                           
        // see that the proper content type is picked up based on the
        // accept header
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "text/html");
    }

    // Response has unsupported content type..
    public void testCreateResponseWriter5() throws Exception {
        ((ServletResponse)getFacesContext().getExternalContext().getResponse()).setContentType("image/png");
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;
                                                                                                                         
        // see that the proper content type is picked up based on the
        // accept header
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "text/html");
    }

    // Added for issue 807
    public void beginCreateResponseWriter6(WebRequest theRequest) {
        theRequest.addHeader("Accept", "text/");
    }

    public void testCreateResponseWriter6() throws Exception {
        RenderKitFactory renderKitFactory = (RenderKitFactory)
              FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;

        // see that the proper content type is picked up based on the
        // accept header
        writer = renderKit
              .createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "text/html");
    }



    public void testGetComponentFamilies() {

        RenderKitImpl rk = new RenderKitImpl();
        Iterator empty = rk.getComponentFamilies();
        assertNotNull(empty);
        assertTrue(!empty.hasNext());

        rk.addRenderer("family", "rendererType", new HiddenRenderer());
        Iterator notEmpty = rk.getComponentFamilies();
        assertNotNull(notEmpty);
        assertTrue(notEmpty.hasNext());

    }


    public void testGetRendererTypes() {

        RenderKitImpl rk = new RenderKitImpl();
        rk.addRenderer("family", "rendererType", new HiddenRenderer());

        Iterator empty = rk.getRendererTypes("non-exist");
        assertNotNull(empty);
        assertTrue(!empty.hasNext());

        Iterator notEmpty = rk.getRendererTypes("family");
        assertNotNull(notEmpty);
        assertTrue(notEmpty.hasNext());

    }


    // Added for issue 1106
    public void beginCreateResponseWriter7(WebRequest theRequest) {
        theRequest.addHeader("Accept", "*/*");
    }

    public void testCreateResponseWriter7() throws Exception {
        WebConfiguration webConfig = WebConfiguration.getInstance();
        webConfig.overrideContextInitParameter(WebConfiguration.BooleanWebContextInitParameter.PreferXHTMLContentType, true);
        RenderKitFactory renderKitFactory = (RenderKitFactory)
            FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(getFacesContext(),
                                                            RenderKitFactory.HTML_BASIC_RENDER_KIT);
        ResponseWriter writer = null;

        // see that the proper content type is picked up based on the
        // accept header
        writer = renderKit.createResponseWriter(new StringWriter(), null, "ISO-8859-1");
        assertEquals(writer.getContentType(), "application/xhtml+xml");
        webConfig.overrideContextInitParameter(WebConfiguration.BooleanWebContextInitParameter.PreferXHTMLContentType, true);
    }


    public void testRenderKitDecoration() throws Exception {

        FacesContext ctx = getFacesContext();
        ServletContext servletContext = (ServletContext) ctx.getExternalContext().getContext();
        FactoryFinder.releaseFactories();
        servletContext.removeAttribute("com.sun.faces.ApplicationAssociate");
        ConfigManager config = ConfigManager.getInstance();

        DocumentBuilderFactory factory = DbfFactory.getFactory();
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        ClassLoader loader = Util.getCurrentLoader(this);
        Document defaultDoc = builder.parse(loader.getResourceAsStream("com/sun/faces/jsf-ri-runtime.xml"));
        Document renderKitDoc = builder.parse(servletContext.getResourceAsStream("/WEB-INF/renderkit1.xml"));

        ConfigProcessor[] configProcessors = {
             new FactoryConfigProcessor(),
             new ApplicationConfigProcessor(),
             new RenderKitConfigProcessor(),
        };
        for (int i = 0; i < configProcessors.length; i++) {
            ConfigProcessor p = configProcessors[i];
            if ((i + 1) < configProcessors.length) {
                p.setNext(configProcessors[i + 1]);
            }
        }

        configProcessors[0].process(new Document[] { defaultDoc, renderKitDoc });

        RenderKitFactory rkf = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit rk = rkf.getRenderKit(getFacesContext(), RenderKitFactory.HTML_BASIC_RENDER_KIT);
        assertEquals(rk.getClass().getName(), DecoratingRenderKit.class.getName());
        assertEquals(RenderKitImpl.class.getName(), ((RenderKitWrapper) rk).getWrapped().getClass().getName());
    }


    // ---------------------------------------------------------- Nested Classes


    public static class DecoratingRenderKit extends RenderKitWrapper {

        private RenderKit delegate;

        public DecoratingRenderKit(RenderKit delegate) {
            this.delegate = delegate;
        }

        public RenderKit getWrapped() {
            return delegate;
        }
    }


} // end of class TestRenderKit
