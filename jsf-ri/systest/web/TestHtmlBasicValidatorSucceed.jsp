<!--
 Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
-->

<html>
<title>Validator Test Page</title>
<head>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
</head>
<body>

<h1>TLV command_button, no lable or key</h1>
This page should Succeed.
<br>
<br>

<f:view>

  <h:command_button value="hello"/>

  <h:command_button key="hello"/>

</f:view>

</body>
</head>
</html>
