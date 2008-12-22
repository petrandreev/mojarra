/*
 * $Id: ApplicationAssociate.java,v 1.55 2008/04/01 15:18:41 rlubke Exp $
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

package com.sun.faces.application;

import com.sun.faces.RIConstants;
import com.sun.faces.scripting.GroovyHelper;
import com.sun.faces.application.resource.ResourceCache;
import com.sun.faces.application.resource.ResourceManager;
import com.sun.faces.application.annotation.AnnotationManager;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.facelets.compiler.Compiler;
import com.sun.faces.facelets.compiler.SAXCompiler;
import com.sun.faces.facelets.FaceletFactory;
import com.sun.faces.facelets.tag.TagDecorator;
import com.sun.faces.facelets.tag.composite.CompositeLibrary;
import com.sun.faces.facelets.tag.jstl.core.JstlCoreLibrary;
import com.sun.faces.facelets.tag.jstl.fn.JstlFnLibrary;
import com.sun.faces.facelets.tag.ui.UILibrary;
import com.sun.faces.facelets.tag.jsf.core.CoreLibrary;
import com.sun.faces.facelets.tag.jsf.html.HtmlLibrary;
import com.sun.faces.facelets.util.ReflectionUtil;
import com.sun.faces.facelets.impl.ResourceResolver;
import com.sun.faces.facelets.impl.DefaultResourceResolver;
import com.sun.faces.facelets.impl.DefaultFaceletFactory;
import com.sun.faces.mgbean.BeanManager;
import com.sun.faces.spi.InjectionProvider;
import com.sun.faces.spi.InjectionProviderFactory;
import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;
import com.sun.faces.util.FacesLogger;
import static com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter;

import javax.el.CompositeELResolver;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.el.VariableResolver;
import javax.faces.application.ProjectStage;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.application.NavigationCase;

/**
 * <p>Break out the things that are associated with the Application, but
 * need to be present even when the user has replaced the Application
 * instance.</p>
 * <p/>
 * <p>For example: the user replaces ApplicationFactory, and wants to
 * intercept calls to createValueExpression() and createMethodExpression() for
 * certain kinds of expressions, but allow the existing application to
 * handle the rest.</p>
 */

public class ApplicationAssociate {

    private static final Logger LOGGER = FacesLogger.APPLICATION.getLogger();

    private ApplicationImpl app = null;

    /**
     * Overall Map containing <code>from-view-id</code> key and
     * <code>ArrayList</code> of <code>NavigationCase</code>
     * objects for that key; The <code>from-view-id</code> strings in
     * this map will be stored as specified in the configuration file -
     * some of them will have a trailing asterisk "*" signifying wild
     * card, and some may be specified as an asterisk "*".
     */
    private Map<String, List<NavigationCase>> caseListMap = null;

    /**
     * The List that contains all view identifier strings ending in an
     * asterisk "*".  The entries are stored without the trailing
     * asterisk.
     */
    private TreeSet<String> wildcardMatchList = null;

    // Flag indicating that a response has been rendered.
    private boolean responseRendered = false;

    private static final String ASSOCIATE_KEY = RIConstants.FACES_PREFIX +
         "ApplicationAssociate";

    private static ThreadLocal<ApplicationAssociate> instance =
        new ThreadLocal<ApplicationAssociate>() {
            protected ApplicationAssociate initialValue() {
                return (null);
            }
        };

    private List<ELResolver> elResolversFromFacesConfig = null;

    @SuppressWarnings("deprecation")
    private VariableResolver legacyVRChainHead = null;

    @SuppressWarnings("deprecation")
    private PropertyResolver legacyPRChainHead = null;
    private ExpressionFactory expressionFactory = null;

    @SuppressWarnings("deprecation")
    private PropertyResolver legacyPropertyResolver = null;

    @SuppressWarnings("deprecation")
    private VariableResolver legacyVariableResolver = null;
    private CompositeELResolver facesELResolverForJsp = null;

    private InjectionProvider injectionProvider;
    private ResourceCache resourceCache;

    private String contextName;
    private boolean requestServiced;

    private BeanManager beanManager;
    private GroovyHelper groovyHelper;
    private AnnotationManager annotationManager;
    private boolean devModeEnabled;
    private Compiler compiler;
    private FaceletFactory faceletFactory;
    private ResourceManager resourceManager;

    private PropertyEditorHelper propertyEditorHelper;

    private NamedEventManager namedEventManager;

    public ApplicationAssociate(ApplicationImpl appImpl) {
        app = appImpl;

        propertyEditorHelper = new PropertyEditorHelper(appImpl);

        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx == null) {
            throw new IllegalStateException(
                 MessageUtils.getExceptionMessageString(
                      MessageUtils.APPLICATION_ASSOCIATE_CTOR_WRONG_CALLSTACK_ID));
        }
        ExternalContext externalContext = ctx.getExternalContext();
        if (null != externalContext.getApplicationMap().get(ASSOCIATE_KEY)) {
            throw new IllegalStateException(
                 MessageUtils.getExceptionMessageString(
                      MessageUtils.APPLICATION_ASSOCIATE_EXISTS_ID));
        }
        externalContext.getApplicationMap().put(ASSOCIATE_KEY, this);
        //noinspection CollectionWithoutInitialCapacity
        caseListMap = new ConcurrentHashMap<String, List<NavigationCase>>();
        wildcardMatchList = new TreeSet<String>(new SortIt());
        injectionProvider = InjectionProviderFactory.createInstance(externalContext);
        WebConfiguration webConfig = WebConfiguration.getInstance(externalContext);
        beanManager = new BeanManager(injectionProvider,
                                      webConfig.isOptionEnabled(
                                           BooleanWebContextInitParameter.EnableLazyBeanValidation));
        annotationManager = new AnnotationManager();

        groovyHelper = GroovyHelper.getCurrentInstance();

        // initialize Facelets
        if (!webConfig.isOptionEnabled(BooleanWebContextInitParameter.DisableFaceletJSFViewHandler)) {
            compiler = createCompiler(webConfig);
            faceletFactory = createFaceletFactory(compiler, webConfig);
            devModeEnabled = (appImpl.getProjectStage() == ProjectStage.Development);
        }

        if (devModeEnabled) {
            resourceCache = new ResourceCache();
        }
        resourceManager = new ResourceManager(resourceCache);
        
    }

    public static ApplicationAssociate getInstance(ExternalContext
         externalContext) {
        if (externalContext == null) {
            return null;
        }
        Map applicationMap = externalContext.getApplicationMap();
        return ((ApplicationAssociate)
             applicationMap.get(ASSOCIATE_KEY));
    }

    public static ApplicationAssociate getInstance(ServletContext context) {
        if (context == null) {
            return null;
        }
        return (ApplicationAssociate) context.getAttribute(ASSOCIATE_KEY);
    }

    public static void setCurrentInstance(ApplicationAssociate associate) {

        if (associate == null) {
            instance.remove();
        } else {
            instance.set(associate);
        }
        
    }

    public static ApplicationAssociate getCurrentInstance() {

        ApplicationAssociate associate = instance.get();
        if (associate == null) {
            // Fallback to ExternalContext lookup
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                ExternalContext extContext = fc.getExternalContext();
                if (extContext != null) {
                    return ApplicationAssociate.getInstance(extContext);
                }
            }
        }

        return associate;

    }


    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public ResourceCache getResourceCache() {
        return resourceCache;
    }

    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }

    public Compiler getCompiler() {
        return compiler;
    }

    public FaceletFactory getFaceletFactory() {
        return faceletFactory;
    }

    public static void clearInstance(ExternalContext
         externalContext) {
        Map applicationMap = externalContext.getApplicationMap();
        ApplicationAssociate me = (ApplicationAssociate) applicationMap.get(ASSOCIATE_KEY);
        if (null != me) {
            if (null != me.resourceBundles) {
                me.resourceBundles.clear();
            }
        }
        applicationMap.remove(ASSOCIATE_KEY);
    }


    public BeanManager getBeanManager() {
        return beanManager;
    }

    public GroovyHelper getGroovyHelper() {
        return groovyHelper;
    }

    public boolean isDevModeEnabled() {
        return devModeEnabled;
    }

    /**
     * Obtain the PropertyEditorHelper instance for this app.
     *
     * @return
     */
    public PropertyEditorHelper getPropertyEditorHelper() {
        return propertyEditorHelper;
    }

    /**
     * This method is called by <code>ConfigureListener</code> and will
     * contain any <code>VariableResolvers</code> defined within
     * faces-config configuration files.
     *
     * @param resolver VariableResolver
     */
    @SuppressWarnings("deprecation")
    public void setLegacyVRChainHead(VariableResolver resolver) {
        this.legacyVRChainHead = resolver;
    }

    @SuppressWarnings("deprecation")
    public VariableResolver getLegacyVRChainHead() {
        return legacyVRChainHead;
    }

    /**
     * This method is called by <code>ConfigureListener</code> and will
     * contain any <code>PropertyResolvers</code> defined within
     * faces-config configuration files.
     *
     * @param resolver PropertyResolver
     */
    @SuppressWarnings("deprecation")
    public void setLegacyPRChainHead(PropertyResolver resolver) {
        this.legacyPRChainHead = resolver;
    }

    @SuppressWarnings("deprecation")
    public PropertyResolver getLegacyPRChainHead() {
        return legacyPRChainHead;
    }

    public CompositeELResolver getFacesELResolverForJsp() {
        return facesELResolverForJsp;
    }

    public void setFacesELResolverForJsp(CompositeELResolver celr) {
        facesELResolverForJsp = celr;
    }

    public void setELResolversFromFacesConfig(List<ELResolver> resolvers) {
        this.elResolversFromFacesConfig = resolvers;
    }

    public List<ELResolver> getELResolversFromFacesConfig() {
        return elResolversFromFacesConfig;
    }

    public void setExpressionFactory(ExpressionFactory expressionFactory) {
        this.expressionFactory = expressionFactory;
    }

    public ExpressionFactory getExpressionFactory() {
        return this.expressionFactory;
    }

    public List<ELResolver> getApplicationELResolvers() {
        return app.getApplicationELResolvers();
    }

    public InjectionProvider getInjectionProvider() {
        return injectionProvider;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getContextName() {
        return contextName;
    }

    /**
     * Maintains the PropertyResolver called through
     * Application.setPropertyResolver()
     * @param resolver PropertyResolver
     */
    @SuppressWarnings("deprecation")
    public void setLegacyPropertyResolver(PropertyResolver resolver) {
        this.legacyPropertyResolver = resolver;
    }

    /**
     * @return the PropertyResolver called through
     * Application.getPropertyResolver()
     */
    @SuppressWarnings("deprecation")
    public PropertyResolver getLegacyPropertyResolver() {
        return legacyPropertyResolver;
    }

    /**
     * Maintains the PropertyResolver called through
     * Application.setVariableResolver()
     * @param resolver VariableResolver
     */
    @SuppressWarnings("deprecation")
    public void setLegacyVariableResolver(VariableResolver resolver) {
        this.legacyVariableResolver = resolver;
    }

    /**
     * @return the VariableResolver called through
     * Application.getVariableResolver()
     */
    @SuppressWarnings("deprecation")
    public VariableResolver getLegacyVariableResolver() {
        return legacyVariableResolver;
    }


    /**
     * Called by application code to indicate we've processed the
     * first request to the application.
     */
    public void setRequestServiced() {
        this.requestServiced = true;
    }

    /**
     * @return <code>true</code> if we've processed a request, otherwise
     *         <code>false</code>
     */
    public boolean hasRequestBeenServiced() {
        return requestServiced;
    }

    /**
     * The "key" is defined as the combination of
     * <code>from-view-id</code><code>from-action</code>
     * <code>from-outcome</code>.
     * @return the derived key
     */
     private String getNavigationKey(NavigationCase navCase) {
         String key = null;
         key = navCase.getFromViewId()
                   + ((navCase.getFromAction() == null) ? "-" : navCase.getFromAction())
                   + ((navCase.getFromOutcome() == null) ? "-" : navCase.getFromOutcome());
         
         return key;
     }


    /**
     * Add a navigation case to the internal case list.  If a case list
     * does not already exist in the case list map containing this case
     * (identified by <code>from-view-id</code>), start a new list,
     * add the case to it, and store the list in the case list map.
     * If a case list already exists, see if a case entry exists in the list
     * with a matching <code>from-view-id</code><code>from-action</code>
     * <code>from-outcome</code> combination.  If there is suach an entry,
     * overwrite it with this new case.  Otherwise, add the case to the list.
     *
     * @param navigationCase the navigation case containing navigation
     *                       mapping information from the configuration file.
     */
    public void addNavigationCase(NavigationCase navigationCase) {

        String fromViewId = navigationCase.getFromViewId();
        List<NavigationCase> caseList = caseListMap.get(fromViewId);
        if (caseList == null) {
            //noinspection CollectionWithoutInitialCapacity
            caseList = new ArrayList<NavigationCase>();
            caseList.add(navigationCase);
            caseListMap.put(fromViewId, caseList);
        } else {
            String key = getNavigationKey(navigationCase);
            boolean foundIt = false;
            for (int i = 0; i < caseList.size(); i++) {
                NavigationCase navCase = caseList.get(i);
                // if there already is a case existing for the
                // fromviewid/fromaction.fromoutcome combination,
                // replace it ...  (last one wins).
                //
                if (key.equals(getNavigationKey(navCase))) {
                    caseList.set(i, navigationCase);
                    foundIt = true;
                    break;
                }
            }
            if (!foundIt) {
                caseList.add(navigationCase);
            }
        }
        if (fromViewId.endsWith("*")) {
            fromViewId =
                 fromViewId.substring(0, fromViewId.lastIndexOf('*'));
            wildcardMatchList.add(fromViewId);
        }

    }
    
    

    public synchronized NamedEventManager getNamedEventManager() {
        if (namedEventManager == null) {
            namedEventManager = new NamedEventManager();
        }
        return namedEventManager;
    }


    /**
     * Return a <code>Map</code> of navigation mappings loaded from
     * the configuration system.  The key for the returned <code>Map</code>
     * is <code>from-view-id</code>, and the value is a <code>List</code>
     * of navigation cases.
     *
     * @return Map the map of navigation mappings.
     */
    public Map<String, List<NavigationCase>> getNavigationCaseListMappings() {
        if (caseListMap == null) {
            return Collections.emptyMap();
        }
        return caseListMap;
    }


    /**
     * Return all navigation mappings whose <code>from-view-id</code>
     * contained a trailing "*".
     *
     * @return <code>TreeSet</code> The navigation mappings sorted in
     *         descending order.
     */
    public TreeSet<String> getNavigationWildCardList() {
        return wildcardMatchList;
    }

    public ResourceBundle getResourceBundle(FacesContext context,
                                            String var) {
        ApplicationResourceBundle bundle = resourceBundles.get(var);
        if (bundle == null) {
            return null;
        }
        UIViewRoot root;
        // Start out with the default locale
        Locale locale;
        Locale defaultLocale = Locale.getDefault();
        locale = defaultLocale;
        // See if this FacesContext has a ViewRoot
        if (null != (root = context.getViewRoot())) {
            // If so, ask it for its Locale
            if (null == (locale = root.getLocale())) {
                // If the ViewRoot has no Locale, fall back to the default.
                locale = defaultLocale;
            }
        }
        assert (null != locale);
        //ResourceBundleBean bean = resourceBundles.get(var);
        return bundle.getResourceBundle(locale);

    }

    /**
     * keys: <var> element from faces-config<p>
     * <p/>
     * values: ResourceBundleBean instances.
     */

    @SuppressWarnings({"CollectionWithoutInitialCapacity"})
    Map<String, ApplicationResourceBundle> resourceBundles =
         new HashMap<String, ApplicationResourceBundle>();

    public void addResourceBundle(String var, ApplicationResourceBundle bundle) {
        resourceBundles.put(var, bundle);
    }

    public Map<String, ApplicationResourceBundle> getResourceBundles() {
        return resourceBundles;
    }

    // This is called by ViewHandlerImpl.renderView().
    public void responseRendered() {
        responseRendered = true;
    }

    public boolean isResponseRendered() {
        return responseRendered;
    }


    protected FaceletFactory createFaceletFactory(Compiler c, WebConfiguration webConfig) {

        // refresh period
        String refreshPeriod = webConfig
              .getOptionValue(WebConfiguration.WebContextInitParameter.FaceletsDefaultRefreshPeriod);
        long period = Long.parseLong(refreshPeriod);

        // resource resolver
        ResourceResolver resolver = new DefaultResourceResolver();
        String resolverName = webConfig
              .getOptionValue(WebConfiguration.WebContextInitParameter.FaceletsResourceResolver);
        if (resolverName != null && resolverName.length() > 0) {
            try {
                resolver = (ResourceResolver) ReflectionUtil.forName(resolverName)
                        .newInstance();
            } catch (Exception e) {
                throw new FacesException("Error Initializing ResourceResolver["
                        + resolverName + "]", e);
            }
        }

        // Resource.getResourceUrl(ctx,"/")
        return new DefaultFaceletFactory(c, resolver, period);

    }


    protected Compiler createCompiler(WebConfiguration webConfig) {

        Compiler c = new SAXCompiler();

        // load decorators
        String decParam = webConfig
              .getOptionValue(WebConfiguration.WebContextInitParameter.FaceletsDecorators);
        if (decParam != null) {
            decParam = decParam.trim();
            String[] decs = Util.split(decParam, ";");
            TagDecorator decObj;
            for (int i = 0; i < decs.length; i++) {
                try {
                    decObj = (TagDecorator) ReflectionUtil.forName(decs[i])
                          .newInstance();
                    c.addTagDecorator(decObj);

                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   "Successfully Loaded Decorator: {0}",
                                   decs[i]);
                    }
                } catch (Exception e) {
                    if (LOGGER.isLoggable(Level.SEVERE)) {
                        LOGGER.log(Level.SEVERE,
                                   "Error Loading Decorator: " + decs[i],
                                   e);
                    }
                }
            }
        }

        // skip params?
        c.setTrimmingComments(
              webConfig.isOptionEnabled(
                    BooleanWebContextInitParameter.FaceletsSkipComments));

        c.addTagLibrary(new CoreLibrary());
        c.addTagLibrary(new HtmlLibrary());
        c.addTagLibrary(new UILibrary());
        c.addTagLibrary(new JstlCoreLibrary());
        c.addTagLibrary(new JstlFnLibrary());
        c.addTagLibrary(new CompositeLibrary());

        return c;

    }


    /**
     * This Comparator class will help sort the <code>NavigationCaseImpl</code> objects
     * based on their <code>fromViewId</code> properties in descending order -
     * largest string to smallest string.
     */
    static class SortIt implements Comparator<String> {

        public int compare(String fromViewId1, String fromViewId2) {
            return -(fromViewId1.compareTo(fromViewId2));
        }
    }

}
