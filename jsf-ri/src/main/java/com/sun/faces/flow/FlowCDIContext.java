/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 1997-2012 Oracle and/or its affiliates. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 * 
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 * 
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 * 
 * Contributor(s):
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

package com.sun.faces.flow;

import java.io.Serializable;
import javax.faces.flow.FlowScoped;
import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.flow.Flow;
import javax.faces.flow.FlowHandler;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

public class FlowCDIContext implements Context, Serializable {
    
    private static final long serialVersionUID = -7144653402477623609L;
    
    private Map<Contextual<?>, String> flowIds;
    
    private static final FlowScopeMapKey FLOW_SCOPE_MAP_KEY = new FlowScopeMapKey();
    
    // This should be vended from a factory for decoration purposes.
    
    public FlowCDIContext(Map<Contextual<?>, String> flowIds) {
        this.flowIds = flowIds;
    }
    
    private static final String PER_SESSION_BEAN_MAP_LIST = FlowCDIContext.class.getPackage().getName() + ".PER_SESSION_BEAN_MAP_LIST";
    private static final String PER_SESSION_CREATIONAL_LIST = FlowCDIContext.class.getPackage().getName() + ".PER_SESSION_CREATIONAL_LIST";
    
    // -------------------------------------------------------- Private Methods
    
    // <editor-fold defaultstate="collapsed" desc="Private helpers">       
    
    private static Map<Contextual<?>, Object> getFlowScopedBeanMapForCurrentFlow() {

        Map<Contextual<?>, Object> result = Collections.emptyMap();
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext extContext = context.getExternalContext();
        Map<String, Object> sessionMap = extContext.getSessionMap();
        Flow currentFlow = getCurrentFlow(context);
        
        if (null == currentFlow) {
            return null;
        }
        
        String flowBeansForWindowId = currentFlow.getIdForCurrentWindow(context) + "_beans";
        result = (Map<Contextual<?>, Object>) sessionMap.get(flowBeansForWindowId);
        if (null == result) {
            result = new ConcurrentHashMap<Contextual<?>, Object>();
            sessionMap.put(flowBeansForWindowId, result);
            ensureBeanMapCleanupOnSessionDestroyed(sessionMap, flowBeansForWindowId);
        }
        
        return result;
    }
    
    private static Map<Contextual<?>, CreationalContext<?>> getFlowScopedCreationalMapForCurrentFlow() {
        Map<Contextual<?>, CreationalContext<?>> result = Collections.emptyMap();
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext extContext = context.getExternalContext();
        Map<String, Object> sessionMap = extContext.getSessionMap();
        Flow currentFlow = getCurrentFlow(context);
        String creationalForWindowId = currentFlow.getIdForCurrentWindow(context) + "_creational";
        result = (Map<Contextual<?>, CreationalContext<?>>) sessionMap.get(creationalForWindowId);
        if (null == result) {
            result = new ConcurrentHashMap<Contextual<?>, CreationalContext<?>>();
            sessionMap.put(creationalForWindowId, result);
            ensureCreationalCleanupOnSessionDestroyed(sessionMap, creationalForWindowId);
        }
        
        return result;

    }
    
    private static void ensureBeanMapCleanupOnSessionDestroyed(Map<String, Object> sessionMap, String flowBeansForWindowId) {
        List<String> beanMapList = (List<String>) sessionMap.get(PER_SESSION_BEAN_MAP_LIST);
        if (null == beanMapList) {
            beanMapList = new ArrayList<String>();
            sessionMap.put(PER_SESSION_BEAN_MAP_LIST, beanMapList);
        }
        beanMapList.add(flowBeansForWindowId);
    }
    
    private static void ensureCreationalCleanupOnSessionDestroyed(Map<String, Object> sessionMap, String creationalForWindowId) {
        List<String> beanMapList = (List<String>) sessionMap.get(PER_SESSION_CREATIONAL_LIST);
        if (null == beanMapList) {
            beanMapList = new ArrayList<String>();
            sessionMap.put(PER_SESSION_CREATIONAL_LIST, beanMapList);
        }
        beanMapList.add(creationalForWindowId);
    }
    
    @SuppressWarnings({"FinalPrivateMethod"})
    private final void assertNotReleased() {
        if (!isActive()) {
            throw new IllegalStateException();
        }
    }
    
    private Flow getCurrentFlow() {
        Flow result = null;
        
        FacesContext context = FacesContext.getCurrentInstance();
        result = getCurrentFlow(context);
        
        return result;
    }
    
    private static Flow getCurrentFlow(FacesContext context) {
        FlowHandler flowHandler = context.getApplication().getFlowHandler();
        
        Flow result = flowHandler.getCurrentFlow(context);
        
        return result;
        
    }
    
    private static class FlowScopeMapKey implements Contextual<Object> {

        public Object create(CreationalContext<Object> cc) {
            return cc;
        }

        public void destroy(Object t, CreationalContext<Object> cc) {
            Map<Object,Object> flowScope = (Map<Object,Object>) t;
            flowScope.clear();
        }
        
    }
    
    
    // </editor-fold>    

    // <editor-fold defaultstate="collapsed" desc="Called from code not related to flow">       
    
    /*
     * Called from WebappLifecycleListener.sessionDestroyed()
     */
    
    public static void sessionDestroyed(HttpSessionEvent hse) {
        HttpSession session = hse.getSession();
        
        List<String> beanMapList = (List<String>) session.getAttribute(PER_SESSION_BEAN_MAP_LIST);
        if (null != beanMapList) {
            for (String cur : beanMapList) {
                Map<Contextual<?>, Object> beanMap = 
                        (Map<Contextual<?>, Object>) session.getAttribute(cur);
                beanMap.clear();
                session.removeAttribute(cur);
            }
            session.removeAttribute(PER_SESSION_BEAN_MAP_LIST);
            beanMapList.clear();
        }
        
        List<String> creationalList = (List<String>) session.getAttribute(PER_SESSION_CREATIONAL_LIST);
        if (null != creationalList) {
            for (String cur : creationalList) {
                Map<Contextual<?>, CreationalContext<?>> beanMap = 
                        (Map<Contextual<?>, CreationalContext<?>>) session.getAttribute(cur);
                beanMap.clear();
                session.removeAttribute(cur);
            }
            session.removeAttribute(PER_SESSION_CREATIONAL_LIST);
            creationalList.clear();
        }
        
        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Called from code related to flow">  
    
    static Map<Object, Object> getCurrentFlowScope() {
        Map<Contextual<?>, Object> flowScopedBeanMap = getFlowScopedBeanMapForCurrentFlow();
        Map<Object, Object> result = null;
        if (null != flowScopedBeanMap) {
            result = (Map<Object, Object>) flowScopedBeanMap.get(FLOW_SCOPE_MAP_KEY);
            if (null == result) {
                result = new ConcurrentHashMap<Object, Object>();
                flowScopedBeanMap.put(FLOW_SCOPE_MAP_KEY, result);
            }
        }
        return result; 
    }
        
    static void flowExited() {
        Map<Contextual<?>, Object> flowScopedBeanMap = getFlowScopedBeanMapForCurrentFlow();
        Map<Contextual<?>, CreationalContext<?>> creationalMap = getFlowScopedCreationalMapForCurrentFlow();
        assert(!flowScopedBeanMap.isEmpty());
        assert(!creationalMap.isEmpty());
        List<Contextual<?>> flowScopedBeansToRemove = new ArrayList<Contextual<?>>();
        
        for (Entry<Contextual<?>, Object> entry : flowScopedBeanMap.entrySet()) {
            Contextual owner = entry.getKey();
            Object bean = entry.getValue();
            CreationalContext creational = creationalMap.get(owner);
            
            owner.destroy(bean, creational);
            flowScopedBeansToRemove.add(owner);
        }
        
        for (Contextual<?> cur : flowScopedBeansToRemove) {
            flowScopedBeanMap.remove(cur);
            creationalMap.remove(cur);
        }
        
    }
    
    static void flowEntered() {
        Map<Contextual<?>, Object> flowScopedBeanMap = getFlowScopedBeanMapForCurrentFlow();
        Map<Contextual<?>, CreationalContext<?>> creationalMap = getFlowScopedCreationalMapForCurrentFlow();
        
        
        
        getCurrentFlowScope();
        
    }

// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="spi.Context implementation">       
    
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creational) {
        assertNotReleased();
        
        T result = get(contextual);
        
        if (null == result) {
            Map<Contextual<?>, Object> flowScopedBeanMap = getFlowScopedBeanMapForCurrentFlow();
            Map<Contextual<?>, CreationalContext<?>> creationalMap = getFlowScopedCreationalMapForCurrentFlow();
            
            synchronized (flowScopedBeanMap) {
                result = (T) flowScopedBeanMap.get(contextual);
                if (null == result) {
                    
                    FacesContext facesContext = FacesContext.getCurrentInstance();
                    FlowHandler flowHandler = facesContext.getApplication().getFlowHandler();
                    String flowIdForBean = flowIds.get(contextual);
                    if (!flowHandler.isActive(facesContext, flowIdForBean)) {
                        throw new ContextNotActiveException("Request to activate bean in flow '" + flowIdForBean + "', but that flow is not active.");
                    }

                    
                    result = contextual.create(creational);
                    
                    if (null != result) {
                        flowScopedBeanMap.put(contextual, result);
                        creationalMap.put(contextual, creational);
                    }
                }
            }
        }
        
        return result;

    }
    
    public <T> T get(Contextual<T> contextual) {
        assertNotReleased();
        
        return (T) getFlowScopedBeanMapForCurrentFlow().get(contextual);
    }
    
    public Class<? extends Annotation> getScope() {
        return FlowScoped.class;
    }
    
    public boolean isActive() {
        return null != getCurrentFlow();
    }
    
    void beforeShutdown(@Observes final BeforeShutdown event, BeanManager beanManager) {
    }
    
    // </editor-fold>
    
}
