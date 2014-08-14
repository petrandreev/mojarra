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

package com.sun.faces.test.javaee7.multi_tenant.cdi_jsp_only_app_sets_tccl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContextFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class BeforeFilter implements Filter {
    
    private FilterConfig filterConfig = null;
    
    public BeforeFilter() {
    }    
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        Thread thread = Thread.currentThread();
        ClassLoader tccl = thread.getContextClassLoader();
        ClassLoader tcclp1 = new URLClassLoader(new URL[0], tccl); 
        thread.setContextClassLoader(tcclp1);
        
        final boolean weldIsTCCLReplacementResilient = true;
        
        if (weldIsTCCLReplacementResilient) {
            try {
                chain.doFilter(request, response);
            } catch (Exception t) {
                HttpServletResponse resp = (HttpServletResponse) response;
                PrintWriter pw = resp.getWriter();
                try {
                    pw.print("<html><body><p id=\"result\">FAILURE</p>");
                    int indentLevel = 0;
                    String indent;
                    Throwable cause = t;
                    do {
                        StringBuilder indentBuilder = new StringBuilder();
                        for (int i = 0; i < indentLevel; i++) {
                            indentBuilder.append("&nbsp;&nbsp;");
                        }
                        indent = indentBuilder.toString();
                        pw.print("<p>" + indent + " Exception: " + cause.getClass().getName() + "</p>");
                        pw.print("<p>" + indent + " Exception Message: " + cause.getLocalizedMessage() + "</p>");
                        pw.print("<code><pre>");
                        cause.printStackTrace(pw);
                        pw.print("</pre></code>");
                    } while (null != (cause = cause.getCause()));
                    pw.print("</body></html>");
                    resp.setStatus(200);
                    pw.close();
                } catch (Exception e) {
                }
            } finally {
                thread.setContextClassLoader(tccl);
            }
        } else {
        }
        
    }

    public void destroy() {        
    }

    public void init(FilterConfig filterConfig) {        
        this.filterConfig = filterConfig;
    }
    
}
