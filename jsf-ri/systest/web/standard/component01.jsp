<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>component01.jsp</title>

    <%@ page import="javax.el.ValueExpression"
          %>
    <%@ page import="javax.faces.FactoryFinder"
          %>
    <%@ page import="javax.faces.application.Application"
          %>
    <%@ page import="javax.faces.application.ApplicationFactory"
          %>
    <%@ page import="javax.faces.component.UIInput"
          %>
    <%@ page import="javax.faces.context.FacesContext"
          %>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <% request.setAttribute("attrName", "attrValue"); %>
</head>

<body>
<f:view>
    <h:inputText id="username" binding="#{usernamecomponent}" size="20"
                 onkeypress="#{requestScope.attrName}"/>
</f:view>
</body>
</html>
<%

    // Acquire the FacesContext instance for this request
    FacesContext facesContext = FacesContext.getCurrentInstance();
    if (facesContext == null) {
        out.println("/component01.jsp FAILED - No FacesContext returned");
        return;
    }

    // Acquire our Application instance
    ApplicationFactory afactory = (ApplicationFactory)
          FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
    Application appl = afactory.getApplication();

    ValueExpression binding = appl.getExpressionFactory().
          createValueExpression(facesContext.getELContext(),
                                "usernamecomponent",
                                Object.class);
    Object result = binding.getValue(facesContext.getELContext());
    if (result == null || !(result instanceof UIInput)) {
        System.out
              .println("/component01.jsp FAILED - Couldn't retrieve component.");
        return;
    }

    UIInput usernamecomponent = (UIInput) result;
    String size = (String) usernamecomponent.getAttributes().get("size");
    if (!(size.equals("20"))) {
        System.out.println(
              "/component01.jsp FAILED - Invalid value for size attribute");
        return;
    }

    String maxlength =
          (String) usernamecomponent.getAttributes().get("maxlength");
    if (!(maxlength.equals("32"))) {
        System.out.println(
              "/component01.jsp FAILED - Invalid value for maxlength attribute");
        return;
    }
%>

