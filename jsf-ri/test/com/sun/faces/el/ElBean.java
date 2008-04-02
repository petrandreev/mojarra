/*
 * $Id: ElBean.java,v 1.4 2004/02/06 18:56:44 rlubke Exp $
 */

/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.faces.el;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// JavaBean for simple expression evaluation tests

public class ElBean {


    public ElBean() {
    }


    public ElBean(String stringProperty) {
        this.stringProperty = stringProperty;
    }


    private boolean booleanProperty = true;


    public boolean getBooleanProperty() {
        return (this.booleanProperty);
    }


    public void setBooleanProperty(boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }


    private byte byteProperty = 123;


    public byte getByteProperty() {
        return (this.byteProperty);
    }


    public void setByteProperty(byte byteProperty) {
        this.byteProperty = byteProperty;
    }


    private double doubleProperty = 654.321;


    public double getDoubleProperty() {
        return (this.doubleProperty);
    }


    public void setDoubleProperty(double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }


    private float floatProperty = (float) 123.45;


    public float getFloatProperty() {
        return (this.floatProperty);
    }


    public void setFloatProperty(float floatProperty) {
        this.floatProperty = floatProperty;
    }


    private int intArray[] = {1, 2, 3};


    public int[] getIntArray() {
        return (this.intArray);
    }


    public void setIntArray(int intArray[]) {
        this.intArray = intArray;
    }


    private List intList = null;


    public List getIntList() {
        if (intList == null) {
            intList = new ArrayList();
            intList.add(new Integer(10));
            intList.add(new Integer(20));
            intList.add(new Integer(30));
            intList.add(new Integer(40));
            intList.add(new Integer(50));
        }
        return (intList);
    }


    public void setIntList(List intList) {
        this.intList = intList;
    }


    private int intProperty = 1234;


    public int getIntProperty() {
        return (this.intProperty);
    }


    public void setIntProperty(int intProperty) {
        this.intProperty = intProperty;
    }


    private long longProperty = 54321;


    public long getLongProperty() {
        return (this.longProperty);
    }


    public void setLongProperty(long longProperty) {
        this.longProperty = longProperty;
    }


    private ElBean nestedArray[] = null;


    public ElBean[] getNestedArray() {
        if (nestedArray == null) {
            nestedArray = new ElBean[2];
            nestedArray[0] = new ElBean("Nested0");
            nestedArray[1] = new ElBean("Nested1");
        }
        return (this.nestedArray);
    }


    public void setNestedArray(ElBean nestedArray[]) {
        this.nestedArray = nestedArray;
    }


    private List nestedList = null;


    public List getNestedList() {
        if (nestedList == null) {
            nestedList = new ArrayList();
            nestedList.add(new ElBean("list0"));
            nestedList.add(new ElBean("list1"));
            nestedList.add(new ElBean("list2"));
            nestedList.add(new ElBean("list3"));
        }
        return (this.nestedList);
    }


    public void setNestedList(List nestedList) {
        this.nestedList = nestedList;
    }


    private Map nestedMap = null;


    public Map getNestedMap() {
        if (nestedMap == null) {
            nestedMap = new HashMap();
            nestedMap.put("map0", new ElBean("map0"));
            nestedMap.put("map1", new ElBean("map1"));
            nestedMap.put("map2", new ElBean("map2"));
        }
        return (this.nestedMap);
    }


    public void setNestedMap(Map nestedMap) {
        this.nestedMap = nestedMap;
    }


    private ElBean nestedProperty = null;


    public ElBean getNestedProperty() {
        if (nestedProperty == null) {
            nestedProperty = new ElBean();
        }
        return (this.nestedProperty);
    }


    public void setNestedProperty(ElBean nestedProperty) {
        this.nestedProperty = nestedProperty;
    }


    private short shortProperty = 321;


    public short getShortProperty() {
        return (this.shortProperty);
    }


    public void setShortProperty(short shortProperty) {
        this.shortProperty = shortProperty;
    }


    private String stringProperty = "This is a String";


    public String getStringProperty() {
        return (this.stringProperty);
    }


    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }


    private String nullString = null;


    public String getNullStringProperty() {
        return ("String length is:" + nullString.length());
    }


}
