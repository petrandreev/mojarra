/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2014 Oracle and/or its affiliates. All rights reserved.
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
package com.sun.faces.test.javaee7.action.basic;

import javax.enterprise.context.RequestScoped;
import com.sun.faces.action.RequestMapping;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("basicBean")
@RequestScoped
public class BasicBean implements Serializable {
    private String value;

    public BasicBean() {
    }
    
    @RequestMapping(value = "/exact.xhtml")
    public String invoke() {
        return "/exact.xhtml";
    }
    
    @RequestMapping(value = "/prefix/*")
    public String prefix() {
        return "/prefix.xhtml";
    }
    
    @RequestMapping(value = "*.do")
    public String extension() {
        return "/extension.xhtml";
    }
    
    @RequestMapping(value = "/form1b.xhtml")
    public String form1() {
        String inputText1 = (String) FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("inputText1");
        setValue("We set inputText1 manually to - " + inputText1);
        return "/form1b.xhtml";
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
