<%@ page contentType="text/html"
%><%@ page import="java.util.Locale"
%><%@ page import="javax.faces.FactoryFinder"
%><%@ page import="javax.faces.application.Application"
%><%@ page import="javax.faces.application.ApplicationFactory"
%><%@ page import="javax.faces.application.FacesMessage"
%><%@ page import="javax.faces.context.FacesContext"
%><%@ page import="com.sun.faces.util.MessageFactory"
%><%@ page import="javax.faces.component.UIViewRoot, javax.faces.render.RenderKitFactory"
%><%

    // Initialize list of message ids
    String list[] = {
          "Custom1A",
          "Custom1B",
          "Custom1C",
    };

    // Acquire the FacesContext instance for this request
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ApplicationFactory afactory = (ApplicationFactory)
          FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
    Application appl = afactory.getApplication();
    if (appl == null) {
        out.println("/message03.jsp FAILED - No Application returned");
        return;
    }
    if (facesContext == null) {
        out.println("/message03.jsp FAILED - No FacesContext returned");
        return;
    }
    UIViewRoot root = (UIViewRoot)
          appl.createComponent(UIViewRoot.COMPONENT_TYPE);
    root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
    facesContext.setViewRoot(root);

    FacesMessage message = null;

    // Check message identifiers that should be present (en_US)
    facesContext.getViewRoot().setLocale(new Locale("en", "US"));
    for (int i = 0; i < list.length; i++) {
        message = MessageFactory.getMessage(facesContext, list[i]);
        if (message == null) {
            out.println("/message03.jsp FAILED - Missing en_US message '" +
                        list[i] + "'");
            return;
        }
    }

    // Check specific message characteristics (en_US)
    message = MessageFactory.getMessage(facesContext, "Custom1B");
    if (!"This Is Custom1B Detail (en)".equals(message.getDetail())) {
        out.println("/message03.jsp FAILED - Bad en_US detail '" +
                    message.getDetail() + "'");
        return;
    }
    if (!"This Is Custom1B Summary (en)".equals(message.getSummary())) {
        out.println("/message03.jsp FAILED - Bad en_US summary '" +
                    message.getSummary() + "'");
        return;
    }

    // Check message identifiers that should be present (fr_FR)
    facesContext.getViewRoot().setLocale(new Locale("fr", "FR"));
    for (int i = 0; i < list.length; i++) {
        message = MessageFactory.getMessage(facesContext, list[i]);
        if (message == null) {
            out.println("/message03.jsp FAILED - Missing fr_FR message '" +
                        list[i] + "'");
            return;
        }
    }

    // Check specific message characteristics (fr_FR)
    message = MessageFactory.getMessage(facesContext, "Custom1B");
    if (!"This Is Custom1B Detail (fr)".equals(message.getDetail())) {
        out.println("/message03.jsp FAILED - Bad fr_FR detail '" +
                    message.getDetail() + "'");
        return;
    }
    if (!"This Is Custom1B Summary (fr)".equals(message.getSummary())) {
        out.println("/message03.jsp FAILED - Bad fr_FR summary '" +
                    message.getSummary() + "'");
        return;
    }

    // All tests passed
    out.println("/message03.jsp PASSED");

%>
