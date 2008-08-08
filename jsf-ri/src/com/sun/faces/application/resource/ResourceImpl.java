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

package com.sun.faces.application.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Collections;

import javax.faces.application.Resource;
import javax.faces.context.FacesContext;

import com.sun.faces.config.WebConfiguration;
import com.sun.faces.util.Util;
import java.util.ArrayList;
import java.util.List;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.application.ResourceHandler;

/**
 * Default implementation of {@link javax.faces.application.Resource}.
 * The ResourceImpl instance itself has the same lifespan as the
 * request, however, the ResourceInfo instances that back this object
 * are cached by the ResourceManager to reduce the time spent scanning
 * for resources.
 */
public class ResourceImpl extends Resource {

    /* HTTP Date format required by the HTTP/1.1 RFC */
    private static final String RFC1123_DATE_PATTERN =
          "EEE, dd MMM yyyy HH:mm:ss zzz";

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    /* The ResourceHandler implementation */
    private ResourceHandlerImpl owner;

    /* The meta data on the resource */
    private ResourceInfo resourceInfo;

    /*
     * Response headers that need to be added by the ResourceManager
     * implementation.
     */
    private Map<String,String> responseHeaders;


    // ------------------------------------------------------------ Constructors


    /**
     * Creates a new instance of ResourceBase
     */
    public ResourceImpl(ResourceHandlerImpl owner,
                        ResourceInfo resourceInfo,
                        String contentType) {

        this.owner = owner;
        this.resourceInfo = resourceInfo;
        super.setResourceName(resourceInfo.getName());
        super.setLibraryName(resourceInfo.getLibraryInfo() != null
                             ? resourceInfo.getLibraryInfo().getName()
                             : null);
        super.setContentType(contentType);

    }


    // --------------------------------------------------- Methods from Resource


    /**
     * @see javax.faces.application.Resource#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        InputStream result = null;
        if (isEvaluateExpressions()) {
            result = getExpressionEvaluatingInputStream(ctx);
        } else {
            result = resourceInfo.getHelper().getInputStream(resourceInfo, ctx);
        }
        return result;
    }
    
    private InputStream getExpressionEvaluatingInputStream(final FacesContext context)
            throws IOException {
        InputStream result = null;
        final InputStream inner = resourceInfo.getHelper().getInputStream(resourceInfo, 
                context);
        result = new InputStream() {

            // Premature optimization is the root of all evil.  Blah blah.
            private List<Integer> buf = new ArrayList<Integer>(1024);
            boolean readingExpression = false;
            boolean failedExpressionTest = false;
            boolean writingExpression = false;

            public boolean isWritingExpression() {
                return writingExpression;
            }

            public void setWritingExpression(boolean writingExpression) {
                this.writingExpression = writingExpression;
            }

            public boolean isFailedExpressionTest() {
                return failedExpressionTest;
            }

            public void setFailedExpressionTest(boolean testingForExpression) {
                this.failedExpressionTest = testingForExpression;
            }

            public List<Integer> getBuf() {
                return buf;
            }

            public void setBuf(List<Integer> buf) {
                this.buf = buf;
            }

            public boolean isReadingExpression() {
                return readingExpression;
            }

            public void setReadingExpression(boolean readingExpression) {
                this.readingExpression = readingExpression;
            }
            
            private int nextRead = -1;
                    
            @Override
            public int read() throws IOException {
                int i = 0;
                char c = 0;
                
                if (isFailedExpressionTest()) {
                    i = nextRead;
                    nextRead = -1;
                    setFailedExpressionTest(false);
                } else if (isWritingExpression()) {
                    if (0 < getBuf().size()) {
                        i = getBuf().remove(0);
                    }
                    else {
                        setWritingExpression(false);
                        i = inner.read();
                    }
                } else {
                    // Read a character.
                    i = inner.read();
                    c = (char) i;
                    // If it *might* be an expression...
                    if (c == '#') {
                        // read another character.
                        i = inner.read();
                        c = (char) i;
                        // If it's '{', assume we have an expression.
                        if (c == '{') {
                            // read it into the buffer, and evaluate it into the
                            // same buffer.
                            readExpressionIntoBufferAndEvaluateIntoBuffer();
                            // set the flag so that we need to return content
                            // from the buffer.
                            setWritingExpression(true);
                            // Make sure to swallow the '{'.
                            i = this.read();
                        } else {
                            // It's not an expression, we need to return '#',
                            i = (int) '#';
                            // then return whatever we just read, on the
                            // *next* read;
                            nextRead = (int) c;
                            setFailedExpressionTest(true);
                        }
                    } 
                }
                
                return i;
            }
            
            private void readExpressionIntoBufferAndEvaluateIntoBuffer() throws IOException {
                assert(isReadingExpression());
                int i = 0;
                char c = 0;
                do {
                    i = inner.read();
                    c = (char) i;
                    if (c == '}') {
                        evaluateExpressionIntoBuffer();
                    } else {
                        getBuf().add(i);
                    }
                } while (c != '}' && i != -1);
            }
            
            /*
             * At this point, we know that getBuf() returns a List<Integer>
             * that contains the bytes of the expression.  
             * Turn it into a String, turn the String into a ValueExpression,
             * evaluate it, store the toString() of it in 
             * expressionResult;
             */
            private void evaluateExpressionIntoBuffer() {
                List<Integer> buf = getBuf();
                char chars[] = new char[buf.size()];
                int i = 0, length = 0;
                for (int cur : buf) {
                    chars[i++] = (char) cur;
                }
                String expressionBody = new String(chars);
                int colon;
                // If this expression contains a ":"
                if (-1 != (colon = expressionBody.indexOf(":"))) {
                    // Make sure it contains only one ":"
                    if (!isPropertyValid(expressionBody)) {
                        // RELEASE_PENDING i18n
                        throw new ELException("Invalid resource format.  Property " +
                                expressionBody + " contains more than one colon (:)");
                    }
                    String[] parts = Util.split(expressionBody, ":");
                    if (null == parts[0] || null == parts[1]) {
                        // RELEASE_PENDING i18n
                        throw new ELException("Invalid resource format.  Property " +
                                expressionBody + " cannot be parsed to extract resource name and library name");
                        
                    }
                    try {
                        int mark = parts[0].indexOf("[") + 2;
                        char quoteMark = parts[0].charAt(mark - 1);
                        parts[0] = parts[0].substring(mark, colon);
                        if (parts[0].equals("this")) {
                            parts[0] = ResourceImpl.this.getLibraryName();
                            mark = parts[1].indexOf("]") - 1;
                            parts[1] = parts[1].substring(0, mark);
                            expressionBody = "resource[" + quoteMark + parts[0]+
                                    ":" + parts[1] + quoteMark + "]";
                        }
                    }
                    catch (Exception e) {
                        // RELEASE_PENDING i18n
                        throw new ELException("Invalid resource format.  Property " +
                                expressionBody + " cannot be parsed");
                        
                    }
                }
                ELContext elContext = context.getELContext();
                ValueExpression ve = context.getApplication().getExpressionFactory().
                        createValueExpression(elContext, "#{" + expressionBody +
                        "}", String.class);
                Object value = ve.getValue(elContext);
                String expressionResult = null;
                if (null != value) {
                    expressionResult = value.toString();
                }
                else {
                    expressionResult = "";
                }
                buf.clear();
                length = expressionResult.length();
                for (i = 0; i < length; i++) {
                    buf.add((int) expressionResult.charAt(i));
                }
            }
            
            
            private boolean isPropertyValid(String property) {
                int idx = property.indexOf(':');
                return (property.indexOf(':', idx + 1) == -1);
            }
            
        };
        
        return result;
    };

    /* PENDING(edburns): really, this should be a public proprty on Resource.
     */
    
    private boolean isEvaluateExpressions() {
        boolean result = false;
        String contentType = null;
        
        result = ((null != (contentType = this.getContentType())) &&
                  (contentType.equals("text/css") ||
                   contentType.equals("text/javascript") ||
                   contentType.equals("application/x-javascript")));
        
        return result;
    }


    /**
     * @see javax.faces.application.Resource#getURL()
     */
    public URL getURL() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        return resourceInfo.getHelper().getURL(resourceInfo, ctx);
    }


    /**
     * <p>
     * Implementation note.  Any values added to getResponseHeaders()
     * will only be visible across multiple calls to this method when
     * servicing a resource request (i.e. {@link ResourceHandler#isResourceRequest(javax.faces.context.FacesContext)}
     * returns <code>true</code>).  If we're not servicing a resource request,
     * an empty Map will be returned and the values added are effectively thrown
     * away.
     * </p>
     * 
     * @see javax.faces.application.Resource#getResponseHeaders()
     */
    public Map<String, String> getResponseHeaders() {

        if (isResourceRequest()) {
            if (responseHeaders == null)
            responseHeaders = new HashMap<String, String>(6, 1.0f);

            long expiresTime = new Date().getTime() +
                               Long.parseLong(owner
                                     .getWebConfig().getOptionValue(
                                     WebConfiguration.WebContextInitParameter.DefaultResourceMaxAge));
            SimpleDateFormat format =
                  new SimpleDateFormat(RFC1123_DATE_PATTERN, Locale.US);
            format.setTimeZone(GMT);
            responseHeaders.put("Expires", format.format(new Date(expiresTime)));

            URL url = getURL();
            InputStream in = null;
            try {
                URLConnection conn = url.openConnection();
                conn.setUseCaches(false);
                conn.connect();
                in = conn.getInputStream();
                long lastModified = conn.getLastModified();
                long contentLength = conn.getContentLength();
                if (lastModified == 0) {
                    lastModified = owner.getCreationTime();
                }
                responseHeaders.put("Last-Modified", format.format(new Date(lastModified)));
                if (lastModified != 0 && contentLength != -1) {
                    responseHeaders.put("ETag", "W/\""
                                    + contentLength
                                    + '-'
                                    + lastModified
                                    + '"');
                }
            } catch (IOException ignored) {
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignored) { }
                }
            }
            return responseHeaders;
        } else {
            return Collections.emptyMap();
        }

    }


    /**
     * @see javax.faces.application.Resource#getRequestPath()
     */
    public String getRequestPath() {

        String uri;
        FacesContext context = FacesContext.getCurrentInstance();
        String facesServletMapping = Util.getFacesMapping(context);
        // If it is extension mapped
        if (Util.isPrefixMapped(facesServletMapping)) {
            uri = facesServletMapping + ResourceHandler.RESOURCE_IDENTIFIER + '/' +
                  getResourceName();
        } else {
            uri = ResourceHandler.RESOURCE_IDENTIFIER + '/' + getResourceName() +
                  facesServletMapping;
        }
        boolean queryStarted = false;
        if (null != getLibraryName()) {
            queryStarted = true;
            uri += "?ln=" + getLibraryName();
        }
        String version = "";
        if (resourceInfo.getLibraryInfo() != null && resourceInfo.getLibraryInfo().getVersion() != null) {
            version += resourceInfo.getLibraryInfo().getVersion().toString();
        }
        if (resourceInfo.getVersion() != null) {
            version += resourceInfo.getVersion().toString();
        }
        if (version.length() > 0) {
            uri += ((queryStarted) ? "&v=" : "?v=") + version;
        }
        String localePrefix = resourceInfo.getLocalePrefix();
        if (localePrefix != null) {
            uri += ((queryStarted) ? "&loc=" : "?loc=") + localePrefix;
        }
        uri = context.getApplication().getViewHandler()
              .getResourceURL(context,
                              uri);

        return uri;

    }


    /**
     * @see javax.faces.application.Resource#userAgentNeedsUpdate(javax.faces.context.FacesContext)
     */
    public boolean userAgentNeedsUpdate(FacesContext context) {

        Map<String,String> requestHeaders =
              context.getExternalContext().getRequestHeaderMap();
        return ((!requestHeaders.containsKey("If-Modified-Since"))
                || (resourceInfo.getHelper()
                      .getLastModified(resourceInfo, context) > owner
                      .getCreationTime()));

    }


    // --------------------------------------------------------- Private Methods


    private boolean isResourceRequest() {

        FacesContext ctx = FacesContext.getCurrentInstance();
        return (ctx.getApplication().getResourceHandler().isResourceRequest(ctx));

    }

}
