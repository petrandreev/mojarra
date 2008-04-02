<!--
 Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
-->

<HTML>
    <HEAD> <TITLE> JSF Basic Components Test Page </TITLE> </HEAD>

    <%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

    <BODY>
        <H3> JSF Basic Components Test Page </H3>

       <f:view>
       <h:form id="basicForm">

  <TABLE BORDER="1">


      <TR>

	<TD>

	      <h:textentry_input id="userName" text="Default_username" />

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:textentry_secret id="password" text="Default_password" />

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:commandButton id="login" value="Login" 
				    commandName="login"/>

	</TD>

      </TR>


      <TR>

	<TD>

	      <h:commandButton id="imageButton" image="duke.gif"
				    commandName="login"/>

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:commandLink id="link" href="hello.html"
				       value="link text"/>

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:commandLink id="imageLink" href="hello.html"
				       image="duke.gif"/>

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:outputText id="userLabel" text="Output Text" />

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:selectmanyCheckbox id="validUser" label="Valid User"
					    selected="true" />

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:selectoneListbox id="appleQuantity">

		<h:selectitem  value="0" label="0"/>
		<h:selectitem  value="1" label="1"/>
		<h:selectitem  value="2" label="2"/>
		<h:selectitem  value="3" label="3"/>
		<h:selectitem  value="4" label="4" selected="true"/>
		<h:selectitem  value="5" label="5"/>
		<h:selectitem  value="6" label="6"/>
		<h:selectitem  value="7" label="7"/>
		<h:selectitem  value="8" label="8"/>
		<h:selectitem  value="9" label="9"/>

	      </h:selectoneListbox>

              Option List

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:selectoneRadio id="shipType" align="horizontal">

		<h:selectitem value="nextDay" label="Next Day" />
		<h:selectitem value="nextWeek" label="Next Week" 
                                  selected="true" />
		<h:selectitem value="nextMonth" label="Next Month" />

              </h:selectoneRadio>

	</TD>

      </TR>

      <TR>

	<TD>
		<h:selectoneRadio id="verticalRadio" 
                                            align="vertical" border="1" >

  		<h:selectitem value="nextDay" label="Next Day" 
                                  selected="true" />
		<h:selectitem value="nextWeek" label="Next Week"  />
		<h:selectitem value="nextMonth" label="Next Month" />

                </h:selectoneRadio>

	</TD>

      </TR>

      <TR>

	<TD>

	      <h:textentry_textarea id="address" text="Hi There" 
                                        rows="10" cols="10"/>

	</TD>

      </TR>

  <TABLE>

</h:form>
</f:view>

    </BODY>
</HTML>
