/*
 * $Id: ResponseStateManagerImpl.java,v 1.35 2006/05/31 21:13:05 rlubke Exp $
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


package com.sun.faces.renderkit;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.config.WebConfiguration.BooleanWebContextInitParameter;
import com.sun.faces.config.WebConfiguration.WebEnvironmentEntry;
import com.sun.faces.config.WebConfiguration.WebContextInitParameter;
import com.sun.faces.io.Base64InputStream;
import com.sun.faces.io.Base64OutputStreamWriter;
import com.sun.faces.util.Util;


/**
 * <p>A <code>ResonseStateManager</code> implementation
 * for the default HTML render kit.
 */
public class ResponseStateManagerImpl extends ResponseStateManager {

    // Log instance for this class
    private static final Logger LOGGER =
          Util.getLogger(Util.FACES_LOGGER + Util.RENDERKIT_LOGGER);

    private static final String FACES_VIEW_STATE =
          "com.sun.faces.FACES_VIEW_STATE";

    private static final char[] STATE_FIELD_START =
          ("<input type=\"hidden\" name=\""
           + ResponseStateManager.VIEW_STATE_PARAM
           + "\" id=\""
           + ResponseStateManager.VIEW_STATE_PARAM
           + "\" value=\"").toCharArray();

    private static final char[] STATE_FIELD_END =
          "\" />".toCharArray();


    private Boolean compressState;   
    private ByteArrayGuard guard; 
    private int csBuffSize;

    public ResponseStateManagerImpl() {

        super();        
        init();

    }

    
    /** @see {@link ResponseStateManager#getComponentStateToRestore(javax.faces.context.FacesContext)} */
    @Override
    @SuppressWarnings("Deprecation")
    public Object getComponentStateToRestore(FacesContext context) {

        // requestMap is a local variable so we don't need to synchronize        
        return context.getExternalContext().getRequestMap()
              .remove(FACES_VIEW_STATE);

    }


    /** @see {@link ResponseStateManager#isPostback(javax.faces.context.FacesContext)} */
    @Override
    public boolean isPostback(FacesContext context) {

        return context.getExternalContext().getRequestParameterMap().
              containsKey(ResponseStateManager.VIEW_STATE_PARAM);

    }


    /** @see {@link ResponseStateManager#getTreeStructureToRestore(javax.faces.context.FacesContext,String)} */
    @Override
    @SuppressWarnings("Deprecation")
    public Object getTreeStructureToRestore(FacesContext context,
                                            String treeId) {

        StateManager stateManager = Util.getStateManager(context);

        String viewString = getStateParam(context);
       
        if (viewString == null) {
            return null;
        }

        if (stateManager.isSavingStateInClient(context)) {
                    
         
            ObjectInputStream ois = null;           

            try {                           
                InputStream bis;
                if (guard != null) {
                    bis = new CipherInputStream(
                          new Base64InputStream(viewString),
                          guard.getDecryptionCipher());                    
                } else {
                    bis = new Base64InputStream(viewString);
                }
                    
                if (compressState) {                                        
                    ois = new ApplicationObjectInputStream(
                          new GZIPInputStream(bis));
                } else {
                    ois = new ApplicationObjectInputStream(bis);
                }
                Object structure = ois.readObject();
                Object state = ois.readObject();
                              
                ois.close();

                storeStateInRequest(context, state);
                return structure;

            } catch (java.io.OptionalDataException ode) {
                LOGGER.log(Level.SEVERE, ode.getMessage(), ode);
                throw new FacesException(ode);
            } catch (java.lang.ClassNotFoundException cnfe) {
                LOGGER.log(Level.SEVERE, cnfe.getMessage(), cnfe);
                throw new FacesException(cnfe);
            } catch (java.io.IOException iox) {
                LOGGER.log(Level.SEVERE, iox.getMessage(), iox);
                throw new FacesException(iox);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ioe) {
                        // ignore
                    }
                }
            }
        } else {
            return viewString;
        }       
    }


    /** @see {@link ResponseStateManager#writeState(javax.faces.context.FacesContext,javax.faces.application.StateManager.SerializedView)} */
    @Override
    @SuppressWarnings("Deprecation")
    public void writeState(FacesContext context, SerializedView view)
    throws IOException {

        StateManager stateManager = Util.getStateManager(context);
        ResponseWriter writer = context.getResponseWriter();

        writer.write(STATE_FIELD_START);

        if (stateManager.isSavingStateInClient(context)) {
            ObjectOutputStream oos = null;
            try {

                Base64OutputStreamWriter bos =
                      new Base64OutputStreamWriter(csBuffSize,
                                                   writer);
                OutputStream base;
                if (guard != null) {
                    base = new CipherOutputStream(bos,
                                                  guard.getEncryptionCipher());
                } else {
                    base = bos;
                }
                if (compressState) {
                    oos = new ObjectOutputStream(
                          new GZIPOutputStream(base));
                } else {
                    oos = new ObjectOutputStream(base);
                }

                oos.writeObject(view.getStructure());
                oos.writeObject(view.getState());
                oos.flush();
                oos.close();

                // flush everything to the underlying writer
                bos.finish();
                
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Client State: total number of characters" 
                                + " written: " + bos.getTotalCharsWritten());
                }
            } finally {
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException ioe) {
                        // ignore
                    }
                }
                    
            }

        } else {
            writer.write(view.getStructure().toString());
        }

        writer.write(STATE_FIELD_END);

        writeRenderKitIdField(context, writer);

    }


    /**
     * <p>Store the state for this request into a temporary attribute
     * within the same request.</p>
     *
     * @param context the <code>FacesContext</code> of the current request
     * @param state   the view state
     */
    private void storeStateInRequest(FacesContext context, Object state) {

        // store the state object temporarily in request scope
        // until it is processed by getComponentStateToRestore
        // which resets it.
        context.getExternalContext().getRequestMap()
              .put(FACES_VIEW_STATE, state);

    }


    /**
     * <p>Write a hidden field if the default render kit ID is not
     * RenderKitFactory.HTML_BASIC_RENDER_KIT.</p>
     *
     * @param context the <code>FacesContext</code> for the current request
     * @param writer  the target writer
     *
     * @throws IOException if an error occurs
     */
    private void writeRenderKitIdField(FacesContext context,
                                       ResponseWriter writer)
          throws IOException {
        String result = context.getApplication().getDefaultRenderKitId();
        if (result != null &&
            !RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(result)) {
            writer.startElement("input", context.getViewRoot());
            writer.writeAttribute("type", "hidden", "type");
            writer.writeAttribute("name",
                                  ResponseStateManager.RENDER_KIT_ID_PARAM,
                                  "name");
            writer.writeAttribute("value",
                                  result,
                                  "value");
            writer.endElement("input");
        }
    }

    /**
     * <p>Get our view state from this request</p>
     *
     * @param context the <code>FacesContext</code> for the current request
     *
     * @return the view state from this request
     */
    private String getStateParam(FacesContext context) {

        return context.getExternalContext().getRequestParameterMap().get(
              ResponseStateManager.VIEW_STATE_PARAM);
    }


    /**
     * <p>Perform the necessary intialization to make this
     * class work.</p>    
     */
    private void init() {
        
        WebConfiguration webConfig = WebConfiguration.getInstance();
        assert(webConfig != null);
        
        String pass = webConfig.getEnvironmentEntry(
                        WebEnvironmentEntry.ClientStateSavingPassword);
        if (pass != null) {
            guard = new ByteArrayGuard(pass);
        }
        compressState = webConfig.getBooleanContextInitParameter(
                            BooleanWebContextInitParameter.CompressViewState);
        String size = webConfig.getContextInitParameter(
                         WebContextInitParameter.ClientStateWriteBufferSize);
        String defaultSize = 
              WebContextInitParameter.ClientStateWriteBufferSize.getDefaultValue();
        try {
            csBuffSize = Integer.parseInt(size);
            if (csBuffSize % 2 != 0) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                               "jsf.renderkit.resstatemgr.clientbuf_div_two",
                               new Object[] {
                                   WebContextInitParameter.ClientStateWriteBufferSize.getQualifiedName(),
                                   size,
                                   defaultSize});
                }          
                csBuffSize = Integer.parseInt(defaultSize);
            } else {
                csBuffSize /= 2;
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine("Using client state buffer size of " 
                                + csBuffSize);
                }
            }
        } catch (NumberFormatException nfe) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                               "jsf.renderkit.resstatemgr.clientbuf_not_integer",
                               new Object[] {
                                   WebContextInitParameter.ClientStateWriteBufferSize.getQualifiedName(),
                                   size,
                                   defaultSize});
                }   
            csBuffSize = Integer.parseInt(defaultSize);
        }
    }

   
} // end of class ResponseStateManagerImpl
