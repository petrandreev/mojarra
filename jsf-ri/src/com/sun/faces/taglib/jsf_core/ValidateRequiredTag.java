/*
 * $Id: ValidateRequiredTag.java,v 1.14 2005/08/22 22:10:27 ofung Exp $
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

// ValidateRequiredTag.java

package com.sun.faces.taglib.jsf_core;

import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.el.ValueExpression;
import javax.el.ExpressionFactory;


/**
 * <B>ValidateRequiredTag</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 *
 * @version $Id: ValidateRequiredTag.java,v 1.14 2005/08/22 22:10:27 ofung Exp $
 */

public class ValidateRequiredTag extends ValidatorTag {

    private static final long serialVersionUID = -4925861676709072353L;
    private static ValueExpression VALIDATOR_ID_EXPR = null;

//
// Constructors and Initializers    
//

    public ValidateRequiredTag() {
        super();
        if (VALIDATOR_ID_EXPR == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            ExpressionFactory factory = context.getApplication().
                    getExpressionFactory();
            VALIDATOR_ID_EXPR =
                factory.createValueExpression(context.getELContext(), 
                    "javax.faces.Required", String.class);
        }
    }

//
// Class methods
//

//
// General Methods
//

    public int doStartTag() throws JspException {
        super.setValidatorId(VALIDATOR_ID_EXPR);
        return super.doStartTag();
    }


// 
// Methods from ValidatorTag
// 

} // end of class ValidateRequiredTag
