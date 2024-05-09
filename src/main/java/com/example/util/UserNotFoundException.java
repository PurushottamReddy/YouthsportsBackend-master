package com.example.util;

public class UserNotFoundException extends Exception {

	private String errorMessage;
	
	public UserNotFoundException(String errMsg) {
	  this.errorMessage =errMsg;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	

}
