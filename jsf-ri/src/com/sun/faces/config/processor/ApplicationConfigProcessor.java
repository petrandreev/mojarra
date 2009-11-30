/*
 * $Id: ApplicationConfigProcessor.java,v 1.7 2007/11/02 00:30:11 rlubke Exp $
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

package com.sun.faces.config.processor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ResourceHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.event.SystemEventListener;
import javax.faces.event.SystemEvent;
import javax.faces.event.NamedEvent;
import javax.servlet.ServletContext;

import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ApplicationResourceBundle;
import com.sun.faces.el.ChainAwareVariableResolver;
import com.sun.faces.el.DummyPropertyResolverImpl;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.Util;
import com.sun.faces.config.ConfigurationException;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.DocumentInfo;

import java.util.LinkedHashSet;
import java.util.Map;
import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter.DisableFaceletJSFViewHandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

/**
 * <p>
 *  This <code>ConfigProcessor</code> handles all elements defined under
 *  <code>/faces-config/application</code>.
 * </p>
 */
public class ApplicationConfigProcessor extends AbstractConfigProcessor {

    private static final Logger LOGGER = FacesLogger.CONFIG.getLogger();

    /**
     * <code>/faces-config/application</code>
     */
    private static final String APPLICATION =
         "application";

    /**
     * <code>/faces-config/application/action-listener</code>
     */
    private static final String ACTION_LISTENER
         = "action-listener";

    /**
     * <code>/faces-config/application/default-render-kit-id
     */
    private static final String DEFAULT_RENDERKIT_ID
         = "default-render-kit-id";

    /**
     * <code>/faces-config/application/default-validators</code>
     */
    private static final String DEFAULT_VALIDATORS
        = "default-validators";

    /**
     * <code>/faces-config/application/default-validators/validator-id</code>
     */
    private static final String VALIDATOR_ID
        = "validator-id";

    /**
     * <code>/faces-config/application/message-bundle
     */
    private static final String MESSAGE_BUNDLE
         = "message-bundle";

    /**
     * <code>/faces-config/application/navigation-handler</code>
     */
    private static final String NAVIGATION_HANDLER
         = "navigation-handler";

    /**
     * <code>/faces-config/application/view-handler</code>
     */
    private static final String VIEW_HANDLER
         = "view-handler";

    /**
     * <code>/faces-config/application/state-manager</code>
     */
    private static final String STATE_MANAGER
         = "state-manager";

    /**
     * <code>/faces-config/application/resource-handler</code>
     */
    private static final String RESOURCE_HANDLER
         = "resource-handler";

    /**
     * <code>/faces-config/application/el-resolver</code>
     */
    private static final String EL_RESOLVER
         = "el-resolver";

    /**
     * <code>/faces-config/application/property-resolver</code>
     */
    private static final String PROPERTY_RESOLVER
         = "property-resolver";

    /**
     * <code>/faces-config/application/variable-resolver</code>
     */
    private static final String VARIABLE_RESOLVER
         = "variable-resolver";

    /**
     * <code>/faces-config/application/locale-config/default-locale</code>
     */
    private static final String DEFAULT_LOCALE
         = "default-locale";

    /**
     * <code>/faces-config/application/locale-config/supported-locale</code>
     */
    private static final String SUPPORTED_LOCALE
         = "supported-locale";

    /**
     * <code>/faces-config/application/resource-bundle</code>
     */
    private static final String RESOURCE_BUNDLE
         = "resource-bundle";

    /**
     * <code>/faces-config/application/resource-bundle/base-name</code>
     */
    private static final String BASE_NAME
         = "base-name";

    /**
     * <code>/faces-config/application/resource-bundle/var</code>
     */
    private static final String VAR
         = "var";

    /**
     * <code>/faces-config/application/resource-bundle/description</code>
     */
    private static final String RES_DESCRIPTIONS
         = "description";

    /**
     * <code>/faces-config/application/resource-bundle/display-name</code>
     */
    private static final String RES_DISPLAY_NAMES
         = "display-name";

    /**
     * <code>/faces-config/application/system-event-listener</code>
     */
    private static final String SYSTEM_EVENT_LISTENER
         = "system-event-listener";

    /**
     * <code>/faces-config/application/system-event-listener/system-event-listener-class</code>
     */
    private static final String SYSTEM_EVENT_LISTENER_CLASS
         = "system-event-listener-class";

    /**
     * <code>/faces-config/application/system-event-listener/system-event-class</code>
     */
    private static final String SYSTEM_EVENT_CLASS
         = "system-event-class";

    /**
     * <code>/faces-config/application/system-event-listener/source-class</code>
     */
    private static final String SOURCE_CLASS
         = "source-class";

    // -------------------------------------------- Methods from ConfigProcessor


    /**
     * @see ConfigProcessor#process(javax.servlet.ServletContext,com.sun.faces.config.DocumentInfo[])
     */
    public void process(ServletContext sc, DocumentInfo[] documentInfos)
    throws Exception {

        Application app = getApplication();
        ApplicationAssociate associate =
              ApplicationAssociate.getInstance(
                    FacesContext.getCurrentInstance().getExternalContext());
        LinkedHashMap<String,Node> viewHandlers = new LinkedHashMap<String,Node>();
        LinkedHashSet<String> defaultValidatorIds = null;
        for (int i = 0; i < documentInfos.length; i++) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           MessageFormat.format("Processing application elements for document: ''{0}''",
                                                documentInfos[i].getSourceURL()));
            }
            Document document = documentInfos[i].getDocument();
            String namespace =
                 document.getDocumentElement().getNamespaceURI();
            NodeList applicationElements = document.getDocumentElement()
                 .getElementsByTagNameNS(namespace, APPLICATION);
            if (applicationElements != null
                && applicationElements.getLength() > 0) {
                for (int a = 0, asize = applicationElements.getLength();
                     a < asize;
                     a++) {
                    Node appElement = applicationElements.item(a);
                    NodeList children = ((Element) appElement)
                         .getElementsByTagNameNS(namespace, "*");
                    if (children != null && children.getLength() > 0) {
                        for (int c = 0, csize = children.getLength();
                             c < csize;
                             c++) {
                            Node n = children.item(c);
                            if (MESSAGE_BUNDLE.equals(n.getLocalName())) {
                                setMessageBundle(app, n);
                            } else if (DEFAULT_RENDERKIT_ID.equals(n.getLocalName())) {
                                setDefaultRenderKitId(app, n);
                            } else if (ACTION_LISTENER.equals(n.getLocalName())) {
                                addActionListener(app, n);
                            } else if (NAVIGATION_HANDLER.equals(n.getLocalName())) {
                                setNavigationHandler(app, n);
                            } else if (VIEW_HANDLER.equals(n.getLocalName())) {
                                String viewHandler = getNodeText(n);
                                if (viewHandler != null) {
                                    viewHandlers.put(viewHandler, n);
                                }
                            } else if (STATE_MANAGER.equals(n.getLocalName())) {
                                setStateManager(app, n);
                            } else if (EL_RESOLVER.equals(n.getLocalName())) {
                                addELResolver(associate, n);
                            } else if (PROPERTY_RESOLVER.equals(n.getLocalName())) {
                                addPropertyResolver(associate, n);
                            } else if (VARIABLE_RESOLVER.equals(n.getLocalName())) {
                                addVariableResolver(associate, n);
                            } else if (DEFAULT_LOCALE.equals(n.getLocalName())) {
                                setDefaultLocale(app, n);
                            } else if (SUPPORTED_LOCALE.equals(n.getLocalName())) {
                                addSupportedLocale(app, n);
                            } else if (RESOURCE_BUNDLE.equals(n.getLocalName())) {
                                addResouceBundle(associate, n);
                            } else if (RESOURCE_HANDLER.equals(n.getLocalName())) {
                                setResourceHandler(app, n);
                            } else if (SYSTEM_EVENT_LISTENER.equals(n.getLocalName())) {
                                addSystemEventListener(app, n);
                            } else if (DEFAULT_VALIDATORS.equals(n.getLocalName())) {
                                if (defaultValidatorIds == null) {
                                    defaultValidatorIds = new LinkedHashSet<String>();
                                } else {
                                    defaultValidatorIds.clear();
                                }
                            } else if (VALIDATOR_ID.equals(n.getLocalName())) {
                                defaultValidatorIds.add(getNodeText(n));
                            }
                        }
                    }
                }
            }
        }

        registerDefaultValidatorIds(app, defaultValidatorIds);

        // perform any special processing for ViewHandlers...
        processViewHandlers(app, viewHandlers);

        // process NamedEvent annotations, if any
        processAnnotations(NamedEvent.class);

        // continue processing...
        invokeNext(sc, documentInfos);

    }


    // --------------------------------------------------------- Private Methods

    /**
     * If defaultValidatorIds is null, then no &lt;default-validators&gt; element appeared in any configuration file.
     * In that case, add javax.faces.Bean if Bean Validation is available. If the &lt;default-validators&gt; appeared
     * at least once, don't add the default (and empty &lt;default-validator&gt; element disabled default validators)
     */
    private void registerDefaultValidatorIds(Application application, LinkedHashSet<String> defaultValidatorIds) {
        if (defaultValidatorIds == null) {
            defaultValidatorIds = new LinkedHashSet<String>();
            if (isBeanValidatorAvailable()) {
                WebConfiguration webConfig = WebConfiguration.getInstance();
                if (!webConfig.isOptionEnabled(WebConfiguration.BooleanWebContextInitParameter.DisableDefaultBeanValidator)) {
                    defaultValidatorIds.add("javax.faces.Bean");
                }
            }
        }

        for (String validatorId : defaultValidatorIds) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                        MessageFormat.format(
                        "Calling Application.addDefaultValidatorId({0})",
                        validatorId));
            }
            application.addDefaultValidatorId(validatorId);
        }
    }

    static boolean isBeanValidatorAvailable() {
        
        boolean result = false;
        final String beansValidationAvailabilityCacheKey = 
                "javax.faces.BEANS_VALIDATION_AVAILABLE";
        Map<String,Object> appMap = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        
        if (appMap.containsKey(beansValidationAvailabilityCacheKey)) {
            result = (Boolean) appMap.get(beansValidationAvailabilityCacheKey);
        } else {
            try {
                Thread.currentThread().getContextClassLoader().loadClass("javax.validation.MessageInterpolator");
                appMap.put(beansValidationAvailabilityCacheKey, result = true);
            } catch (ClassNotFoundException cnfe) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Unable to load Beans Validation");
                }
                appMap.put(beansValidationAvailabilityCacheKey, Boolean.FALSE);
            } catch (Throwable t) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Unable to load Beans Validation");
                }
                appMap.put(beansValidationAvailabilityCacheKey, Boolean.FALSE);
            }
        }

        return result;
    }

    private void setMessageBundle(Application application,
                                  Node messageBundle) {

        if (messageBundle != null) {
            String bundle = getNodeText(messageBundle);
            if (bundle != null) {

                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               MessageFormat.format(
                                    "Calling Application.setMessageBundle({0})",
                                    bundle));
                }
                application.setMessageBundle(bundle);
            }
        }

    }


    private void setDefaultRenderKitId(Application application,
                                       Node defaultId) {

        if (defaultId != null) {
            String id = getNodeText(defaultId);
            if (id != null) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               MessageFormat.format(
                                    "Calling Application.setDefaultRenderKitId({0})",
                                    id));
                }
                application.setDefaultRenderKitId(id);
            }
        }

    }

    private void addActionListener(Application application,
                                   Node actionListener) {

        if (actionListener != null) {

            String listener = getNodeText(actionListener);
            if (listener != null) {
                Object instance = createInstance(listener,
                                                 ActionListener.class,
                                                 application.getActionListener(),
                                                 actionListener);
                if (instance != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Calling Application.setActionListeners({0})",
                                        listener));
                    }
                    application
                         .setActionListener((ActionListener) instance);
                }
            }
        }
    }


    private void setNavigationHandler(Application application,
                                      Node navigationHandler) {

        if (navigationHandler != null) {

            String handler = getNodeText(navigationHandler);
            if (handler != null) {
                Class<?> rootType = findRootType(handler,
                                                 navigationHandler,
                                                 new Class[] {
                                                       ConfigurableNavigationHandler.class,
                                                       NavigationHandler.class
                                                     });
                Object instance = createInstance(handler,
                                                 ((rootType != null) ? rootType : NavigationHandler.class),
                                                 application.getNavigationHandler(),
                                                 navigationHandler);
                if (instance != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Calling Application.setNavigationHandlers({0})",
                                        handler));
                    }
                    application
                         .setNavigationHandler((NavigationHandler) instance);
                }
            }
        }

    }


    private void setStateManager(Application application,
                                 Node stateManager) {

        if (stateManager != null) {
            String manager = getNodeText(stateManager);
            if (manager != null) {
                Object instance = createInstance(manager,
                                                 StateManager.class,
                                                 application.getStateManager(),
                                                 stateManager);
                if (instance != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Calling Application.setStateManagers({0})",
                                        manager));
                    }
                    application.setStateManager((StateManager) instance);
                }
            }
        }

    }

    private void setViewHandler(Application application,
                                Node viewHandler) {

        if (viewHandler != null) {
            String handler = getNodeText(viewHandler);
            if (handler != null) {
                Object instance = createInstance(handler,
                                                 ViewHandler.class,
                                                 application.getViewHandler(),
                                                 viewHandler);
                if (instance != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Calling Application.setViewHandler({0})",
                                        handler));
                    }
                    application.setViewHandler((ViewHandler) instance);
                }
            }
        }

    }


    private void addELResolver(ApplicationAssociate associate,
                               Node elResolver) {

        if (elResolver != null) {
            if (associate != null) {
                List<ELResolver> resolvers = associate
                     .getELResolversFromFacesConfig();
                if (resolvers == null) {
                    //noinspection CollectionWithoutInitialCapacity
                    resolvers = new ArrayList<ELResolver>();
                    associate.setELResolversFromFacesConfig(resolvers);
                }
                String elResolverClass = getNodeText(elResolver);
                if (elResolverClass != null) {
                    Object elRes = createInstance(elResolverClass,
                                                  ELResolver.class,
                                                  null,
                                                  elResolver);
                    if (elRes != null) {
                        if (LOGGER.isLoggable(Level.FINE)) {
                            LOGGER.log(Level.FINE,
                                       MessageFormat.format(
                                            "Adding ''{0}'' to ELResolver chain",
                                            elResolverClass));
                        }
                        resolvers.add((ELResolver) elRes);
                    }
                }
            }
        }

    }


    @SuppressWarnings("deprecation")
    private void addPropertyResolver(ApplicationAssociate associate,
                                     Node propertyResolver) {

        if (propertyResolver != null) {
            if (associate != null) {
                Object resolverImpl = associate.getLegacyPRChainHead();
                if (resolverImpl == null) {
                    resolverImpl = new DummyPropertyResolverImpl();
                }

                String resolver = getNodeText(propertyResolver);
                if (resolver != null) {
                    resolverImpl = createInstance(resolver,
                                                  PropertyResolver.class,
                                                  resolverImpl,
                                                  propertyResolver);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Adding ''{0}'' to PropertyResolver chain",
                                        resolverImpl));
                    }
                }
                if (resolverImpl != null) {
                    associate
                         .setLegacyPRChainHead((PropertyResolver) resolverImpl);
                }
            }
        }

    }


    @SuppressWarnings("deprecation")
    private void addVariableResolver(ApplicationAssociate associate,
                                     Node variableResolver) {

        if (variableResolver != null) {
            if (associate != null) {
                Object resolverImpl = associate.getLegacyVRChainHead();
                if (resolverImpl == null) {
                    resolverImpl = new ChainAwareVariableResolver();
                }
                String resolver = getNodeText(variableResolver);
                if (resolver != null) {
                    resolverImpl = createInstance(resolver,
                                                  VariableResolver.class,
                                                  resolverImpl,
                                                  variableResolver);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Adding ''{0}'' to VariableResolver chain",
                                        resolverImpl));
                    }
                }
                if (resolverImpl != null) {
                    associate
                         .setLegacyVRChainHead((VariableResolver) resolverImpl);
                }
            }
        }

    }


    private void setDefaultLocale(Application application,
                                  Node defaultLocale) {
        if (defaultLocale != null) {
            String defLocale = getNodeText(defaultLocale);
            if (defLocale != null) {
                Locale def = Util.getLocaleFromString(defLocale);
                if (def != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Setting default Locale to ''{0}''",
                                        defLocale));
                    }
                    application.setDefaultLocale(def);
                }
            }
        }
    }


    private void addSupportedLocale(Application application,
                                    Node supportedLocale) {

        if (supportedLocale != null) {
            Set<Locale> sLocales = getCurrentLocales(application);
            String locString = getNodeText(supportedLocale);
            if (locString != null) {
                Locale loc = Util.getLocaleFromString(locString);
                if (loc != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Adding supported Locale ''{0}''",
                                        locString));
                    }
                    sLocales.add(loc);
                }
                application.setSupportedLocales(sLocales);
            }
        }

    }



    private void addResouceBundle(ApplicationAssociate associate,
                                  Node resourceBundle) {

        if (resourceBundle != null) {
            NodeList children = resourceBundle.getChildNodes();
            if (children != null) {
                String baseName = null;
                String var = null;
                List<Node> descriptions = null;
                List<Node> displayNames = null;
                for (int i = 0, size = children.getLength(); i < size; i++) {
                    Node n = children.item(i);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        if (BASE_NAME.equals(n.getLocalName())) {
                            baseName = getNodeText(n);
                        } else if (VAR.equals(n.getLocalName())) {
                            var = getNodeText(n);
                        } else if (RES_DESCRIPTIONS.equals(n.getLocalName())) {
                            if (descriptions == null) {
                                descriptions = new ArrayList<Node>(2);
                            }
                            descriptions.add(n);
                        } else if (RES_DISPLAY_NAMES.equals(n.getLocalName())) {
                            if (displayNames == null) {
                                displayNames = new ArrayList<Node>(2);
                            }
                            displayNames.add(n);
                        }
                    }
                }
                if ((baseName != null) && (var != null)) {
                    associate.addResourceBundle(var,
                                                new ApplicationResourceBundle(
                                                     baseName,
                                                     getTextMap(displayNames),
                                                     getTextMap(descriptions)));
                }
            }
        }
    }


    private Set<Locale> getCurrentLocales(Application application) {

        //noinspection CollectionWithoutInitialCapacity
        Set<Locale> supportedLocales = new HashSet<Locale>();
        for (Iterator<Locale> i = application.getSupportedLocales();
             i.hasNext();) {
            supportedLocales.add(i.next());
        }
        return supportedLocales;

    }


    private void setResourceHandler(Application application, Node resourceHandler) {

        if (resourceHandler != null) {
            String handler = getNodeText(resourceHandler);
            if (handler != null) {
                Object instance = createInstance(handler,
                                                 ResourceHandler.class,
                                                 application.getResourceHandler(),
                                                 resourceHandler);
                if (instance != null) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   MessageFormat.format(
                                        "Calling Application.setResourceHandler({0})",
                                        handler));
                    }
                    application.setResourceHandler((ResourceHandler) instance);
                }
            }
        }
    }


    private void addSystemEventListener(Application application,
                                        Node systemEventListener) {

        NodeList children = systemEventListener.getChildNodes();
        String listenerClass = null;
        String eventClass = null;
        String sourceClass = null;
        for (int j = 0, len = children.getLength(); j < len; j++) {
            Node n = children.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (SYSTEM_EVENT_LISTENER_CLASS.equals(n.getLocalName())) {
                    listenerClass = getNodeText(n);
                } else if (SYSTEM_EVENT_CLASS.equals(n.getLocalName())) {
                    eventClass = getNodeText(n);
                } else if (SOURCE_CLASS.equals(n.getLocalName())) {
                    sourceClass = getNodeText(n);
                }
            }
        }
        if (listenerClass != null) {
            SystemEventListener selInstance = (SystemEventListener)
                  createInstance(listenerClass,
                                 SystemEventListener.class,
                                 null,
                                 systemEventListener);
            if (selInstance != null) {
                try {
                    // If there is an eventClass, use it, otherwise use
                    // SystemEvent.class
                    //noinspection unchecked
                    Class<? extends SystemEvent> eventClazz =
                          (Class<? extends SystemEvent>) loadClass(eventClass, this, null);
                    // If there is a sourceClass, use it, otherwise use null
                    Class sourceClazz =
                          (sourceClass != null && sourceClass.length() != 0)
                          ? Util.loadClass(sourceClass, this.getClass())
                          : null;
                    application.subscribeToEvent(eventClazz,
                                                 sourceClazz,
                                                 selInstance);
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   "Subscribing for event {0} and source {1} using listener {2}",
                                   new Object[]{
                                         eventClazz.getName(),
                                         ((sourceClazz != null) ? sourceClazz
                                               .getName() : "ANY"),
                                         selInstance.getClass().getName()
                                   });
                    }
                } catch (ClassNotFoundException cnfe) {
                    throw new ConfigurationException(cnfe);
                }
            }
        }
    }


    private void processViewHandlers(Application app,
                                     LinkedHashMap<String, Node> viewHandlers) {
        // take special action on the ViewHandlers that have been
        // configured for the application.  If any of the ViewHandlers
        // is the FaceletViewHandler, don't install the 2.0
        // FaceletViewHandler.  Make the application behave as 1.2
        // unless they use our ViewHandler
        WebConfiguration webConfig = WebConfiguration.getInstance();
        if (!webConfig.isOptionEnabled(DisableFaceletJSFViewHandler)) {
            if (viewHandlers.containsKey("com.sun.facelets.FaceletViewHandler")) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                               "jsf.application.legacy_facelet_viewhandler_detected",
                               "com.sun.facelets.FaceletViewHandler");
                }
                webConfig.overrideContextInitParameter(DisableFaceletJSFViewHandler, true);
            }
        }
        for (Node n : viewHandlers.values()) {
            setViewHandler(app, n);
        }
    }

}
