/*
 * $Id: RIConstants.java,v 1.8 2002/07/10 17:57:19 jvisvanathan Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces;

import javax.faces.render.RenderKitFactory;

/**
 * This class contains literal strings used throughout the Faces RI.
 */
public class RIConstants {

    public static final String URL_PREFIX = "/faces";
    
    /**
     * <p>Path Info prefix that indicates a form submit.</p>
     */
     public static final String FORM_PREFIX = "/form/";
    
    /**
     * <p>Path Info prefix that indicates hyperlink</p>
     */
    public static final String COMMAND_PREFIX = "/command/";
    
    /**
     * Used to add uniqueness to the names.
    */
    public final static String FACES_PREFIX = "com.sun.faces.";

    public final static String DEFAULT_RENDER_KIT = FACES_PREFIX +
	RenderKitFactory.DEFAULT_RENDER_KIT;
    
    public static final String JSF_RESOURCE_FILENAME = "JSFMessages";

    public final static String DISABLE_RENDERERS = FACES_PREFIX +
	"DisableRenderers";

    /**

    * If the following name=value pair appears in the request query
    * string, the CreateRequestTreePhase will proceed directly to
    * RenderResponsePhase.

    */

    public final static String INITIAL_REQUEST_NAME = "initialRequest";
    public final static String INITIAL_REQUEST_VALUE = "true";
    
    /**
     * The message identifier of the {@link Message} to be created as
     * a result of type conversion error.
     */
    public static final String CONVERSION_ERROR_MESSAGE_ID =
        "com.sun.faces.TYPECONVERSION_ERROR";
    
}
