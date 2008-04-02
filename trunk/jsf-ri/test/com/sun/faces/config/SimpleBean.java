/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * https://javaserverfaces.dev.java.net/CDDL.html or
 * legal/CDDLv1.0.txt. 
 * See the License for the specific language governing
 * permission and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at legal/CDDLv1.0.txt.    
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.faces.config;

public class SimpleBean {


    Integer intProp = null;

    private NonManagedBean nonManagedBean = null;
    private String footerClass = "column-footer";

    private String headerClass = "column-header";

    private String simpleProperty;


    // ------------------------------------------------------------ Constructors


    public SimpleBean() {
    }


    // ---------------------------------------------------------- Public Methods


    public String getFooterClass() {

        return footerClass;

    }


    public void setFooterClass(String footerClass) {

        this.footerClass = footerClass;

    }


    public String getHeaderClass() {

        return headerClass;

    }


    public void setHeaderClass(String headerClass) {

        this.headerClass = headerClass;

    }


    public NonManagedBean getNonManagedBean() {

        return nonManagedBean;

    }


    public void setNonManagedBean(NonManagedBean nmb) {

        nonManagedBean = nmb;

    }


    public String getSimpleProperty() {

        return simpleProperty;

    }


    public void setSimpleProperty(String simpleProperty) {

        this.simpleProperty = simpleProperty;

    }


    public boolean getFalseValue() {

        return false;

    }


    public Integer getIntProperty() {

        return intProp;

    }


    public boolean getTrueValue() {

        return true;

    }


    public void setIntProperty(Integer newVal) {

        intProp = newVal;

    }

}
