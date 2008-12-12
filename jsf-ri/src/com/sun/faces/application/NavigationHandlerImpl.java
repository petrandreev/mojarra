/*
 * $Id: NavigationHandlerImpl.java,v 1.58 2008/01/31 18:36:00 rlubke Exp $
 */

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.faces.application;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.NavigationCase;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.faces.util.MessageUtils;
import com.sun.faces.util.Util;
import com.sun.faces.util.FacesLogger;
import javax.faces.application.ConfigurableNavigationHandler;

/**
 * <p><strong>NavigationHandlerImpl</strong> is the class that implements
 * default navigation handling. Refer to section 7.4.2 of the specification for
 * more details.
 * PENDING: Make independent of ApplicationAssociate. 
 */

public class NavigationHandlerImpl extends ConfigurableNavigationHandler {

    //
    // Protected Constants
    //

    // Log instance for this class
    private static final Logger logger = FacesLogger.APPLICATION.getLogger();

    //
    // Class Variables
    //

    // Instance Variables

    /**
     * <code>Map</code> containing configured navigation cases.
     */
    private Map<String, List<NavigationCase>> caseListMap;

    /**
     * <code>Set</code> containing wildcard navigation cases.
     */
    private Set<String> wildCardSet;

    /**
     * Flag indicating navigation cases properly consumed and available.
     */
    private boolean navigationConfigured;

    /**
     * This constructor uses the current <code>Application</code>
     * instance to obtain the navigation mappings used to make
     * navigational decisions.
     */
    public NavigationHandlerImpl() {
        super();
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Created NavigationHandler instance ");
        }
        // if the user is using the decorator pattern, this would cause
        // our ApplicationAssociate to be created, if it isn't already
        // created.
        ApplicationFactory aFactory = (ApplicationFactory)
              FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        aFactory.getApplication();
        ApplicationAssociate associate = ApplicationAssociate.getInstance(
              FacesContext.getCurrentInstance().getExternalContext());
        if (associate != null) {
            caseListMap = associate.getNavigationCaseListMappings();
            wildCardSet = associate.getNavigationWildCardList();
            navigationConfigured = (wildCardSet != null &&
                                    caseListMap != null);
        }
    }


    NavigationHandlerImpl(ApplicationAssociate associate) {
        if (associate == null) {
            throw new NullPointerException();
        } else {
            caseListMap = associate.getNavigationCaseListMappings();
            wildCardSet = associate.getNavigationWildCardList();
            navigationConfigured = (wildCardSet != null &&
                                    caseListMap != null);
        }
    }

    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        NavigationCase result = null;
        CaseStruct caseStruct = getViewId(context, fromAction, outcome);
        if (null != caseStruct) {
            result = caseStruct.navCase;
        }
        
        return result;
    }

    @Override
    public Map<String, List<NavigationCase>> getNavigationCases() {
        return caseListMap;
    }
    

    /**
     * Determine the next view based on the current view
     * (<code>from-view-id</code> stored in <code>FacesContext</code>),
     * <code>fromAction</code> and <code>outcome</code>.
     *
     * @param context    The <code>FacesContext</code>
     * @param fromAction the action reference string
     * @param outcome    the outcome string
     */
    public void handleNavigation(FacesContext context, String fromAction,
                                 String outcome) {
        if (context == null) {
            String message = MessageUtils.getExceptionMessageString
                (MessageUtils.NULL_PARAMETERS_ERROR_MESSAGE_ID, "context");
            throw new NullPointerException(message);
        }
        if (outcome == null) {
           if (logger.isLoggable(Level.FINE)) {
               logger.fine("No navigation rule found for null outcome "
                           + "and viewId " +
                           context.getViewRoot().getViewId() +
                           " Explicitly remain on the current view ");
            }
            return; // Explicitly remain on the current view
        }
        CaseStruct caseStruct = getViewId(context, fromAction, outcome);
        ExternalContext extContext = context.getExternalContext();
        if (caseStruct != null) {
            ViewHandler viewHandler = Util.getViewHandler(context);
            assert (null != viewHandler);

            if (caseStruct.navCase.isRedirect()) {
                // perform a 302 redirect.
                String newPath =
                    viewHandler.getActionURL(context, caseStruct.viewId);
                try {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Redirecting to path " + newPath
                                    + " for outcome " + outcome +
                                    "and viewId " + caseStruct.viewId);
                    }
                    // encode the redirect to ensure session state
                    // is maintained
                    extContext.redirect(extContext.encodeActionURL(newPath));
                } catch (java.io.IOException ioe) {
                    if (logger.isLoggable(Level.SEVERE)) {
                        logger.log(Level.SEVERE,"jsf.redirect_failed_error",
                                   newPath);
                    }
                    throw new FacesException(ioe.getMessage(), ioe);
                }
                context.responseComplete();
               if (logger.isLoggable(Level.FINE)) {
                   logger.fine("Response complete for " + caseStruct.viewId);
               }
            } else {
                UIViewRoot newRoot = viewHandler.createView(context,
                                                            caseStruct.viewId);
                context.setViewRoot(newRoot);
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Set new view in FacesContext for " +
                                caseStruct.viewId);
                }
            }
        }
    }


    /**
     * This method uses helper methods to determine the new <code>view</code> identifier.
     * Refer to section 7.4.2 of the specification for more details.
     *
     * @param context    The Faces Context
     * @param fromAction The action reference string
     * @param outcome    The outcome string
     * @return The <code>view</code> identifier.
     */
    private CaseStruct getViewId(FacesContext context, String fromAction,
                                 String outcome) {
        
        UIViewRoot root = context.getViewRoot();
        String viewId = (root != null ? root.getViewId() : null);
        
        // if viewId is not null, use its value to find
        // a navigation match, otherwise look for a match
        // based soley on the fromAction and outcome
        CaseStruct caseStruct = null;
        if (viewId != null) {
            caseStruct = findExactMatch(viewId, fromAction, outcome);

            if (caseStruct == null) {
                caseStruct = findWildCardMatch(viewId, fromAction, outcome);
            }
        }

        if (caseStruct == null) {
            caseStruct = findDefaultMatch(fromAction, outcome);
        }

        if (caseStruct == null && logger.isLoggable(Level.WARNING)) {
            if (fromAction == null) {
                logger.log(Level.FINE,
                           "jsf.navigation.no_matching_outcome",
                           new Object[] {viewId, outcome});
            } else {
                logger.log(Level.FINE,
                           "jsf.navigation.no_matching_outcome_action",
                           new Object[] {viewId, outcome, fromAction});
            }
        }
        return caseStruct;
    }


    /**
     * This method finds the List of cases for the current <code>view</code> identifier.
     * After the cases are found, the <code>from-action</code> and <code>from-outcome</code>
     * values are evaluated to determine the new <code>view</code> identifier.
     * Refer to section 7.4.2 of the specification for more details.
     *
     * @param viewId     The current <code>view</code> identifier.
     * @param fromAction The action reference string.
     * @param outcome    The outcome string.
     * @return The <code>view</code> identifier.
     */

    private CaseStruct findExactMatch(String viewId,
                                      String fromAction,
                                      String outcome) {

        // if the user has elected to replace the Application instance
        // entirely
        if (!navigationConfigured) {
            return null;
        }

        List<NavigationCase> caseList = caseListMap.get(viewId);

        if (caseList == null) {
            return null;
        }

        // We've found an exact match for the viewId.  Now we need to evaluate
        // from-action/outcome in the following order:
        // 1) elements specifying both from-action and from-outcome
        // 2) elements specifying only from-outcome
        // 3) elements specifying only from-action
        // 4) elements where both from-action and from-outcome are null


        return determineViewFromActionOutcome(caseList, fromAction, outcome);
    }


    /**
     * This method traverses the wild card match List (containing <code>from-view-id</code>
     * strings and finds the List of cases for each <code>from-view-id</code> string.
     * Refer to section 7.4.2 of the specification for more details.
     *
     * @param viewId     The current <code>view</code> identifier.
     * @param fromAction The action reference string.
     * @param outcome    The outcome string.
     * @return The <code>view</code> identifier.
     */

    private CaseStruct findWildCardMatch(String viewId,
                                         String fromAction,
                                         String outcome) {
        CaseStruct result = null;

        // if the user has elected to replace the Application instance
        // entirely
        if (!navigationConfigured) {
            return null;
        }

        for (String fromViewId : wildCardSet) {
            // See if the entire wildcard string (without the trailing "*" is
            // contained in the incoming viewId.  
            // Ex: /foobar is contained with /foobarbaz
            // If so, then we have found our largest pattern match..
            // If not, then continue on to the next case;

            if (!viewId.startsWith(fromViewId)) {
                continue;
            }

            // Append the trailing "*" so we can do our map lookup;

            String wcFromViewId = new StringBuilder(32).append(fromViewId).append('*').toString();
            List<NavigationCase> caseList = caseListMap.get(wcFromViewId);

            if (caseList == null) {
                return null;
            }

            // If we've found a match, then we need to evaluate
            // from-action/outcome in the following order:
            // 1) elements specifying both from-action and from-outcome
            // 2) elements specifying only from-outcome
            // 3) elements specifying only from-action
            // 4) elements where both from-action and from-outcome are null

            result = determineViewFromActionOutcome(caseList, fromAction,
                                                    outcome);
            if (result != null) {
                break;
            }
        }
        return result;
    }


    /**
     * This method will extract the cases for which a <code>from-view-id</code> is
     * an asterisk "*".
     * Refer to section 7.4.2 of the specification for more details.
     *
     * @param fromAction The action reference string.
     * @param outcome    The outcome string.
     * @return The <code>view</code> identifier.
     */

    private CaseStruct findDefaultMatch(String fromAction,
                                        String outcome) {
        // if the user has elected to replace the Application instance
        // entirely
        if (!navigationConfigured) {
            return null;
        }

        List<NavigationCase> caseList = caseListMap.get("*");

        if (caseList == null) {
            return null;
        }

        // We need to evaluate from-action/outcome in the follow
        // order:  1)elements specifying both from-action and from-outcome
        // 2) elements specifying only from-outcome
        // 3) elements specifying only from-action
        // 4) elements where both from-action and from-outcome are null

        return determineViewFromActionOutcome(caseList, fromAction, outcome);
    }


    /**
     * This method will attempt to find the <code>view</code> identifier based on action reference
     * and outcome.  Refer to section 7.4.2 of the specification for more details.
     *
     * @param caseList   The list of navigation cases.
     * @param fromAction The action reference string.
     * @param outcome    The outcome string.
     * @return The <code>view</code> identifier.
     */


    private CaseStruct determineViewFromActionOutcome(List<NavigationCase> caseList,
                                                      String fromAction,
                                                      String outcome) {

        CaseStruct result = new CaseStruct();
        for (NavigationCase cnc : caseList) {
            String cncFromAction = cnc.getFromAction();
            String fromOutcome = cnc.getFromOutcome();
            String toViewId = cnc.getToViewId();
            if ((cncFromAction != null) && (fromOutcome != null)) {
                if ((cncFromAction.equals(fromAction)) &&
                    (fromOutcome.equals(outcome))) {
                    result.viewId = toViewId;
                    result.navCase = cnc;
                    return result;
                }
            }

             if ((cncFromAction == null) && (fromOutcome != null)) {
                if (fromOutcome.equals(outcome)) {
                    result.viewId = toViewId;
                    result.navCase = cnc;
                    return result;
                }
            }

            if ((cncFromAction != null) && (fromOutcome == null)) {
                if (cncFromAction.equals(fromAction)) {
                    result.viewId = toViewId;
                    result.navCase = cnc;
                    return result;
                }
            }

            if ((cncFromAction == null) && (fromOutcome == null)) {
                result.viewId = toViewId;
                result.navCase = cnc;
                return result;
            }
        }

        return null;
    }


    private static class CaseStruct {
        String viewId;
        NavigationCase navCase;
    }

}
