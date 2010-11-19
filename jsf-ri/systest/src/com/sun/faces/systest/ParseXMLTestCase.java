/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.faces.systest;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import com.sun.faces.htmlunit.AbstractTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author sheetalv
 */
public class ParseXMLTestCase extends AbstractTestCase {

    List list = new ArrayList();
    private final static String xmlDir = "/conf/share";
    private final static String systest = "/systest";

    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public ParseXMLTestCase(String name) {
        super(name);
    }


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {
        return (new TestSuite(ParseXMLTestCase.class));
    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {
        super.tearDown();
    }


    // ------------------------------------------------------------ Test Methods


    /**
     * Added for issue 904.
     */
    public void testParseXML() throws Exception {

         String curDir = System.getProperty("user.dir");
         System.out.println("current dir = " + curDir);
         String dir = "";
         if (curDir.indexOf(systest) != -1) {
            dir = curDir.substring(0, curDir.indexOf(systest));
        }
        visitAllDirsAndFiles(new File(dir + xmlDir));
        //printAllXMLFiles();
        for (Object file : list) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                factory.setValidating(true);
                SAXParser saxParser = factory.newSAXParser();
                System.out.println("XML file to be parsed : file://" + file.toString());
                saxParser.parse(new InputSource(new FileInputStream(file.toString())), new DefaultHandler());
                System.out.println("parsing complete.");
            } catch (Exception e) {
                System.out.println("Parse error for " + file.toString() + " " + e.toString());
            }
        }

    }

    // Process all files and directories under dir
    public void visitAllDirsAndFiles(File dir) {

        if (dir.isFile()) {
            if (isXML(dir)) {
                //add it to the list
                list.add(dir);
            }
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                visitAllDirsAndFiles(new File(dir, children[i]));
            }
        }
    }

    public boolean isXML(File file) {
        String name = file.getName();
        if (name.endsWith(".xml")) {
            //it is an xml file
            //add to list
            return true;
        } else {
            return false;
        }
    }

    private void printAllXMLFiles() {
        for (Object l : list) {
            System.out.println("XML file : " + l);
        }
    }
}
