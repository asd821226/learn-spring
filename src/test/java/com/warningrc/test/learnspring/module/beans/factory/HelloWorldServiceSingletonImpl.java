package com.warningrc.test.learnspring.module.beans.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldServiceSingletonImpl implements HelloWorldService {
	
    /** slf4j Logger for HelloWorldServiceSingletonImpl. */
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldServiceSingletonImpl.class);
    
    
    private String userName;

	public HelloWorldServiceSingletonImpl(String userName) {
		this.userName = userName;
	}

	@Override
	public void sayHello(String people) {
		logger.info(userName + " say : hello " + people);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
