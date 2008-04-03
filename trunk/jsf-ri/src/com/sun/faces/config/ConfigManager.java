/*
 * $Id: ConfigManager.java,v 1.7 2007/05/09 05:04:14 rlubke Exp $
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

package com.sun.faces.config;

import com.sun.faces.config.configprovider.MetaInfResourceProvider;
import com.sun.faces.config.configprovider.RIConfigResourceProvider;
import com.sun.faces.config.configprovider.WebResourceProvider;
import com.sun.faces.config.processor.ApplicationConfigProcessor;
import com.sun.faces.config.processor.ComponentConfigProcessor;
import com.sun.faces.config.processor.ConfigProcessor;
import com.sun.faces.config.processor.ConverterConfigProcessor;
import com.sun.faces.config.processor.FactoryConfigProcessor;
import com.sun.faces.config.processor.LifecycleConfigProcessor;
import com.sun.faces.config.processor.ManagedBeanConfigProcessor;
import com.sun.faces.config.processor.NavigationConfigProcessor;
import com.sun.faces.config.processor.RenderKitConfigProcessor;
import com.sun.faces.config.processor.ValidatorConfigProcessor;
import com.sun.faces.spi.ConfigurationResourceProvider;
import com.sun.faces.util.Timer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 *  This class manages the initialization of each web application that uses
 *  JSF.
 * </p>
 */
public class ConfigManager {

    /**
     * <p>
     *  The list of resource providers.  By default, this contains a provider
     *  for the RI, and two providers to satisfy the requirements of the
     *  specification.
     * </p>
     */
    private static final List<ConfigurationResourceProvider> RESOURCE_PROVIDERS
         = new ArrayList(3);

    /**
     * <p>
     *  The <code>ConfigManager</code> will multithread the calls to the
     *  <code>ConfigurationResourceProvider</code>s as well as any calls
     *  to parse a resources into a DOM.  By default, we'll use only 5 threads
     *  per web application.
     * </p>
     */
    private static final int NUMBER_OF_TASK_THREADS = 5;

    /**
     * <p>
     *  There is only once instance of <code>ConfigManager</code>.
     * <p>
     */
    private static final ConfigManager CONFIG_MANAGER = new ConfigManager();

    /**
     * <p>
     *   Contains each <code>ServletContext</code> that we've initialized.
     *   The <code>ServletContext</code> will be removed when the application
     *   is destroyed.
     * </p>
     */
    @SuppressWarnings({"CollectionWithoutInitialCapacity"})
    private List<ServletContext> initializedContexts =
         new CopyOnWriteArrayList();

    /**
     * <p>
     *  The chain of {@link ConfigProcessor}, used to initialize JSF.
     * </p>
     */
    private static final ConfigProcessor CONFIG_PROCESSOR_CHAIN;
    private static final String XSL = "/com/sun/faces/jsf1_0-1_1to1_2.xsl";


    static {
        RESOURCE_PROVIDERS.add(new RIConfigResourceProvider());
        RESOURCE_PROVIDERS.add(new MetaInfResourceProvider());
        RESOURCE_PROVIDERS.add(new WebResourceProvider());
        ConfigProcessor[] configProcessors = {
             new FactoryConfigProcessor(),
             new LifecycleConfigProcessor(),
             new ApplicationConfigProcessor(),
             new ComponentConfigProcessor(),
             new ConverterConfigProcessor(),
             new ValidatorConfigProcessor(),
             new ManagedBeanConfigProcessor(),
             new RenderKitConfigProcessor(),
             new NavigationConfigProcessor()
        };
        for (int i = 0; i < configProcessors.length; i++) {
            ConfigProcessor p = configProcessors[i];
            if ((i + 1) < configProcessors.length) {
                p.setNext(configProcessors[i + 1]);
            }
        }
        CONFIG_PROCESSOR_CHAIN = configProcessors[0];
    }


    // ---------------------------------------------------------- Public Methods


    /**
     * @return a <code>ConfigManager</code> instance
     */
    public static ConfigManager getInstance() {

        return CONFIG_MANAGER;

    }


    /**
     * <p>
     *   This method bootstraps JSF based on the parsed configuration resources.
     * </p>
     *
     * @param sc the <code>ServletContext</code> for the application that
     *  requires initialization
     */
    public void initialize(ServletContext sc) {

        if (!hasBeenInitialized(sc)) {
            initializedContexts.add(sc);
            try {                
                CONFIG_PROCESSOR_CHAIN.process(getConfigDocuments(sc));
            } catch (Exception e) {
                 // no i18n here - it's too early
                throw new ConfigurationException(
                     "Unexpected error during configuration processing",
                     e);
            }
        }

    }


    /**
     * <p>
     *   This method will remove any information about the application.
     * </p>
     * @param sc the <code>ServletContext</code> for the application that
     *  needs to be removed
     */
    public void destory(ServletContext sc) {

        initializedContexts.remove(sc);
        
    }


    /**
     * @param sc the <code>ServletContext</code> for the application in question
     * @return <code>true</code> if this application has already been initialized,
     *  otherwise returns </code>fase</code>
     */
    public boolean hasBeenInitialized(ServletContext sc) {
        return (initializedContexts.contains(sc));
    }


    // --------------------------------------------------------- Private Methods


    /**
     * <p>
     *   Obtains an array of <code>Document</code>s to be processed
     *   by {@link ConfigManager#CONFIG_PROCESSOR_CHAIN}.
     * </p>
     *
     * @param sc the <code>ServletContext</code> for the application to be
     *  processed
     * @return an array of <code>Document</code>s
     */
    private static Document[] getConfigDocuments(ServletContext sc) {

        ExecutorService executor =
             Executors.newFixedThreadPool(NUMBER_OF_TASK_THREADS);
        
        List<FutureTask<List<URL>>> urlTasks =
             new ArrayList<FutureTask<List<URL>>>(RESOURCE_PROVIDERS.size());
        for (ConfigurationResourceProvider p : RESOURCE_PROVIDERS) {
            FutureTask<List<URL>> t =
                 new FutureTask<List<URL>>(new URLTask(p, sc));
            urlTasks.add(t);
            executor.execute(t);
        }

        List<FutureTask<Document>> docTasks =
             new ArrayList<FutureTask<Document>>(RESOURCE_PROVIDERS.size() << 1);
        boolean validating = WebConfiguration.getInstance(sc)
             .getBooleanContextInitParameter(
                  WebConfiguration.BooleanWebContextInitParameter.ValidateFacesConfigFiles);
        DocumentBuilderFactory factory = DbfFactory.getFactory(validating);
        for (FutureTask<List<URL>> t : urlTasks) {
            try {
                List<URL> l = t.get();                
                for (URL u : l) {
                    FutureTask<Document> d =
                         new FutureTask<Document>(new ParseTask(factory, u));
                    docTasks.add(d);
                    executor.execute(d);
                }
            } catch (InterruptedException e) {
                ;
            } catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }

        List<Document> docs = new ArrayList(docTasks.size());
        for (FutureTask<Document> t : docTasks) {
            try {
                docs.add(t.get());
            } catch (ExecutionException e) {
                throw new ConfigurationException(e);
            } catch (InterruptedException e) {
                ;
            }
        }

        executor.shutdown();
        return docs.toArray(new Document[docs.size()]);

    }




    // ----------------------------------------------------------- Inner Classes


    /**
     * <p>
     *  This <code>Callable</code> will be used by {@link ConfigManager#getConfigDocuments(javax.servlet.ServletContext)}.
     *  It represents a single configuration resource to be parsed into a DOM.
     * </p>
     */
    private static class ParseTask implements Callable<Document> {

        private URL documentURL;
        private DocumentBuilder builder;

        // -------------------------------------------------------- Constructors


        /**
         * <p>
         *   Constructs a new ParseTask instance
         * </p>
         * @param factory a DocumentBuilderFactory configured with the desired
         *  parse settings
         * @param documentURL a URL to the configuration resource to be parsed
         */
        public ParseTask(DocumentBuilderFactory factory,
                         URL documentURL)
        throws Exception {

            this.documentURL = documentURL;
            builder = factory.newDocumentBuilder();
            builder.setEntityResolver(DbfFactory.FACES_ENTITY_RESOLVER);
            builder.setErrorHandler(DbfFactory.FACES_ERROR_HANDLER);

        }


        // ----------------------------------------------- Methods from Callable


        /**
         * @return the result of the parse operation (a DOM)
         * @throws Exception if an error occurs during the parsing process
         */
        public Document call() throws Exception {

            InputStream stream = getInputStream(documentURL);
            try {
                Timer timer = Timer.getInstance();
                if (timer != null) {
                    timer.startTiming();
                }

                Document d = builder.parse(getParseSource());

                if (timer != null) {
                    timer.stopTiming();
                    timer.logResult("Parse " + documentURL.toExternalForm());
                }
              
                return d;
            } catch (Exception e) {
                throw new ConfigurationException(MessageFormat.format(
                     "Unable to parse document ''{0}'': {1}",
                     documentURL.toExternalForm(),
                     e.getMessage()));
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ioe) {
                        ;
                    }
                }
            }
        }


        // ----------------------------------------------------- Private Methods


        /**
         * Obtain a <code>Transformer</code> using the style sheet
         * referenced by the <code>XSL</code> constant.
         *
         * @return a new Tranformer instance
         * @throws Exception if a Tranformer instance could not be created
         */
        private static Transformer getTransformer() throws Exception {

            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setURIResolver(new URIResolver() {

                public Source resolve(String href, String base)
                throws TransformerException {
                     System.out.println("URI Resolver href: " + href);
                        System.out.println("URI Resolver base: " + base);
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            return factory
                 .newTransformer(new StreamSource(getInputStream(ConfigManager
                      .class.getResource(XSL))));

        }


        /**
         * @return an <code>InputStream</code> to the resource referred to by
         *         <code>url</code>
         * @throws IOException if an error occurs
         */
        private static InputStream getInputStream(URL url) throws IOException {

            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            return new BufferedInputStream(conn.getInputStream());

    }

        private InputSource getParseSource() throws Exception {

            if (builder.isValidating()) {
                // if we're validating, we need to apply xslt transformations
                // to convert all documents to 1.2
                ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
                StreamResult sResult = new StreamResult(baos);
                DocumentBuilder tBuilder = getNonValidatingBuilder();
                DOMSource domSource
                     = new DOMSource(tBuilder.parse(getInputStream(documentURL),
                                                    documentURL.toExternalForm()));
                Transformer transformer = getTransformer();
                transformer.transform(domSource, sResult);
                InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
                is.setSystemId(documentURL.toExternalForm());
                return is;
            } else {
                InputSource is = new InputSource(getInputStream(documentURL));
                is.setSystemId(documentURL.toExternalForm());
                return is;               
            }

        }


        private DocumentBuilder getNonValidatingBuilder() throws Exception {

            DocumentBuilderFactory tFactory = DbfFactory.getFactory(false);
            DocumentBuilder tBuilder = tFactory.newDocumentBuilder();
            tBuilder.setEntityResolver(DbfFactory.FACES_ENTITY_RESOLVER);
            tBuilder.setErrorHandler(DbfFactory.FACES_ERROR_HANDLER);
            return tBuilder;

        }

    } // END ParseTask


    /**
     * <p>
     *  This <code>Callable</code> will be used by {@link ConfigManager#getConfigDocuments(javax.servlet.ServletContext)}.
     *  It represents one or more URLs to configuration resources that require
     *  processing.
     * </p>
     */
    private static class URLTask implements Callable<List<URL>> {

        private ConfigurationResourceProvider provider;
        private ServletContext sc;


        // -------------------------------------------------------- Constructors


        /**
         * Constructs a new <code>URLTask</code> instance.
         * @param provider the <code>ConfigurationResourceProvider</code> from
         *  which zero or more <code>URL</code>s will be returned
         * @param sc the <code>ServletContext</code> of the current application
         */
        public URLTask(ConfigurationResourceProvider provider,
                       ServletContext sc) {
            this.provider = provider;
            this.sc = sc;
        }


        // ----------------------------------------------- Methods from Callable


        /**
         * @return zero or more <code>URL</code> instances
         * @throws Exception if an Exception is thrown by the underlying
         *  <code>ConfigurationResourceProvider</code> 
         */
        public List<URL> call() throws Exception {
            return provider.getResources(sc);
        }

    } // END URLTask
}
