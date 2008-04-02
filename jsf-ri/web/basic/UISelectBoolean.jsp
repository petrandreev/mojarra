<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>UISelectBoolean</title>
  </head>

  <body>

    <h1>UISelectBoolean</h1>

    <h3>$Id: UISelectBoolean.jsp,v 1.3 2002/09/20 00:59:54 eburns Exp $</h3>

    <%@ taglib uri="http://java.sun.com/jsf/core/" prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
    <%@ taglib uri="http://java.sun.com/j2ee/html_basic/" prefix="faces" %>

     <fmt:setBundle basename="basic.Resources" scope="session" 
                    var="basicBundle"/>

     <jsp:useBean id="LoginBean" class="basic.LoginBean" scope="session" />
     <f:usefaces>  

       <p>Form is rendered after this.</p>
     
       <faces:form id="standardRenderKitForm" 
                   formName="standardRenderKitForm">

         <faces:command_button id="standardRenderKitSubmit" 
                      commandName="standardRenderKitSubmit">
             <faces:output_text id="stdRenderKitLabel" key="standardRenderKitSubmitLabel"
                      bundle="basicBundle" />
         </faces:command_button>

         <table width="100%" border="1" cellpadding="3" cellspacing="3">

<!-- Each included page should have table rows for the appropriate widget. -->

           <%@ include file="table_header.jsp" %>

           <%@ include file="selectboolean_checkbox_row.jsp" %>

         </table>

         <faces:command_button id="standardRenderKitSubmit" 
                      commandName="standardRenderKitSubmit">
             <faces:output_text id="stdRenderKitLabel" key="standardRenderKitSubmitLabel"
                      bundle="basicBundle" />
         </faces:command_button>

       </faces:form>

     </f:usefaces>   


  </body>
</html>
