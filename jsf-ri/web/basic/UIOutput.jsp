<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>UIOutput</title>
  </head>

  <body>

    <h1>UIOutput</h1>

    <h3>$Id: UIOutput.jsp,v 1.6 2002/11/12 22:51:41 jhorwat Exp $</h3>

    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
    <%@ taglib uri="http://java.sun.com/jsf/html_basic" prefix="h" %>

     <fmt:setBundle basename="basic.Resources" scope="session" 
                    var="basicBundle"/>

     <jsp:useBean id="LoginBean" class="basic.LoginBean" scope="session" />

     <f:use_faces>  

       <p>Form is rendered after this.</p>
     
       <h:form id="standardRenderKitForm" 
                   formName="standardRenderKitForm">

         <h:command_button id="standardRenderKitSubmit" 
             commandName="standardRenderKitSubmit"
             key="standardRenderKitSubmitLabel"
             bundle="${basicBundle}"> 
         </h:command_button>

         <table width="100%" border="1" cellpadding="3" cellspacing="3">

<!-- Each included page should have table rows for the appropriate widget. -->

           <%@ include file="table_header.jsp" %>

           <%@ include file="output_date_row.jsp" %>

           <%@ include file="output_datetime_row.jsp" %>

           <%@ include file="output_message_row.jsp" %>

           <%@ include file="output_number_row.jsp" %>

           <%@ include file="output_text_row.jsp" %>

           <%@ include file="output_time_row.jsp" %>

<tr>
<td><b>Errors:</b>
</td>

<td>
		<h:output_errors id="globalErrors" />
</td>

</tr>

         </table>

         <h:command_button id="standardRenderKitSubmit" 
             commandName="standardRenderKitSubmit"
             key="standardRenderKitSubmitLabel"
             bundle="${basicBundle}"> 
         </h:command_button>

       </h:form>

     </f:use_faces>   


  </body>
</html>
