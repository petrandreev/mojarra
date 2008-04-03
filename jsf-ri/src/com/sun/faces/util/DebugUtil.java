/*
 * $Id: DebugUtil.java,v 1.41 2007/11/12 23:08:24 rlubke Exp $
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

package com.sun.faces.util;

// DebugUtil.java

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.sun.faces.io.FastStringWriter;
import com.sun.faces.renderkit.RenderKitUtils;

/**
 * <B>DebugUtil</B> is a class ...
 * <p/>
 * <B>Lifetime And Scope</B> <P>
 */

public class DebugUtil {

//
// Protected Constants
//

//
// Class Variables
//

    private static boolean keepWaiting = true;

    private static int curDepth = 0;

//
// Instance Variables
//

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

    public DebugUtil() {
        super();
        // Util.parameterNonNull();
        this.init();
    }


    protected void init() {
        // super.init();
    }

//
// Class methods
//

    public static void setKeepWaiting(boolean keepWaiting) {

        DebugUtil.keepWaiting = keepWaiting;

    }

    /**
     * Usage: <P>
     * <p/>
     * Place a call to this method in the earliest possible entry point of
     * your servlet app.  It will cause the app to enter into an infinite
     * loop, sleeping until the static var keepWaiting is set to false.  The
     * idea is that you attach your debugger to the servlet, then, set a
     * breakpont in this method.  When it is hit, you use the debugger to set
     * the keepWaiting class var to false.
     */

    public static void waitForDebugger() {
        while (keepWaiting) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("DebugUtil.waitForDebugger(): Exception: " +
                                   e.getMessage());
            }
        }
    }


    private static void indentPrintln(Writer out, String str) {

        // handle indentation
        try {
            for (int i = 0; i < curDepth; i++) {
                out.write("  ");
            }
            out.write(str + "\n");
        } catch (IOException ignored) {}
    }

    /**
     * @param root the root component
     * @return the output of printTree() as a String.
     * Useful when used with a Logger. For example:
     *    logger.log(DebugUtil.printTree(root));
     */
    public static String printTree(UIComponent root) {
        Writer writer = new FastStringWriter(1024);
        printTree(root, writer);
        return writer.toString();
    }

    /**
     * Output of printTree() to a PrintStream.
     * Usage:
     *    DebugUtil.printTree(root, System.out);
     *
     * @param root the root component
     * @param out the PrintStream to write to
     */
    public static void printTree(UIComponent root, PrintStream out) {
        PrintWriter writer = new PrintWriter(out);
        printTree(root, writer);
        writer.flush();
    }
    
    public static void printTree(UIComponent root, Logger logger, Level level) {
        StringWriter sw = new StringWriter();
        printTree(root, sw);
        logger.log(level, sw.toString());
    }

    public static void printTree(UIComponent root, Writer out) {
        if (null == root) {
            return;
        }
        Object value = null;

/* PENDING
   indentPrintln(out, "===>Type:" + root.getComponentType());
*/
        indentPrintln(out, "id:" + root.getId());
        indentPrintln(out, "type:" + root.toString());

        if (root instanceof javax.faces.component.UISelectOne) {
            List<SelectItem> items =
                  RenderKitUtils.getSelectItems(FacesContext.getCurrentInstance(), root);
            indentPrintln(out, " {");
            if (!items.isEmpty()) {
                for (SelectItem curItem : items) {
                    indentPrintln(out, "\t value="
                                       + curItem.getValue()
                                       +
                                       " label="
                                       + curItem.getLabel()
                                       + " description="
                                       +
                                       curItem.getDescription());
                }
            }
            indentPrintln(out, " }");
        } else {
            if (root instanceof ValueHolder) {
                value = ((ValueHolder)root).getValue();
            }
            indentPrintln(out, "value= " + value);

            Iterator<String> it = root.getAttributes().keySet().iterator();
            if (it != null) {
                while (it.hasNext()) {
                    String attrValue;
                    String attrName = it.next();
                    Object attrObj = root.getAttributes().get(attrName);

                    if (null != attrObj) {
                        // chop off the address since we don't want to print
                        // out anything that'll vary from invocation to
                        // invocation
                        attrValue = attrObj.toString();
                        int at;
                        boolean doTruncate = false;
                        if (-1 == (at = attrValue.indexOf("$"))) {
                            if (-1 != (at = attrValue.indexOf("@"))) {
                                doTruncate = true;
                            }
                        } else {
                            doTruncate = true;
                        }

                        if (doTruncate) {
                            attrValue = attrValue.substring(0, at);
                        }
                    } else {
                        attrValue = (String) attrObj;
                    }

                    indentPrintln(out, "attr=" + attrName +
                                       " : " + attrValue);
                }
            }
        }

        curDepth++;
        Iterator<UIComponent> it = root.getChildren().iterator();
        // print all the facets of this component
        for (UIComponent uiComponent : root.getFacets().values()) {
            printTree(uiComponent, out);
        }
        // print all the children of this component
        while (it.hasNext()) {
            printTree(it.next(), out);
        }
        curDepth--;
    }
    
    public static void simplePrintTree(UIComponent root, 
                                      String duplicateId,
                                       Writer out) {
        if (null == root) {
            return;
        }                     

        if (duplicateId.equals(root.getId())) {
            indentPrintln(out, "+id: " + root.getId() + "  <===============");
        } else {
            indentPrintln(out, "+id: " + root.getId());
        }
        indentPrintln(out, " type: " + root.toString());           

        curDepth++;       
        // print all the facets of this component
        for (UIComponent uiComponent : root.getFacets().values()) {
            simplePrintTree(uiComponent, duplicateId, out);
        }
        // print all the children of this component
        for (UIComponent uiComponent : root.getChildren()) {
            simplePrintTree(uiComponent, duplicateId, out);
        }
        curDepth--;
    }


//    /**
//     * Output of printTree() as a String. 
//     * Useful when used with a Logger. For example:
//     *    logger.log(DebugUtil.printTree(root));
//     */
//    public static String printTree(TreeStructure root) {
//        Writer writer = new FastStringWriter(1024);
//        printTree(root, writer);
//        return writer.toString();
//    }
//
//    /**
//     * Output of printTree() to a PrintStream. 
//     * Usage:
//     *    DebugUtil.printTree(root, System.out);
//     */
//    public static void printTree(TreeStructure root, PrintStream out) {
//        PrintWriter writer = new PrintWriter(out);
//        printTree(root, writer);
//        writer.flush();
//    }
//
//    public static void printTree(TreeStructure root, Writer out) {
//        if (null == root) {
//            return;
//        }
//        int i = 0;
//        Object value = null;
//
///* PENDING
//   indentPrintln(out, "===>Type:" + root.getComponentType());
//*/
//        indentPrintln(out, "id:" + root.id);
//        indentPrintln(out, "type:" + root.className);
//
//        Iterator items = null;
//        SelectItem curItem = null;
//        int j = 0;
//
//        curDepth++;
//        if (null != root.children) {
//            Iterator<TreeStructure> it = root.children.iterator();
//            while (it.hasNext()) {
//                printTree(it.next(), out);
//            }
//        }
//        curDepth--;
//    }

    public static void printTree(Object [] root, Writer out) {
        if (null == root) {
            indentPrintln(out, "null");
            return;
        }

/* PENDING
   indentPrintln(out, "===>Type:" + root.getComponentType());
*/
        // drill down to the bottom of the first element in the array
        boolean foundBottom = false;
        Object [] myState = root;
        while (!foundBottom) {
            Object state = myState[0];
            foundBottom = !state.getClass().isArray();
            if (!foundBottom) {
                myState = (Object []) state;
            }
        }

        indentPrintln(out, "type:" + myState[8]);

        curDepth++;
        root = (Object []) root[1];
        for (int i = 0; i < root.length; i++) {
            printTree((Object []) root[i], out);
        }
        curDepth--;
    }
//
// General Methods
//


} // end of class DebugUtil
