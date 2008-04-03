/*
 * $Id: ApplyRequestValuesPhase.java,v 1.26 2007/07/19 15:01:56 rlubke Exp $
 */

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

package com.sun.faces.lifecycle;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import java.util.logging.Logger;
import java.util.logging.Level;

import com.sun.faces.util.FacesLogger;

/**
 * ApplyRequestValuesPhase executes <code>processDecodes</code> on each
 * component in the tree so that it may update it's current value from the
 * information included in the current request (parameters, headers, c
 * cookies and so on.)
 */
public class ApplyRequestValuesPhase extends Phase {

    // Log instance for this class
    private static Logger LOGGER = FacesLogger.LIFECYCLE.getLogger();


    // ---------------------------------------------------------- Public Methods


    public void execute(FacesContext facesContext) throws FacesException {

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Entering ApplyRequestValuesPhase");
        }

        UIComponent component = facesContext.getViewRoot();
        assert (null != component);

        try {
            component.processDecodes(facesContext);
        } catch (RuntimeException re) {
            String exceptionMessage = re.getMessage();
            if (null != exceptionMessage) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,exceptionMessage, re);
                }
            }
            throw new FacesException(exceptionMessage, re);
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Exiting ApplyRequestValuesPhase");
        }

    }


    public PhaseId getId() {

        return PhaseId.APPLY_REQUEST_VALUES;

    }


    // The testcase for this class is TestApplyRequestValuesPhase.java

} // end of class ApplyRequestValuesPhase
