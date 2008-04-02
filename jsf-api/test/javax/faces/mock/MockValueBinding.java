/*
 * $Id: MockValueBinding.java,v 1.7 2005/08/22 22:08:26 ofung Exp $
 */

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

package javax.faces.mock;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.Application;
import javax.faces.component.StateHolder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;


/**
 * <p>Mock implementation of {@link ValueBinding} that supports a limited
 * subset of expression evaluation functionality:</p>
 * <ul>
 * <li>Looking up the first name via the configured {@link VariableResolver}
 *     (which is also limited in capability).</li>
 * <li>Resolving the "." operator via the configured
 *     {@link PropertyResolver}.</li>
 * <li>Supports only <code>getValue()</code> and <code>setValue()</code>.</li>
 * </ul>
 */

public class MockValueBinding extends ValueBinding implements StateHolder {


    // ------------------------------------------------------------ Constructors


    public MockValueBinding() {

	this(null, null);

    }


    public MockValueBinding(Application application, String ref) {

        this.application = application;
	if (ref != null) {
	    if (ref.startsWith("#{") && ref.endsWith("}")) {
		ref = ref.substring(2, ref.length() - 1);
	    }
	}
        this.ref = ref;

    }


    // ------------------------------------------------------ Instance Variables


    private transient Application application; // Restored as necessary
    private String ref;


    // ---------------------------------------------------- ValueBinding Methods


    public Object getValue(FacesContext context)
        throws EvaluationException, PropertyNotFoundException {

        if (context == null) {
            throw new NullPointerException();
        }
        // System.out.println("getValue(" + ref + ")");
        List names = parse(ref);
        // for (int i = 0; i < names.size(); i++) {
        //     System.out.println("  names[" + i + "]=" + names.get(i));
        // }

        // Resolve the variable name
        VariableResolver vr = application().getVariableResolver();
        String name = (String) names.get(0);
        Object base = vr.resolveVariable(context, name);
        // System.out.println("  base=" + base);
        if (names.size() < 2) {
            return (base);
        }

        // Resolve the property names
        PropertyResolver pr = application().getPropertyResolver();
        for (int i = 1; i < names.size(); i++) {
            // System.out.println("  property=" + names.get(i));
            base = pr.getValue(base, (String) names.get(i));
        }

        // Return the resolved value
        // System.out.println("  result=" + base);
        return (base);

    }


    public void setValue(FacesContext context, Object value)
        throws EvaluationException, PropertyNotFoundException {

        if (context == null) {
            throw new NullPointerException();
        }
        // System.out.println("setValue(" + ref + "," + value + ")");
        List names = parse(ref);
        // for (int i = 0; i < names.size(); i++) {
        //     System.out.println("  names[" + i + "]=" + names.get(i));
        // }

        // Resolve the variable name
        VariableResolver vr = application().getVariableResolver();
        String name = (String) names.get(0);
        Object base = vr.resolveVariable(context, name);
        // System.out.println("  base=" + base);
        if (names.size() < 2) {
            if ("applicationScope".equals(name) ||
                "requestScope".equals(name) ||
                "sessionScope".equals(name)) {
                throw new ReferenceSyntaxException("Cannot set '" +
                                                   name + "'");
            }
            Map map = econtext().getRequestMap();
            if (map.containsKey(name)) {
                map.put(name, value);
                return;
            }
            map = econtext().getSessionMap();
            if ((map != null) && (map.containsKey(name))) {
                map.put(name, value);
                return;
            }
            map = econtext().getApplicationMap();
            if (map.containsKey(name)) {
                map.put(name, value);
                return;
            }
            econtext().getRequestMap().put(name, value);
            return;
        }

        // Resolve the property names
        PropertyResolver pr = application().getPropertyResolver();
        for (int i = 1; i < (names.size() - 1); i++) {
            // System.out.println("  property=" + names.get(i));
            base = pr.getValue(base, (String) names.get(i));
        }

        // Update the last property
        pr.setValue(base, (String) names.get(names.size() - 1), value);

    }


    public boolean isReadOnly(FacesContext context)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }


    public Class getType(FacesContext context)
        throws PropertyNotFoundException {

        throw new UnsupportedOperationException();

    }

    public String getExpressionString() {
	return "#{" + ref + "}";
    }




    // ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
	Object values[] = new Object[1];
	values[0] = ref;
	return (values);
    }


    public void restoreState(FacesContext context, Object state) {
	Object values[] = (Object[]) state;
	ref = (String) values[0];
    }


    private boolean transientFlag = false;


    public boolean isTransient() {
	return (this.transientFlag);
    }


    public void setTransient(boolean transientFlag) {
	this.transientFlag = transientFlag;
    }


    // ---------------------------------------------------------- Public Methods


    public String ref(){
	return (this.ref);
    }


    // --------------------------------------------------------- Private Methods


    private Application application() {

	if (application == null) {
	    application = FacesContext.getCurrentInstance().getApplication();
	}
	return (application);

    }


    private ExternalContext econtext() {

        return (FacesContext.getCurrentInstance().getExternalContext());

    }


    private List parse(String ref) {

        String expr = ref;
        List names = new ArrayList();
        while (expr.length() > 0) {
            int period = expr.indexOf(".");
            if (period >= 0) {
                names.add(expr.substring(0, period));
                expr = expr.substring(period + 1);
            } else {
                names.add(expr);
                expr = "";
            }
        }
        if (names.size() < 1) {
            throw new ReferenceSyntaxException("No expression in '" +
                                               ref + "'");
        }
        for (int i = 0; i < names.size(); i++) {
            String name = (String) names.get(i);
            if (name.length() < 1) {
                throw new ReferenceSyntaxException("Invalid expression '" +
                                                   ref + "'");
            }
        }
        return (names);

    }


}
