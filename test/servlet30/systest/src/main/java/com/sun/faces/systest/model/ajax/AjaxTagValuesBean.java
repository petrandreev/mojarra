/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.faces.systest.model.ajax;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Arrays;

@ManagedBean(name="ajaxtag")
@SessionScoped
public class AjaxTagValuesBean {
    private Integer count = 0;
    private Boolean checked = false;
    private String text = "";
    private String[] outArray = { "out1", ":form2:out2", ":out3" };
    private Collection<String> outSet = new LinkedHashSet<String>(Arrays.asList(outArray));
    private String render = "out1";
    private String[] checkedvalues = {};
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Integer getCount() {
        return count++;
    }

    public void setCheckedValues(String[] values) {
        this.checkedvalues = values;
    }

    public String[] getCheckedValues() {
        return checkedvalues;
    }

    public String getCheckedValuesString() {
        String v = "Value: ";
        for (String value : checkedvalues) {
            v = v + value;
        }
        return v;
    }

    public void reset(ActionEvent ae) {
        count = 0;
        checked = false;
        text = "";
    }

    public Collection<String> getRenderList() {
        return outSet;
    }

    public String getRenderOne() {
        return render;
    }

    private String ajaxEvent = "valueChange";
    public void setAjaxEvent(String ajaxEvent) {
        this.ajaxEvent = ajaxEvent;
    }
    public String getAjaxEvent() {
        return ajaxEvent;
    }

}
