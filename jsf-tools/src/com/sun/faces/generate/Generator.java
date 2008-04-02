/*
 * $Id: Generator.java,v 1.2 2005/08/22 22:12:24 ofung Exp $
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

package com.sun.faces.generate;

import com.sun.faces.config.beans.FacesConfigBean;

/**
 * <p>Base interface for all <code>jsf-tools</code> generators.</p>
 */
public interface Generator {

    /**
     * <p>Perform whatever generation tasks are necessary using
     * the provided <code>FacesConfigBean</code> as the model.
     *
     * @param configBean model data
     */
    public void generate(FacesConfigBean configBean);

}
