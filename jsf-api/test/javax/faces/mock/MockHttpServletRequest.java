/*
 * $Id: MockHttpServletRequest.java,v 1.4 2003/04/29 18:13:05 eburns Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.faces.mock;


import java.io.BufferedReader;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


// Mock Object for HttpServletRequest (Version 2.4)
public class MockHttpServletRequest implements HttpServletRequest {


    public MockHttpServletRequest() {
        super();
    }


    public MockHttpServletRequest(HttpSession session) {
        super();
        setHttpSession(session);
    }


    public MockHttpServletRequest(String contextPath, String servletPath,
                                  String pathInfo, String queryString) {
        super();
        setPathElements(contextPath, servletPath, pathInfo, queryString);
    }



    public MockHttpServletRequest(String contextPath, String servletPath,
                                  String pathInfo, String queryString,
                                  HttpSession session) {
        super();
        setPathElements(contextPath, servletPath, pathInfo, queryString);
        setHttpSession(session);
    }



    protected HashMap attributes = new HashMap();
    protected String contextPath = null;
    protected Locale locale = null;
    protected HashMap parameters = new HashMap();
    protected String pathInfo = null;
    protected Principal principal = null;
    protected String queryString = null;
    protected String servletPath = null;
    protected HttpSession session = null;


    // --------------------------------------------------------- Public Methods


    public void addParameter(String name, String value) {
        String values[] = (String[]) parameters.get(name);
        if (values == null) {
            String results[] = new String[] { value };
            parameters.put(name, results);
            return;
        }
        String results[] = new String[values.length + 1];
        System.arraycopy(values, 0, results, 0, values.length);
        results[values.length] = value;
        parameters.put(name, results);
    }


    public void setHttpSession(HttpSession session) {
        this.session = session;
    }


    public void setLocale(Locale locale) {
        this.locale = locale;
    }


    public void setPathElements(String contextPath, String servletPath,
                                String pathInfo, String queryString) {

        this.contextPath = contextPath;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
        this.queryString = queryString;

    }


    public void setUserPrincipal(Principal principal) {
        this.principal = principal;
    }



    // --------------------------------------------- HttpServletRequest Methods


    public String getAuthType() {
        throw new UnsupportedOperationException();
    }


    public String getContextPath() {
        return (contextPath);
    }


    public Cookie[] getCookies() {
        throw new UnsupportedOperationException();
    }


    public long getDateHeader(String name) {
        throw new UnsupportedOperationException();
    }


    public String getHeader(String name) {
        throw new UnsupportedOperationException();
    }


    public Enumeration getHeaderNames() {
        throw new UnsupportedOperationException();
    }


    public Enumeration getHeaders(String name) {
        throw new UnsupportedOperationException();
    }


    public int getIntHeader(String name) {
        throw new UnsupportedOperationException();
    }


    public String getMethod() {
        throw new UnsupportedOperationException();
    }


    public String getPathInfo() {
        return (pathInfo);
    }


    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }


    public String getQueryString() {
        return (queryString);
    }


    public String getRemoteUser() {
        if (principal != null) {
            return (principal.getName());
        } else {
            return (null);
        }
    }


    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }


    public String getRequestURI() {
        StringBuffer sb = new StringBuffer();
        if (contextPath != null) {
            sb.append(contextPath);
        }
        if (servletPath != null) {
            sb.append(servletPath);
        }
        if (pathInfo != null) {
            sb.append(pathInfo);
        }
        if (sb.length() > 0) {
            return (sb.toString());
        }
        throw new UnsupportedOperationException();
    }


    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException();
    }


    public String getServletPath() {
        return (servletPath);
    }


    public HttpSession getSession() {
        return (getSession(true));
    }


    public HttpSession getSession(boolean create) {
        if (create && (session == null)) {
            throw new UnsupportedOperationException();
        }
        return (session);
    }


    public Principal getUserPrincipal() {
        return (principal);
    }


    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException();
    }


    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }


    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }


    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }


    public boolean isUserInRole(String role) {
        if ((principal != null) && (principal instanceof MockPrincipal)) {
            return (((MockPrincipal) principal).isUserInRole(role));
        } else {
            return (false);
        }
    }


    // ------------------------------------------------- ServletRequest Methods


    public Object getAttribute(String name) {
        return (attributes.get(name));
    }


    public Enumeration getAttributeNames() {
        return (new MockEnumeration(attributes.keySet().iterator()));
    }


    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }


    public int getContentLength() {
        throw new UnsupportedOperationException();
    }


    public String getContentType() {
        throw new UnsupportedOperationException();
    }


    public ServletInputStream getInputStream() {
        throw new UnsupportedOperationException();
    }


    public Locale getLocale() {
        return (locale);
    }

    //
    // Servlet 2.4 methods
    // 

    public int getRemotePort() {
        throw new UnsupportedOperationException();
    }

    public String getLocalName() {
        throw new UnsupportedOperationException();
    }

    public String getLocalAddr() {
        throw new UnsupportedOperationException();
    }

    public int getLocalPort() {
        throw new UnsupportedOperationException();
    }

    public Enumeration getLocales() {
        throw new UnsupportedOperationException();
    }

    public String getParameter(String name) {
        String values[] = (String[]) parameters.get(name);
        if (values != null) {
            return (values[0]);
        } else {
            return (null);
        }
    }


    public Map getParameterMap() {
        return (parameters);
    }


    public Enumeration getParameterNames() {
        return (new MockEnumeration(parameters.keySet().iterator()));
    }


    public String[] getParameterValues(String name) {
        return ((String[]) parameters.get(name));
    }


    public String getProtocol() {
        throw new UnsupportedOperationException();
    }


    public BufferedReader getReader() {
        throw new UnsupportedOperationException();
    }


    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }


    public String getRemoteAddr() {
        throw new UnsupportedOperationException();
    }


    public String getRemoteHost() {
        throw new UnsupportedOperationException();
    }


    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }


    public String getScheme() {
        return ("http");
    }


    public String getServerName() {
        return ("localhost");
    }


    public int getServerPort() {
        return (8080);
    }


    public boolean isSecure() {
        return (false);
    }


    public void removeAttribute(String name) {
        attributes.remove(name);
    }


    public void setAttribute(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }


    public void setCharacterEncoding(String name) {
        throw new UnsupportedOperationException();
    }


}
