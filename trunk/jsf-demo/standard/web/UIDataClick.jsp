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

<%@ taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html"  prefix="h" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<f:view>
<html>
<head>
  <title>UIDataClick</title>
  <link rel="stylesheet" type="text/css"
       href='<%= request.getContextPath() + "/stylesheet.css" %>'>
</head>
<body>

  <h1>UIDataClick</h1>

  <h:form id="standardRenderKitForm">

    <table>
<tr>

<td>AccountId
</td>

<td>    <h:outputText value="#{customer.accountId}" /></td>

</tr>

<tr>
<td>Name
</td>
<td>    <h:outputText value="#{customer.name}" /></td>

</tr>

<tr>
<td>Symbol
</td>
<td>    <h:outputText value="#{customer.symbol}" /></td>

</tr>

<tr>
<td>Total Sales
</td>
<td>    <h:outputText value="#{customer.totalSales}" /></td>

</tr>


  </h:form>

</f:view>
