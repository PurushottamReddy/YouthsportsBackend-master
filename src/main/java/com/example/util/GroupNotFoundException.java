package com.example.util;

public class GroupNotFoundException extends Exception {

	private String errorMessage;
	
	public GroupNotFoundException(String msg) {
		this.errorMessage = msg;
	}

	
	public GroupNotFoundException() {
		super();
	}


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
