/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.faces.config;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.text.MessageFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.sun.faces.util.FacesLogger;
import com.sun.faces.util.Timer;

/**
 * This class is used by the config system to order <code>faces-config</code>
 * documents found on the classpath or configured explicitly via the
 * <code>javax.faces.CONFIG_FILES</code> context init parameter.
 */
public class DocumentOrderingWrapper {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = FacesLogger.CONFIG.getLogger();

    /**
     * {@link Comparator} implementation to aid in sorting <code>faces-config</code>
     * documents.
     */
    private static Comparator<DocumentOrderingWrapper> COMPARATOR =
          new DocumentOrderingComparator();

    /**
     * This is the limit on the number of attempts made to sort the documents.
     * Any attempt to exceed this limit will result in an Exception being thrown.
     */
    private static final int MAX_SORT_PASSED = 1000;

    /**
     * Constant for the <code>ordering</code> element.
     */
    private static final String ORDERING = "ordering";

    /**
     * Constant for the <code>before</code> element.
     */
    private static final String BEFORE = "before";

    /**
     * Constant for the <code>after</code> element.
     */
    private static final String AFTER = "after";

    /**
     * Constant for the <code>name</code> element.
     */
    private static final String NAME = "name";

    /**
     * Constant for the <code>others</code> element.
     */
    private static final String OTHERS = "others";

    /**
     * Others keyword for sorting.
     */
    private static final String OTHERS_KEY = DocumentOrderingWrapper.class.getName() + ".OTHERS_KEY";

    /**
     * Return code indicating that element <code>n</code> is to be swapped
     * with <code>n + 1</code>
     */
    private static final int SWAP = -1;

    /**
     * Return code indicating that no swap needs to occur for the elements
     * being compared.
     */
    private static final int DO_NOT_SWAP = 0;

    /**
     * The wrapped Document.
     */
    private Document document;

    /**
     * The wrapped Document's ID.
     */
    private String id;

    /**
     * The wrapped Document's before IDs.
     */
    private String[] beforeIds;

    /**
     * The wrapped Document's before IDs.
     */
    private String[] afterIds;

    // -------------------------------------------------------- Constructors


    /**
     * Constructs a new <code>DocumentOrderingWrapper</code> for the specified
     * <code>Document<code>.
     */
    public DocumentOrderingWrapper(Document document) {

        this.document = document;
        init();

    }


    // ------------------------------------------------------ Public Methods


    /**
     * @return the wrapped <code>Document</code>
     */
    public Document getDocument() {

        return document;

    }


    /**
     * @return this <code>Document</code>'s ID, if any
     */
    public String getDocumentId() {

        return id;

    }


    /**
     * @return this <code>Document<code>'s before IDs, if any
     */
    public String[] getBeforeIds() {

        return beforeIds;

    }


    /**
     * @return this <code>Document</code>'s after IDs, if any
     */
    public String[] getAfterIds() {

        return afterIds;

    }


    /**
     * @return <code>true</code> if any before IDs are present, otherwise
     *  <code>false</code>
     */
    public boolean isBeforeOrdered() {
        return beforeIds.length != 0;
    }


    /**
     * @return <code>true</code> if any after IDs are present, otherwise,
     *  <code>false</code>
     */
    public boolean isAfterOrdered() {
        return afterIds.length != 0;
    }


    /**
     * @return <code>true</code> if this document has any before or after IDs,
     *  otherwise <code>false</code>
     */
    public boolean isOrdered() {
        return (isBeforeOrdered() || isAfterOrdered());
    }


    /**
     * @return <code>true</code> if this document is before the specified
     *  <code>id</code>, otherwise <code>false</code>
     */
    public boolean isBefore(String id) {

        return (search(beforeIds, id));

    }


    /**
     * @return <code>true</code> if this document is after the specified
     *  <code>id</code>, otherwise <code>false</code>
     */
    public boolean isAfter(String id) {

        return (search(afterIds, id));
    }


    /**
     * @return <code>true</code> if this document is after others, otherwise
     *  <code>false</code>
     */
    public boolean isAfterOthers() {

        return (search(afterIds, OTHERS_KEY));

    }


    /**
     * @return <code>true</code> if this document is before others, otherwise
     *  <code>false</code>
     */
    public boolean isBeforeOthers() {

         return (search(beforeIds, OTHERS_KEY));

    }


    @Override
    public String toString() {
        return "Document{" +
               "id='" + id + '\'' +
               ", beforeIds="
               + (beforeIds == null ? null : Arrays.asList(beforeIds))
               +
               ", afterIds="
               + (afterIds == null ? null : Arrays.asList(afterIds))
               +
               '}';
    }


    /**
     * Sort the provided array of <code>Document</code>s per the order specified
     * in the List represented by absoluteOrder.
     * @param documents Documents to sort
     * @param absoluteOrder the absolute order as specified in the /WEB-INF/faces-config.xml
     * @return an array of DocumentOrderingWrappers that may be smaller than the
     *  input array of wrappers.
     */
    public static DocumentOrderingWrapper[] sort(DocumentOrderingWrapper[] documents,
                                                 List<String> absoluteOrder) {

        List<DocumentOrderingWrapper> sourceList = new CopyOnWriteArrayList<DocumentOrderingWrapper>();
        sourceList.addAll(Arrays.asList(documents));

        List<DocumentOrderingWrapper> targetList = new ArrayList<DocumentOrderingWrapper>();
        for (String name : absoluteOrder) {
            if ("others".equals(name)) {
                continue;
            }
            boolean found = false;
            for (DocumentOrderingWrapper wrapper : sourceList) {
                if (!found && name.equals(wrapper.getDocumentId())) {
                    found = true;
                    targetList.add(wrapper);
                    sourceList.remove(wrapper);
                } else if (found && name.equals(wrapper.getDocumentId())){
                    // we've already processed a document with this name
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        LOGGER.log(Level.WARNING,
                                   "Multiple documents named {0} found while processing absolute ordering.  Processing the first named document only.",
                                   new Object[] { name });
                    }
                    // only log this once
                    break;
                }
            }
            if (!found) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING,
                               "Unable to find document named ''{0}'' while performing absolut ordering processing.",
                               new Object[] { name });
                }
            }
        }

        int othersIndex = absoluteOrder.indexOf("others");
        if (othersIndex != -1) {
            // any wrappers left in sourceList are considered others.
            // start pushing them into targetList at the index
            for (DocumentOrderingWrapper wrapper : sourceList) {
                targetList.add(othersIndex, wrapper);
            }
        }

        return targetList.toArray(new DocumentOrderingWrapper[targetList.size()]);
        
    }


    /**
     * Sort the provided array of <code>Document</code>s per the requirements
     * of the 2.0 specification.  Note, that this method only provides partial
     * ordering and not absolute ordering.
     */
    public static void sort(DocumentOrderingWrapper[] documents) {

        Timer t = Timer.getInstance();
        if (t != null) {
            t.startTiming();
        }
        try {
            enhanceOrderingData(documents);
        } catch (CircularDependencyException re) {
            String msg = "Circular dependencies detected!\nDocument Info\n==================\n";
            for (DocumentOrderingWrapper w : documents) {
                msg += ("  " + w.toString() + '\n');
            }
            throw new ConfigurationException(msg);
        }

        boolean doMore = true;
        int numberOfPasses = 0;

        while (doMore) {
            numberOfPasses++;
            if (numberOfPasses == MAX_SORT_PASSED) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    String msg = "Exceeded maximum number of attempts to sort the application's faces-config documents.\nDocument Info\n==================";
                    for (DocumentOrderingWrapper w : documents) {
                        msg += ("  " + w.toString() + '\n');
                    }
                    LOGGER.severe(msg);                    
                }
                throw new ConfigurationException("Exceeded maximum number of attempts to sort the faces-config documents.");
            }
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           "Starting sort pass number {0}...",
                           numberOfPasses);
            }
            doMore = false;
            for (int i = 0; i < documents.length - 1; i++) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               "Comparing {0}, {1}",
                               new Object[] { documents[i].id, documents[i + 1].id });
                }
                if (COMPARATOR.compare(documents[i], documents[i + 1]) != DO_NOT_SWAP) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.log(Level.FINE,
                                   "Swapping {0} with {1}",
                                   new Object[] { documents[i].id, documents[i + 1].id });
                    }
                    DocumentOrderingWrapper temp = documents[i];
                    documents[i] = documents[i + 1];
                    documents[i + 1] = temp;
                    doMore = true;
                }
            }

            // compare first and last elements
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE,
                           "Comparing {0}, {1}",
                           new Object[]{documents[0].id, documents[documents.length - 1].id});
            }
            if (COMPARATOR.compare(documents[0], documents[documents.length - 1]) != DO_NOT_SWAP) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.log(Level.FINE,
                               "Swapping {0} with {1}",
                               new Object[]{documents[0].id, documents[documents.length - 1].id});
                }
                DocumentOrderingWrapper temp = documents[0];
                documents[0] = documents[documents.length - 1];
                documents[documents.length - 1] = temp;
                doMore = true;
            }
        }

        if (t != null) {
            t.stopTiming();
            t.logResult("\"faces-config\" document sorting complete in " + numberOfPasses + '.');
        }

    }

    // ----------------------------------------------------- Private Methods


    /**
     * Update before/after knowledge of all nodes in the array.
     * Consider the case of A after B, B after C, C after D, and D
     * with no ordering characteristics.
     * When the code below executes, the before/after of specific nodes
     * will be updated so that in the case of the example above we'd
     * have:
     *
     *  A -> after B
     *  B -> before A, after C
     *  C -> before B, A after D
     *  D -> before C, B, A
     *
     * So when an attempt is made to sort [A, B, C, D] the end
     * result is [D, C, B, A]
     * No extra enhancement of after ID information is necessary outside
     * of the single node information due to the way the algorithm processes
     * the array.
     *
     * This method also performs cyclic detection.  If, after updating the
     * before/after information, the before/after information contains a
     * reference to the document ID we're currently processing, throw an
     * exception.
     */
    private static void enhanceOrderingData(DocumentOrderingWrapper[] wrappers)
    throws CircularDependencyException {

        for (int i = 0; i < wrappers.length; i++) {
            DocumentOrderingWrapper w = wrappers[i];

            // process before IDs other than 'others'
            for (String id : w.getBeforeIds()) {
                if (OTHERS_KEY.equals(id)) {
                    continue;
                }
                for (int ii = 0; ii < wrappers.length; ii++) {
                    DocumentOrderingWrapper other = wrappers[ii];
                    if (id.equals(other.id)) {
                        String[] afterIds = other.getAfterIds();
                        if (Arrays.binarySearch(afterIds, w.id) < 0) {
                            Set<String> newAfterIds = new HashSet<String>(afterIds.length + 1);
                            newAfterIds.addAll(Arrays.asList(afterIds));
                            newAfterIds.add(w.id);
                            other.afterIds = newAfterIds
                                  .toArray(new String[newAfterIds.size()]);
                            Arrays.sort(other.afterIds);
                        }

                        String[] otherBeforeIds = other.getBeforeIds();
                        if (otherBeforeIds.length > 0) {

                            String[] currentBeforeIds = w.getBeforeIds();
                            Set<String> newBeforeIds = new HashSet<String>();
                            newBeforeIds.addAll(Arrays.asList(currentBeforeIds));
                            for (String bid : otherBeforeIds) {
                                if (OTHERS_KEY.equals(bid)) {
                                    continue;
                                }
                                newBeforeIds.add(bid);
                            }
                            String[] temp = newBeforeIds.toArray(new String[newBeforeIds.size()]);
                            Arrays.sort(temp);
                            if (search(temp, w.id)) {
                                throw new CircularDependencyException();
                            }
                            w.beforeIds = temp;
                        }
                    }
                }

            }

            // process after IDs other than 'others'
            for (String id : w.getAfterIds()) {
                if (OTHERS_KEY.equals(id)) {
                    continue;
                }
                for (int ii = 0; ii < wrappers.length; ii++) {
                    DocumentOrderingWrapper other = wrappers[ii];
                    if (id.equals(other.id)) {
                        String[] beforeIds = other.getBeforeIds();
                        if (Arrays.binarySearch(beforeIds, w.id) < 0) {
                            Set<String> newBeforeIds =
                                  new HashSet<String>(beforeIds.length + 1);
                            newBeforeIds.addAll(Arrays.asList(beforeIds));
                            newBeforeIds.add(w.id);
                            other.beforeIds = newBeforeIds
                                  .toArray(new String[newBeforeIds.size()]);
                            Arrays.sort(other.beforeIds);
                        }
                        String[] otherAfterIds = other.getAfterIds();
                        if (otherAfterIds.length > 0) {
                            String[] currentAfterIds = w.getAfterIds();
                            Set<String> newAfterIds = new HashSet<String>();
                            newAfterIds.addAll(Arrays.asList(currentAfterIds));
                            for (String bid : otherAfterIds) {
                                if (OTHERS_KEY.equals(bid)) {
                                    continue;
                                }
                                newAfterIds.add(bid);
                            }
                            String[] temp = newAfterIds.toArray(new String[newAfterIds.size()]);
                            Arrays.sort(temp);
                            if (search(temp, w.id)) {
                                throw new CircularDependencyException();
                            }
                            w.afterIds = temp;
                        }
                    }
                }
            }
        }
    }


    /**
     * Simple helper method around <code>Arrays.binarySearch()</code>.
     * @param ids an array of IDs
     * @param id the ID to search for
     * @return true if <code>ids</code> contains <code>id</code>
     */
    private static boolean search(String[] ids, String id) {

        return (Arrays.binarySearch(ids, id) >= 0);

    }


    /**
     * <p>
     * Performs the initialization necessary to allow sorting of
     * <code>faces-config</code> documents.
     * </p>
     */
    private void init() {

        Element documentElement = document.getDocumentElement();
        String namespace = documentElement.getNamespaceURI();
        id = getDocumentName(documentElement);
        NodeList orderingElements =
              documentElement.getElementsByTagNameNS(namespace, ORDERING);

        Set<String> beforeIds = null;
        Set<String> afterIds = null;

        if (orderingElements.getLength() > 0) {
            for (int i = 0, len = orderingElements.getLength(); i < len; i++) {
                Node orderingNode = orderingElements.item(i);
                NodeList children = orderingNode.getChildNodes();
                for (int j = 0, jlen = children.getLength(); j < jlen; j++) {
                    Node n = children.item(j);
                    if (beforeIds == null) {
                        beforeIds = extractIds(n, BEFORE);
                    }
                    if (afterIds == null) {
                        afterIds = extractIds(n, AFTER);
                    }
                }
            }
        }
        this.beforeIds = ((beforeIds != null)
                          ? beforeIds.toArray(new String[beforeIds.size()])
                          : new String[0]);
        this.afterIds = ((afterIds != null)
                         ? afterIds.toArray(new String[afterIds.size()])
                         : new String[0]);
        Arrays.sort(this.beforeIds);
        Arrays.sort(this.afterIds);

        // ensure any ID defined in the 'before' array isn't present in the
        // 'after' array and vice versa as a documents can't come before
        // *and* after another.
        checkDuplicates(this.beforeIds, this.afterIds);
        checkDuplicates(this.afterIds, this.beforeIds);

    }


    private String getDocumentName(Element documentElement) {

        NodeList children = documentElement.getChildNodes();
        String documentName = "";
        if (children != null && children.getLength() > 0) {
            for (int i = 0, len = children.getLength(); i < len; i++) {
                Node n = children.item(i);
                if (NAME.equals(n.getLocalName())) {
                    documentName = getNodeText(n);
                    break;
                }
            }
        }
        return documentName;

    }


    /**
     * Ensure the IDs in <code>source</code> aren't present in <code>searchTarget</code>.
     */
    private void checkDuplicates(String[] source, String[] searchTarget) {

        for (String id : source) {
            if (search(searchTarget, id)) {
                String msg = MessageFormat.format("Document {0} is specified to come before and after {1}.",
                                                  document.getDocumentURI(),
                                                  id);
                throw new ConfigurationException(msg);
            }
        }

    }


    /**
     * Extract and return a <code>Set</code> of IDs contained within the provided
     * <code>Node</code>.
     */
    private Set<String> extractIds(Node n, String nodeName) {

        Set<String> idsList = null;
        if (nodeName.equals(n.getLocalName())) {
            idsList = new HashSet<String>();
            NodeList ids = n.getChildNodes();
            for (int k = 0, klen = ids.getLength(); k < klen; k++) {
                Node idNode = ids.item(k);
                if (NAME.equals(idNode.getLocalName())) {
                    String id = getNodeText(idNode);
                    if (id != null) {
                        idsList.add(id);
                    }
                } if (OTHERS.equals(idNode.getLocalName())) {
                    if (id != null) {
                        idsList.add(OTHERS_KEY);
                    }
                }
            }
        }
        return idsList;

    }


    /**
     * Return the textual content, if any, of the provided <code>Node</code>.
     */
    private String getNodeText(Node node) {

        String res = null;
        if (node != null) {
            res = node.getTextContent();
            if (res != null) {
                res = res.trim();
            }
        }

        return ((res != null && res.length() != 0) ? res : null);

    }


    // ---------------------------------------------------------- Nested Classes


    @SuppressWarnings({"ComparatorNotSerializable"})
    private static final class DocumentOrderingComparator implements Comparator<DocumentOrderingWrapper> {


        // --------------------------------------------- Methods from Comparator


        public int compare(DocumentOrderingWrapper wrapper1,
                           DocumentOrderingWrapper wrapper2) {

            String w1Id = wrapper1.id;
            String w2Id = wrapper2.id;
            boolean w1IsOrdered = wrapper1.isOrdered();
            boolean w2IsOrdered = wrapper2.isOrdered();
            if (w1IsOrdered && !w2IsOrdered) {
                if (wrapper1.isAfterOrdered() && !wrapper1.isBeforeOthers()) {
                    return SWAP;
                }
            }

            boolean w2IsBeforeW1 = wrapper2.isBefore(w1Id);
            boolean w1IsAfterW2 = wrapper1.isAfter(w2Id);

            if (w2IsBeforeW1 || w1IsAfterW2) {
                return SWAP; // move w2 before w1
            }

            // no explicit ID ordering.  Check others ordering
            boolean w1IsAfterOthers = wrapper1.isAfterOthers();
            if (w1IsAfterOthers && !wrapper1.isBefore(w2Id) && !(wrapper1.isAfterOthers() && wrapper2.isAfterOthers())) {
                return SWAP;
            }
            boolean w2IsBeforeOthers = wrapper2.isBeforeOthers();
            if (w2IsBeforeOthers && !wrapper2.isAfter(w1Id) && !(wrapper1.isBeforeOthers() && wrapper2.isBeforeOthers())) {
                return SWAP;
            }

            return DO_NOT_SWAP;

        }

    } // END FacesConfigComparator

    
    @SuppressWarnings({"serial"})
    private static final class CircularDependencyException extends Exception {


        // -------------------------------------------------------- Constructors


        public CircularDependencyException() {

            super();

        }


        public CircularDependencyException(String message) {

            super(message);

        }


        public CircularDependencyException(String message, Throwable cause) {

            super(message, cause);

        }


        public CircularDependencyException(Throwable cause) {

            super(cause);

        }

    } // END CircularDependencyException
}
