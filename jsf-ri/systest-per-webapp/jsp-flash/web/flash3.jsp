<%--

    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

    Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.

    The contents of this file are subject to the terms of either the GNU
    General Public License Version 2 only ("GPL") or the Common Development
    and Distribution License("CDDL") (collectively, the "License").  You
    may not use this file except in compliance with the License.  You can
    obtain a copy of the License at
    https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
    or packager/legal/LICENSE.txt.  See the License for the specific
    language governing permissions and limitations under the License.

    When distributing the software, include this License Header Notice in each
    file and include the License file at packager/legal/LICENSE.txt.

    GPL Classpath Exception:
    Oracle designates this particular file as subject to the "Classpath"
    exception as provided by Oracle in the GPL Version 2 section of the License
    file that accompanied this code.

    Modifications:
    If applicable, add the following below the License Header, with the fields
    enclosed by brackets [] replaced by your own identifying information:
    "Portions Copyright [year] [name of copyright owner]"

    Contributor(s):
    If you wish your version of this file to be governed by only the CDDL or
    only the GPL Version 2, indicate your decision by adding "[Contributor]
    elects to include this software in this distribution under the [CDDL or GPL
    Version 2] license."  If you don't indicate a single choice of license, a
    recipient has the option to distribute your version of this file under
    either the CDDL, the GPL Version 2 or to extend the choice of license to
    its licensees as provided above.  However, if you add GPL Version 2 code
    and therefore, elected the GPL Version 2 license, then the option applies
    only if the new code is made subject to such option by the copyright
    holder.

--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>RoR Flash Test Page 3</title>
    <%@ taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
    <%@ taglib uri="http://java.sun.com/jsf/html"  prefix="h" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

  </head>

  <body>
    <h1>RoR Flash Test Page 3</h1>

<f:view>

  <p>If you have something in flash.now that you later, during the same
  request, decide you want to promote to stick around for the next
  request, use flash.keep.</p>

  <h:form prependId="false" id="form1">

  <h:panelGrid columns="2" border="1">

    Value of the previous request's foo

    <h:outputText value="#{flash.foo}" />

    Value of the this request's bar.  Should be null.

    <h:outputText value="#{flash.bar}" />

    Put <code>banzai</code> in the flash.now under key
    <code>buckaroo</code>.

    <c:set target="${flash.now}" property="buckaroo" value="banzai" />

    <f:verbatim>
      &lt;c:set target="\${flash.now}" property="buckaroo" value="banzai" /&gt;
    </f:verbatim>

    Value of <code>\#{flash.now.buckaroo}</code>, should be
    <code>banzai</code>.

    <h:outputText id="flash3NowValueId" value="#{flash.now.buckaroo}" />

    Promote buckaroo to stick around for the next request.

    <c:set target="${flash.keep}" property="buckaroo" 
           value="${flash.now.buckaroo}" />

    <f:verbatim>
      &lt;c:set target="\${flash.keep}" property="buckaroo" 
                     value="\${flash.now.buckaroo}" /&gt;
    </f:verbatim>

    <h:commandButton id="reload" value="reload" />

    <h:commandButton id="back" value="back" action="back" />

    &nbsp;

    <h:commandButton id="next" value="next" action="next" />

   </h:panelGrid>

  </h:form>

</f:view>

  </body>
</html>
