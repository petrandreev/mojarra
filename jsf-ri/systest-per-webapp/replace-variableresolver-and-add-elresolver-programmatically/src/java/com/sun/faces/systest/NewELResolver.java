/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Sun Microsystems, Inc. All rights reserved.
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
package com.sun.faces.systest;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class NewELResolver extends ELResolver {

    public NewELResolver() {
        FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put("newER", this);
    }

    public NewELResolver(FacesContext context) {
        context.getExternalContext().getApplicationMap().put("newER", this);
    }


    @Override
    public Class<?> getCommonPropertyType(ELContext elc, Object o) {
        return Object.class;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elc, Object o) {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    public Class<?> getType(ELContext elc, Object o, Object o1) {
        return Object.class;
    }

    @Override
    public Object getValue(ELContext elc, Object name, Object property) {
        if (property.equals("traceResolution")) {
            Bean.captureStackTrace((FacesContext)elc.getContext(FacesContext.class));
        }

        return null;
    }

    @Override
    public boolean isReadOnly(ELContext elc, Object o, Object o1) {
        boolean result = false;
        FacesContext facesContext = (FacesContext) elc.getContext(FacesContext.class);

        if (null != o) {
            if (o.equals("newERDirect")) {
                facesContext.getExternalContext().getRequestMap().put("newERDirect",
                        "isReadOnly invoked directly");
                elc.setPropertyResolved(true);
                result = true;
            } else if (o.equals("newERThruChain")) {
                facesContext.getExternalContext().getRequestMap().put("newERThruChain",
                        "isReadOnly invoked thru chain");
                elc.setPropertyResolved(true);
                result = true;
            }

        }
        return result;
    }

    @Override
    public void setValue(ELContext elc, Object o, Object o1, Object o2) {
        
    }



}
