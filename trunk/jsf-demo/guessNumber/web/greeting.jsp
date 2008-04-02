<!--
 Copyright 2002, 2003 Sun Microsystems, Inc. All Rights Reserved.
 
 Redistribution and use in source and binary forms, with or
 without modification, are permitted provided that the following
 conditions are met:
 
 - Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 
 - Redistribution in binary form must reproduce the above
   copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials
   provided with the distribution.
    
 Neither the name of Sun Microsystems, Inc. or the names of
 contributors may be used to endorse or promote products derived
 from this software without specific prior written permission.
  
 This software is provided "AS IS," without a warranty of any
 kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
  
 You acknowledge that this software is not designed, licensed or
 intended for use in the design, construction, operation or
 maintenance of any nuclear facility.
-->

<HTML>
    <HEAD> <title>Hello</title> </HEAD>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <body bgcolor="white">
    <h2>Hi. My name is Duke.  I'm thinking of a number from 0 to 10.
    Can you guess it?</h2>
    <jsp:useBean id="UserNumberBean" class="guessNumber.UserNumberBean" scope="session" />
    <f:view>
    <h:form id="helloForm" formName="helloForm" >
        <h:graphic_image id="waveImg" url="/wave.med.gif" />
  	<h:input_text id="userNo" valueRef="UserNumberBean.userNumber">                
	        <f:validate_longrange minimum="0" maximum="10" />
         </h:input_text> 
	 <h:command_button id="submit" action="success" value="Submit" />
         <p>
	 <h:output_errors id="errors1" for="userNo"/>

<hr>
<p>
       <h:selectmany_checkboxlist valueRef="UserNumberBean.status">
         <h:selectitem itemValue="1" itemLabel="Open" />
         <h:selectitem itemValue="2" itemLabel="Submitted" />
         <h:selectitem itemValue="3" itemLabel="Accepted" />
         <h:selectitem itemValue="4" itemLabel="Rejected" />
       </h:selectmany_checkboxlist>
</p>
<hr>

    </h:form>
    </f:view>
</HTML>  
