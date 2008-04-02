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

<%@ page import="java.util.Date" %>
<%@ taglib uri="http://java.sun.com/jsf/core"   prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html"   prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/demo/components" prefix="d" %>

<%

    // Construct a preconfigured Date in session scope
    Date date = (Date)
      pageContext.getAttribute("date", PageContext.SESSION_SCOPE);
    if (date == null) {
      date = new Date();
      pageContext.setAttribute("date", date,
                               PageContext.SESSION_SCOPE);
    }

%>


<f:view>
<html>
<head>
  <title>Demonstration Components - Tabbed Panes</title>
</head>
<body bgcolor="white">

<h:form>
     <d:stylesheet path="/stylesheet.css"/>
Powered by Faces components:

<d:pane_tabbed id="tabcontrol"
        paneClass="tabbed-pane"
     contentClass="tabbed-content"
    selectedClass="tabbed-selected"
  unselectedClass="tabbed-unselected">

  <d:pane_tab id="first">

    <f:facet name="label">
      <d:pane_tablabel label="T a b 1" commandName="first" />
    </f:facet>

    <h:panel_group>
      <h:output_text value="This is the first pane with the date set to: "/>
      <h:output_text value="#{sessionScope.date}">
          <f:convert_datetime dateStyle="medium"/>
      </h:output_text>
    </h:panel_group>

  </d:pane_tab>

  <d:pane_tab id="second" selected="true">

    <f:facet name="label">
      <d:pane_tablabel image="images/duke.gif" commandName="second"/>
    </f:facet>

    <h:panel_group>
      <h:output_text value="Hi folks!  My name is 'Duke'.  Here's a sample of some of the components you can build:"/>
    </h:panel_group>
    <h:panel_group>
      <h:command_button value="button"/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
      <h:selectboolean_checkbox checked="true" alt="checkbox"/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
      <h:selectone_radio layout="PAGE_DIRECTION" border="1" value="nextMonth">
        <f:selectitem itemValue="nextDay" itemLabel="Next Day"/>
        <f:selectitem itemValue="nextWeek" itemLabel="Next Week"  />
        <f:selectitem itemValue="nextMonth" itemLabel="Next Month" />
      </h:selectone_radio>
      <h:selectone_listbox id="appleQuantity" title="Select Quantity"
        tabindex="20" value="4" >
        <f:selectitem  disabled="true" itemValue="0" itemLabel="0"/>
        <f:selectitem  itemValue="1" itemLabel="One" />
        <f:selectitem  itemValue="2" itemLabel="Two" />
        <f:selectitem  itemValue="3" itemLabel="Three" />
        <f:selectitem  itemValue="4" itemLabel="Four" />
      </h:selectone_listbox>
    </h:panel_group>

  </d:pane_tab>

  <d:pane_tab id="third">

    <f:facet name="label">
      <d:pane_tablabel label="T a b 3" commandName="third"/>
    </f:facet>

 
    <jsp:include page="tabbedpanes3.jsp"/>

  </d:pane_tab>

</d:pane_tabbed>

<hr>
</h:form>
<a href='<%= request.getContextPath() + "/index.jsp" %>'>Back</a> to home page.

<h1>How to Use this Component</h1>

<p>This component produces a tabbed pane user interface.</p>

<h2>JSP Attributes</h2>

<p>This component allows the user to define CSS classes via JSP attributes that are output in the rendered markup.  This makes it possible to produce highly customizable output.  You can compare the rendered source of this page, using the "View Source" feature of your browser, with <a href="ShowSource.jsp?filename=/tabbedpanes.jsp">the JSP source</A> for this page.</p>

<table border="1">

<tr>
<th>JSP Attribute Name</th>
<th>What it Does</th>
</tr>

<tr>

<td><code>paneClass</code></td>

<td>A style sheet class which controls the display attributes of the outer border and tabs of the control.</td>

</tr>

<tr>

<td><code>contentClass</code></td>

<td>A style sheet class which controls the display attributes of the selected child pane contents.</td>

</tr>

<tr>

<td><code>selectedClass</code></td>

<td>A style sheet class which controls the display attributes of the select tab label.  This is used to distinguish the selected tab from the other unselected tabs.</td>

</tr>

<tr>

<td><code>unselectedClass</code></td>

<td>A style sheet class which controls the display attributes of an unselected tab label.  This is used to distinguish an unselected tab from a selected tab.</td>

</tr>

</table>

<h2>Tab Controls</h2>

<p><p>The pane control consists of multiple <code>pane_tab</code> tags, and each one corresponds to the individual tabbed panes of the control.  You can optionally indicate that a tab is initially selected with the <code>selected</code> attribute of this tag.  You must specify a unique <code>id</code> attribute for each <code>pane_tab</code> tag.

<h2>Facets</h2>

<p>Each <code>pane_tab</code> tag contains the label for the tabbed pane, as well as the content. You can define Facets for the tab labels for each of the panes.</p>

<table border="1">

<tr>
<th>Facet Name</th>
<th>What it Does</th>
</tr>

<tr>

<td><code>label</code>
</td>

<td>This should be a <code>pane_tablabel</code> tag which has either a <code>label</code> or <code>image</code> attribute and a <code>commandName</code> attribute.  This element is rendered as a button, so <code>commandName</code> is required. The <code>image</code> attribute references an image that will appear on the face of the button.  The <code>label</code> attribute is the label for the button.  This facet should be nested within a <code>pane_tab</code> tag.</td>

</tr>

</table>

<hr>

<a href='<%= request.getContextPath() + "/index.jsp" %>'>Back</a> to home page.

</body>
</html>

</f:view>
