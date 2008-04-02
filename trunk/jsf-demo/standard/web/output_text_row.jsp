<!--
 The contents of this file are subject to the terms
 of the Common Development and Distribution License
 (the License). You may not use this file except in
 compliance with the License.
 
 You can obtain a copy of the License at
 https://javaserverfaces.dev.java.net/CDDL.html or
 legal/CDDLv1.0.txt. 
 See the License for the specific language governing
 permission and limitations under the License.
 
 When distributing Covered Code, include this CDDL
 Header Notice in each file and include the License file
 at legal/CDDLv1.0.txt.    
 If applicable, add the following below the CDDL Header,
 with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"
 
 [Name of File] [ver.__] [Date]
 
 Copyright 2005 Sun Microsystems Inc. All Rights Reserved
-->

           <tr>

             <td>

               <h:outputText id="outputText1Label"
                     value="outputText"/>

             </td>


             <td>

               <h:outputText id="outputText1" 
                       value="#{LoginBean.userName}"/>


             </td>

            </tr>

           <tr>

             <td>

               <h:outputText id="outputText0Label" 
                     value="outputText with outputClass"/>

             </td>


             <td>

               <h:outputText id="outputText0" 
                       styleClass="outputText0"
                       value="#{LoginBean.userName}"/>


             </td>

            </tr>


           <tr>

             <td>

               <h:outputText id="outputText2Label" 
                     value="outputText from bundle"/>

             </td>


             <td>

               <h:outputText id="outputText2"
                                  value="#{standardBundle.linkLabel}"/>


             </td>

	      <td>

		<h:message id="outputText2Errors" 
			  for="outputText2" />

	      </td>

            </tr>

