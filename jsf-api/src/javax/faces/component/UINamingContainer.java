/*
 * $Id: UINamingContainer.java,v 1.19 2007/04/27 22:00:05 ofung Exp $
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

package javax.faces.component;

import javax.faces.context.FacesContext;



/**
 * <p><strong class="changed_modified_2_0">UINamingContainer</strong> is 
 * a convenience base class for
 * components that wish to implement {@link NamingContainer} functionality.</p>
 */

public class UINamingContainer extends UIComponentBase
    implements NamingContainer {


    // ------------------------------------------------------ Manifest Constants


    /**
     * <p>The standard component type for this component.</p>
     */
    public static final String COMPONENT_TYPE = "javax.faces.NamingContainer";


    /**
     * <p>The standard component family for this component.</p>
     */
    public static final String COMPONENT_FAMILY = "javax.faces.NamingContainer";

    /**
     * <p class="changed_added_2_0">The context-param that
     * allows the separator char for clientId strings to be set on a
     * per-web application basis.</p>
     *
     * @since 2.0
     */
    public static final String SEPARATOR_CHAR_PARAM_NAME = "javax.faces.SEPARATOR_CHAR";

    // ------------------------------------------------------------ Constructors


    /**
     * <p>Create a new {@link UINamingContainer} instance with default property
     * values.</p>
     */
    public UINamingContainer() {

        super();
        setRendererType(null);

    }

    // -------------------------------------------------------------- Properties


    public String getFamily() {

        return (COMPONENT_FAMILY);

    }

    /**
     * <p class="changed_added_2_0">Return the character used to
     * separate segments of a clientId.  The implementation must
     * determine if there is a &lt;<code>context-param</code>&gt; with
     * the value given by the value of the symbolic constant {@link
     * #SEPARATOR_CHAR_PARAM_NAME}.  If there is a value for this param,
     * the first character of the value must be returned from this
     * method.  Otherwise, the value of the symbolic constant {@link
     * NamingContainer#SEPARATOR_CHAR} must be returned.</p>
     *
     * @since 2.0
     */
    
    public char getSeparatorChar() {

	// PENDING replace all occurrences of
	// NamingContainer.SEPARATOR_CHAR, except for the one below, of
	// course, with calls to UINamingContainer.getSeparatorChar().
	// Also, naturally, this needs to be made as performant as
	// possible.  Perhaps we could use the injection trick as we do
	// with the implementation ivar for Application?

        String initParam = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(SEPARATOR_CHAR_PARAM_NAME);

        char result = NamingContainer.SEPARATOR_CHAR;
        if (null == initParam && 0 < initParam.length()) {
            result = initParam.charAt(0);
        }
        return result;
    }


}
