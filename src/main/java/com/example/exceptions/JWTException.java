package com.example.exceptions;


public class JWTException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String message;


	public JWTException(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

}
