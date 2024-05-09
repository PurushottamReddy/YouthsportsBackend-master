package com.example.util;

public class InvalidInputException extends Exception{

	private String errorMessage;
	public InvalidInputException(String msg) {
		this.errorMessage = msg;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
