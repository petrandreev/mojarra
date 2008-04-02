<html>
<title>Validator Test Page</title>
<head>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
</head>
<body>

<h1>TLV c:iterator with JSF id</h1>
This page should succeed.
<br>
<br>

<f:use_faces>

  <c:forEach var="i" begin="0" end="3" varStatus="status">
    Array[<c:out value="${i}"/>]: 
    <h:output_text id="has_id" value="Has ID" key="has_id"/><br>
  </c:forEach>

</f:use_faces>

</body>
</head>
</html>
