<%--
   Copyright 2003 Sun Microsystems, Inc.  All rights reserved.
   SUN PROPRIETARY/CONFIDENTIAL.  Use is subject license terms.
--%>

<%-- $Id: verbatim_test.jsp,v 1.4 2003/11/10 02:40:32 eburns Exp $ --%>
<html>
  <head>
    <title>Test of the Verbatim Tag</title>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ page import="javax.faces.context.FacesContext"%>
<%

  String textToEscape = "This text<b>must be escaped</b>";
  FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("textToEscape", textToEscape);  
%>


  </head>

<f:view>

  <body>
    <h1>Test of the Verbatim Tag</h1>

<p>

    <f:verbatim>This text must be echoed verbatim <B>INCLUDING</B> any
    <I>MARKUP</I>.  The angle brackets must be un-escaped.
    </f:verbatim>

</p>

<p>

    <f:verbatim escape="false">This text must be echoed verbatim
    <B>INCLUDING</B> any <I>MARKUP</I>.  The angle brackets must be
    un-escaped.
    </f:verbatim>

</p>

<p>

    <f:verbatim escape="true">This text must be echoed verbatim
    <B>INCLUDING</B> any <I>MARKUP</I>.  The angle brackets must be
    escaped.
    </f:verbatim>

    <p><h:output_text value="#{textToEscape}"/></p>

</p>




    <hr>
  </body>

</f:view>

</html>
