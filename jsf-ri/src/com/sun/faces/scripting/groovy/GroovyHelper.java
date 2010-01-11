/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.faces.scripting.groovy;

import com.sun.faces.util.Util;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.lang.reflect.Constructor;
import java.net.URL;

/**
 * Base class for interfacing with Groovy.
 */
public abstract class GroovyHelper {

    public static boolean isGroovyAvailable(FacesContext ctx) {

        return (ctx.getExternalContext().getApplicationMap().get("com.sun.faces.groovyhelper") != null);
        
    }

    public static GroovyHelper getCurrentInstance(FacesContext ctx) {

        return (GroovyHelper) ctx.getExternalContext().getApplicationMap().get("com.sun.faces.groovyhelper");

    }


    public static GroovyHelper getCurrentInstance(ServletContext sc) {
        return (GroovyHelper) sc.getAttribute("com.sun.faces.groovyhelper");
    }


    public static GroovyHelper getCurrentInstance() {

        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx != null) {
            return getCurrentInstance(ctx);
        }
        return null;

    }
    public abstract Class<?> loadScript(String name);

    public static Object newInstance(String name, Class<?> type, Object root)
    throws Exception {
        Class<?> delegate = Util.loadClass(name, GroovyHelper.class);
        try {
            Constructor decorationCtor = requiresDecoration(delegate, type, root);
            if (decorationCtor != null) {
                return decorationCtor.newInstance(root);
            } else {
                return delegate.newInstance();
            }
        } catch (Exception e) {
            throw new FacesException(e);
        }
    }

    public static Object newInstance(String name) throws Exception {
        return newInstance(name, null, null);
    }

    public abstract void setClassLoader();
    
    public abstract void addURL(URL toAdd);

    // --------------------------------------------------------- Private Methods


     private static Constructor requiresDecoration(Class<?> groovyClass, Class<?> ctorArgument, Object root) {
        if (root != null) {
            try {
                return groovyClass.getConstructor(ctorArgument);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
