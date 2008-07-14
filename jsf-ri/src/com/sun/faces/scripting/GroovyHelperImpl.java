/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.faces.scripting;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.Util;
import groovy.util.GroovyScriptEngine;


/**
 * Helper class to interface with the Groovy runtime.
 */
class GroovyHelperImpl extends GroovyHelper {


    private static final Logger LOGGER = FacesLogger.APPLICATION.getLogger();
    private static final String SCRIPT_PATH = "/WEB-INF/groovy/";

    private MojarraGroovyClassLoader loader;

    // ------------------------------------------------------------ Constructors


    GroovyHelperImpl() throws Exception {

        FacesContext ctx = FacesContext.getCurrentInstance();
        // only called during init - safe to cast and save.
        URL u = ctx.getExternalContext().getResource(SCRIPT_PATH);

        if (u != null) {

            final GroovyScriptEngine engine =
                  new GroovyScriptEngine(new URL[]{u},
                                         Thread.currentThread().getContextClassLoader());

            loader = AccessController.doPrivileged(
                  new PrivilegedAction<MojarraGroovyClassLoader>() {
                      public MojarraGroovyClassLoader run() {
                          return new MojarraGroovyClassLoader(engine);
                      }
                  });
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO,
                           "Groovy support enabled.");
            }
            ctx.getExternalContext().getApplicationMap()
                  .put("com.sun.faces.groovyhelper", this);

        } else {
            throw new UnsupportedOperationException();
        }
    }

    // ---------------------------------------------------------- Public Methods


    public Class<?> loadScript(String name) {
        try {
            String script = name;
            if (script.endsWith(".groovy")) {
                script = script.substring(0, script.indexOf(".groovy"));
            }
            //return engine.loadScriptByName(script);
            return Util.loadClass(script, this);
        } catch (Exception e) {
            throw new FacesException(e);
        }
    }    

    public void setClassLoader() {
        Thread.currentThread().setContextClassLoader(loader);
    }


    // ----------------------------------------------------------- Inner Classes

    private static final class MojarraGroovyClassLoader extends URLClassLoader {

        private GroovyScriptEngine gse;

        public MojarraGroovyClassLoader(GroovyScriptEngine gse) {
            super(new URL[0], gse.getGroovyClassLoader());
            this.gse = gse;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name == null) {
                throw new NullPointerException();
            }
            Class<?> c;
            try {
                c = gse.getGroovyClassLoader().getParent().loadClass(name);
            } catch (ClassNotFoundException cnfe) {
                try {
                    c = gse.loadScriptByName(name);
                } catch (Exception e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            if (c == null) {
                throw new ClassNotFoundException(name);
            }
            return c;
        }



    }

}
