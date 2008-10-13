/*
 * $Id: SelectItemsIterator.java,v 1.18 2008/01/08 22:29:24 rlubke Exp $
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

package javax.faces.component;


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Collection;

import javax.faces.model.SelectItem;


/**
 * <p>Package private class for iterating over the set of {@link SelectItem}s
 * for a parent {@link UISelectMany} or {@link UISelectOne}.</p>
 */

final class SelectItemsIterator implements Iterator<SelectItem> {


    // ------------------------------------------------------------ Constructors


    /**
     * <p>Construct an iterator instance for the specified parent component.</p>
     *
     * @param parent The parent {@link UIComponent} whose children will be
     *  processed
     */
    public SelectItemsIterator(UIComponent parent) {

        kids = parent.getChildren().listIterator();
        initializeItems();

    }


    // ------------------------------------------------------ Instance Variables


    /**
     * <p>Iterator over the SelectItem elements pointed at by a
     * <code>UISelectItems</code> component, or <code>null</code>.</p>
     */
    private Iterator<SelectItem> items;


    /**
     * <p>Iterator over the children of the parent component.</p>
     */
    private ListIterator<UIComponent> kids;


    /**
     * 
     */
    private SingleElementIterator singleItemIterator;


    // -------------------------------------------------------- Iterator Methods


    /**
     * <p>Return <code>true</code> if the iteration has more elements.</p>
     */
    public boolean hasNext() {

        if (items != null) {
            if (items.hasNext()) {
                return (true);
            } else {
                items = null;
            }
        }
        Object next = findNextValidChild();
        if (next != null) {
            kids.previous();
            return true;
        }
        return false;

    }


    /**
     * <p>Return the next element in the iteration.</p>
     *
     * @throws NoSuchElementException if there are no more elements
     */
    @SuppressWarnings({"unchecked"})
    public SelectItem next() {

        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (items != null) {
            return (items.next());
        }
        initializeItems();
        return next();

    }


    /**
     * <p>Throw UnsupportedOperationException.</p>
     */
    public void remove() {

        throw new UnsupportedOperationException();

    }


    // --------------------------------------------------------- Private Methods


    private void initializeItems() {

        UIComponent kid = (UIComponent) findNextValidChild();
        if (kid instanceof UISelectItem) {
            UISelectItem ui = (UISelectItem) kid;
            SelectItem item = (SelectItem) ui.getValue();
            if (item == null) {
                item = new SelectItem(ui.getItemValue(),
                                      ui.getItemLabel(),
                                      ui.getItemDescription(),
                                      ui.isItemDisabled(),
                                      ui.isItemEscaped());
            }
            updateSingeItemIterator(item);
            items = singleItemIterator;
        } else if (kid instanceof UISelectItems) {
            UISelectItems ui = (UISelectItems) kid;
            Object value = ui.getValue();
            if (value instanceof SelectItem) {
                updateSingeItemIterator((SelectItem) value);
                items = singleItemIterator;
            } else if (value instanceof SelectItem[]) {
                items = Arrays.asList((SelectItem[]) value).iterator();
              } else if (value instanceof Collection) {
                items = ((Collection) value).iterator();
            } else if (value instanceof Map) {
                List<SelectItem> list = new ArrayList<SelectItem>(((Map) value).size());
                for (Iterator keys = ((Map) value).keySet().iterator();
                    keys.hasNext(); ) {

                    Object key = keys.next();
                    if (key == null) {
                        continue;
                    }
                    Object val = ((Map) value).get(key);
                    if (val == null) {
                        continue;
                    }
                    list.add(new SelectItem(val, key.toString(),
                        null));

                }

                items = list.iterator();
            } else {
                throw new IllegalArgumentException();
            }
        } 

    }

    private Object findNextValidChild() {

        if (kids.hasNext()) {
            Object next = kids.next();
            while (kids.hasNext() && !(next instanceof UISelectItem || next instanceof UISelectItems)) {
                next = kids.next();
            }
            if (next instanceof UISelectItem || next instanceof UISelectItems) {
                return next;
            }
        }
        return null;

    }


    private void updateSingeItemIterator(SelectItem item) {

        if (singleItemIterator == null) {
            singleItemIterator = new SingleElementIterator();
        }
        singleItemIterator.updateItem(item);

    }


    // ---------------------------------------------------------- Nested Classes


    private static final class SingleElementIterator implements Iterator<SelectItem> {

        private SelectItem item;
        private boolean nextCalled;

        public boolean hasNext() {
            return !nextCalled;
        }

        public SelectItem next() {
            if (nextCalled) {
                throw new NoSuchElementException();
            }
            nextCalled = true;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void updateItem(SelectItem item) {
            this.item = item;
            nextCalled = false;
        }

    }
}
