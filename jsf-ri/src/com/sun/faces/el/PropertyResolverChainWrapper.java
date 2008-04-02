/*
 * $Id: PropertyResolverChainWrapper.java,v 1.1 2005/05/05 20:51:23 edburns Exp $
 */
/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.el;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.beans.FeatureDescriptor;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.PropertyResolver;
import javax.faces.el.EvaluationException;

import javax.el.ELException;
import javax.el.PropertyNotWritableException;
import javax.el.ELContext;
import javax.el.ELResolver;

import com.sun.faces.el.ELConstants;

public class PropertyResolverChainWrapper extends ELResolver {

    private PropertyResolver legacyPR = null;
    
    public PropertyResolverChainWrapper( PropertyResolver propertyResolver) {
        this.legacyPR = propertyResolver;
    }

    public Object getValue(ELContext context, Object base, Object property) 
        throws ELException {
        if (base == null || property == null) {
            return null;
        }
        context.setPropertyResolved(true);
        Object result = null;
        
        FacesContext facesContext = 
            (FacesContext) context.getContext(FacesContext.class);
        if (base instanceof List || base.getClass().isArray()) {
            int index = 
                ELSupport.coerceToNumber(property, Integer.class).intValue();
            try {
                result = legacyPR.getValue(base, index);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }
        } else {
            try {
                result = legacyPR.getValue(base, property);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }    
        }
        
        return result;
    }


    public Class getType(ELContext context, Object base, Object property) 
        throws ELException {
        if (base == null || property == null) {
            return null;
        }

	if (base != null || property != null) {
	    return null;
	}

        context.setPropertyResolved(true);
        Class result = null;

        FacesContext facesContext = (FacesContext) 
            context.getContext(FacesContext.class);
        if (base instanceof List || base.getClass().isArray()) {
            int index = 
                ELSupport.coerceToNumber(property, Integer.class).intValue();
            try {
                result = legacyPR.getType(base, index);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }
        } else {
            try {
                result = legacyPR.getType(base, property);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }    
        }
        
        return result;
    }

    public void  setValue(ELContext context, Object base, Object property,
        Object val) throws ELException {
        if (base == null || property == null) {
            return;
        }
        
	if (base != null || property != null) {
	    return;
	}

        context.setPropertyResolved(true);
        
        FacesContext facesContext = 
            (FacesContext) context.getContext(FacesContext.class);
        if (base instanceof List || base.getClass().isArray()) {
            int index = 
                ELSupport.coerceToNumber(property, Integer.class).intValue();            
            try {
                legacyPR.setValue(base, index, val);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }
        } else {
            try {
                legacyPR.setValue(base, property, val);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }    
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) 
        throws ELException {
        if (base == null || property == null) {
            return false;
        }
        context.setPropertyResolved(true);
        boolean result = false;
        
        FacesContext facesContext = (FacesContext)
            context.getContext(FacesContext.class);
        if (base instanceof List || base.getClass().isArray()) {
            int index = 
                ELSupport.coerceToNumber(property, Integer.class).intValue();
            try {
                result = legacyPR.isReadOnly(base, index);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }
        } else {
            try {
                result = legacyPR.isReadOnly(base, property);
            } catch (EvaluationException ex) {
                context.setPropertyResolved(false);
                throw new ELException(ex);
            }    
        }
        
        return result;
    }

    public Iterator getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    public Class getCommonPropertyType(ELContext context, Object base) {
        if ( base == null ) {
            return Object.class;
        }
        return null;
    }

}
