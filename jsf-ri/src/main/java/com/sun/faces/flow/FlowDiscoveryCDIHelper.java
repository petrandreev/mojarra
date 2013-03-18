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

import com.sun.faces.flow.builder.FlowBuilderImpl;
import com.sun.faces.RIConstants;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.Util;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Producer;
import javax.faces.context.FacesContext;
import javax.faces.flow.Flow;
import javax.faces.flow.builder.FlowBuilder;
import javax.faces.flow.builder.FlowDefinition;
import javax.faces.flow.FlowHandler;
import javax.faces.flow.builder.FlowBuilderParameter;
import javax.inject.Named;

/*
 * This is an application scoped bean named with a well-defined,
 * but Mojarra private, name.  ApplicationAssociate.loadFlowsFromJars()
 * uses this class to cause any flows defined in this way to be 
 * built using the FlowBuilder API.
 * 
 * A better way is to @Inject the extension directly but this doesn't
 * seem to work in the version of weld we have.
 */

@Named(RIConstants.FLOW_DISCOVERY_CDI_HELPER_BEAN_NAME)
@ApplicationScoped
public class FlowDiscoveryCDIHelper implements Serializable {
    
    private static final long serialVersionUID = 2010898398003809226L;
    
    private static final Logger LOGGER = FacesLogger.FLOW.getLogger();
    
    public FlowDiscoveryCDIHelper() {
    }
    
    public void discoverFlows(FacesContext context, FlowHandler flowHandler) {
        BeanManager beanManager = (BeanManager) Util.getCDIBeanManager(context.getExternalContext().getApplicationMap());
        
        FlowDiscoveryCDIContext flowDiscoveryContext = (FlowDiscoveryCDIContext) beanManager.getContext(FlowDefinition.class);
        List<Producer<Flow>> flowProducers = flowDiscoveryContext.getFlowProducers();
        
        for (Producer<Flow> cur : flowProducers) {
            Flow toAdd = cur.produce(beanManager.<Flow>createCreationalContext(null));
            if (null == toAdd) {
                LOGGER.log(Level.SEVERE, "Flow producer method {0}() returned null.  Ignoring.",
                        new String [] { cur.toString() });
            } else {
                flowHandler.addFlow(context, toAdd);
            }
        }
        
    }
    
    @Produces @FlowBuilderParameter
    FlowBuilder createFlowBuilder() {
        FacesContext context = FacesContext.getCurrentInstance();
        FlowBuilder result = new FlowBuilderImpl(context);
        return result;
    }
    
}
