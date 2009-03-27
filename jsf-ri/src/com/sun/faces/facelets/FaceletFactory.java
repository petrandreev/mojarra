/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.faces.facelets;

import javax.faces.view.facelets.FaceletException;
import java.io.IOException;

import java.net.URL;
import javax.el.ELException;
import javax.faces.FacesException;

/**
 * FaceletFactory for producing Facelets relative to the context of the
 * underlying implementation.
 * 
 * @author Jacob Hookom
 * @version $Id$
 */
public abstract class FaceletFactory {

    private static ThreadLocal<FaceletFactory> INSTANCE =
          new ThreadLocal<FaceletFactory>();

    /**
     * Return a Facelet instance as specified by the file at the passed URI.
     * 
     * @param uri
     * @return
     * @throws IOException
     * @throws FaceletException
     * @throws FacesException
     * @throws ELException
     */
    public abstract Facelet getFacelet(String uri) throws IOException;
    
    public abstract Facelet getFacelet(URL url) throws IOException;

    public abstract Facelet getMetadataFacelet(String uri) throws IOException;

    public abstract Facelet getMetadataFacelet(URL url) throws IOException;
    

    /**
     * NOT CURRENTLY USED.  However, this class may be moved to the API and
     * the TL lookup may end up being the preferred way to get a hold of this
     * instance.
     *
     * Set the static instance
     * 
     * @param factory
     */
    public static void setInstance(FaceletFactory factory) {
        if (factory == null) {
            INSTANCE.remove();
        } else {
            INSTANCE.set(factory);
        }
    }

    /**
     * NOT CURRENTLY USED.  However, this class may be moved to the API and
     * the TL lookup may end up being the preferred way to get a hold of this
     * instance.
     * 
     * @return
     */
    public static FaceletFactory getInstance() {
        return INSTANCE.get();
    }
}
