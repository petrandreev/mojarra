/*
 * $Id: LoginBean.java,v 1.7 2002/08/14 19:11:27 eburns Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package basic;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.component.SelectItem;

public class LoginBean {

    String userName = null;
    String password = null;
    String address = null;
    String validUser = null;
    Integer pin = null;

    String defaultOptions[] = {
	"pinto",
	"black",
	"garbanzo",
	"green",
	"string",
	"coffee",
	"baked"
    };

    public LoginBean () {
        System.out.println("Model Object Created");
	options = new ArrayList(defaultOptions.length);
	int i = 0;
	for (i = 0; i < defaultOptions.length; i++) {
	    options.add(new SelectItem(defaultOptions[i], defaultOptions[i], 
				       defaultOptions[i]));
	}
    }
  
    public void setUserName(String user_name) {
        userName = user_name;
        System.out.println("Set userName " + userName);
    }

    public String getUserName() {
        System.out.println("get userName " + userName);
        return userName;
    }

    public void setPin(Integer _pin) {
        this.pin = _pin;
        System.out.println("Set pin " + pin);
    }

    public Integer getPin() {
        System.out.println("get pin " + pin);
        return pin;
    }

    public void setPassword(String pwd) {
        password = pwd;
        System.out.println("set Password " + password);
    }

    public String getPassword() {
        System.out.println("get Password " + password);
        return password;
    }

    public void setAddress(String addr) {
        address = addr;
        System.out.println("Set Address " + address);
    }

    public String getAddress() {
        System.out.println("get Address " + address);
        return address;
    }

    public void setValidUser(String valid_user) {
        validUser = valid_user;
        System.out.println("Set validUser " + validUser);
    }

    public String getvalidUser() {
        System.out.println("get validUser " + validUser);
        return validUser;
    }

    protected double doubleVal;

    public void setDouble(double newDoubleVal) {
	doubleVal = newDoubleVal;
    }

    public double getDouble() {
	return doubleVal;
    }

    protected int intVal;

    public int getInt()
    {
	return intVal;
    }
    
    public void setInt(int newIntVal)
    {
	intVal = newIntVal;
    }

    protected String stringVal;

    public String getString()
    {
	return stringVal;
    }
    
    public void setString(String newStringVal)
    {
	stringVal = newStringVal;
    }

    protected ArrayList options = null;
    protected Object currentOption = defaultOptions[4];
    
    public Collection getOptions() {
	return options;
    }

    public void setOptions(Collection newOptions) {
	options = new ArrayList(newOptions);
    }

    public Object getCurrentOption() {
	return currentOption;
    }

    public void setCurrentOption(Object newCurrentOption)
    {
	currentOption = newCurrentOption;
    }



}
