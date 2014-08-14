package com.sun.faces.test.agnostic.facelets.composite;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class AttributeELInputBean {

    private Boolean _ismandatory = true;

    public Boolean isMandatory() {
        return _ismandatory;
    }

    public void setMandatory(Boolean mandatory) {
        _ismandatory = mandatory;
    }
}
