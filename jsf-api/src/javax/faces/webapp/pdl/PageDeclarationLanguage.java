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

package javax.faces.webapp.pdl;

import java.beans.BeanInfo;
import java.io.IOException;
import javax.faces.application.Resource;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * <p class="changed_added_2_0">The contract that a page declaration
 * language must implement to interact with the JSF runtime. 
 * An implementation
 * of this class must be thread-safe.</p>
 *
 * <div class="changed_added_2_0">
 * 
 * <p>Instances of this class are application scoped and must be
 * obtained from the {@link PageDeclarationLanguageFactory}.</p>
 * 
 * </div>
 * 
 * @since 2.0
 * 
 */
public abstract class PageDeclarationLanguage {

    /**
     * <p class="changed_added_2_0">Return a reference to the component
     * metadata for the composite component represented by the argument
     * <code>componentResource</code>, or <code>null</code> if the
     * metadata cannot be found.  See section JSF.7.6.2 for the
     * specification of the default implementation.</p>
     *
     * @param context The <code>FacesContext</code> for this request.
     * @param componentResource The <code>Resource</code> that represents the component.
     * @since 2.0
     *
     * @throws NullPointerException if any of the arguments are
     * <code>null</code>.

     * @throws javax.faces.FacesException if there is an error in
     * obtaining the metadata
     */
    public abstract BeanInfo getComponentMetadata(FacesContext context, Resource componentResource);


    /**
     * <p class="changed_added_2_0">Take implementation specific action
     * to discover a <code>Resource</code> given the argument
     * <code>componentResource</code>.  See section JSF.7.6.2 for the
     * specification of the default implementation.</p>
     *
     * @param context The <code>FacesContext</code> for this request.
     * @param componentResource The <code>Resource</code> that represents the component.
     * @since 2.0

     * @throws NullPointerException if any of the arguments are
     * <code>null</code>.

     * @throws javax.faces.FacesException if there is an error in
     * obtaining the script component resource
     *
     */
    public abstract Resource getScriptComponentResource(FacesContext context,
            Resource componentResource);
    
    
    /**
     * <p class="changed_added_2_0">Create a <code>UIViewRoot</code>
     * from the PDL contained in the artifact referenced by the argument
     * <code>viewId</code>.  See section JSF.7.6.2 for the specification of
     * the default implementation.</p>
     *
     * @param context the <code>FacesContext</code> for this request.
     * @param viewId the identifier of an artifact that contains the PDL
     * syntax that describes this view.
     *
     * @throws NullPointerException if any of the arguments are
     * <code>null</code>

     * @since 2.0
     */

    public abstract UIViewRoot createView(FacesContext context,
                                 String viewId);
    
    /**
     * <p class="changed_added_2_0">Restore a <code>UIViewRoot</code>
     * from a previously created view.  See section JSF.7.6.2 for the
     * specification of the default implementation.</p>
     *
     * @param context the <code>FacesContext</code> for this request.
     * @param viewId the identifier for a previously rendered view.
     *
     * @throws NullPointerException if any of the arguments are
     * <code>null</code>

     * @since 2.0
     */

    public abstract UIViewRoot restoreView(FacesContext context, String viewId);

    
    /**
     * <p class="changed_added_2_0">Render a view rooted at
     * argument<code>view</code>. See section JSF.7.6.2 for the
     * specification of the default implementation.</p>
     *
     * @param context the <code>FacesContext</code> for this request.
     * @param view the <code>UIViewRoot</code> from an early call to
     * {@link #createView} or {@link #restoreView}.
     *
     * @throws NullPointerException if any of the arguments are
     * <code>null</code>

     * @since 2.0
     */

    public abstract void renderView(FacesContext context,
                                    UIViewRoot view)
    throws IOException;
    

}
