<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Test CommandLink inside prependId form with button name == submit</title>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
  </head>

  <body>
    <h1>Test CommandLink inside prependId form with button name == submit</h1>

<f:view>

  <h:form id="form" prependId="false">

    <p>This form has prepndId==false and has a button with an
    id=="submit".</p>

    <p><h:commandButton id="submit" value="submit" action="welcome"/></p>

    <p><h:commandLink id="whatever" action="welcome">Command Link</h:commandLink></p>

  </h:form>

</f:view>

    <hr>
    <address><a href="mailto:ed.burns@sun.com">Edward Burns</a></address>
<!-- Created: Fri Jul 21 06:40:37 PDT 2006 -->
<!-- hhmts start -->
Last modified: Fri Jul 21 07:12:08 PDT 2006
<!-- hhmts end -->
  </body>
</html>
