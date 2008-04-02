<!--
 The contents of this file are subject to the terms
 of the Common Development and Distribution License
 (the License). You may not use this file except in
 compliance with the License.
 
 You can obtain a copy of the License at
 https://javaserverfaces.dev.java.net/CDDL.html or
 legal/CDDLv1.0.txt. 
 See the License for the specific language governing
 permission and limitations under the License.
 
 When distributing Covered Code, include this CDDL
 Header Notice in each file and include the License file
 at legal/CDDLv1.0.txt.    
 If applicable, add the following below the CDDL Header,
 with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"
 
 [Name of File] [ver.__] [Date]
 
 Copyright 2005 Sun Microsystems Inc. All Rights Reserved
-->

<%@ page contentType="text/html" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="s" uri="/WEB-INF/taglib.tld" %>

<f:view>
<html>
<head>
<title>jstl-if-02</title>
</head>
<body>
<h:outputText value="[First]"/>
<c:if test="${param.component}">
  <s:facets id="comp" value="Second">
    <c:if test="${param.header}">
      <f:facet name="header">
        <h:outputText id="head" value="Header"/>
      </f:facet>
    </c:if>
    <c:if test="${param.footer}">
      <f:facet name="footer">
        <h:outputText id="foot" value="Footer"/>
      </f:facet>
    </c:if>
  </s:facets>
</c:if>
<h:outputText value="[Third]"/>
</body>
</html>
</f:view>
