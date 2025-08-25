package com.example.e_commerce.Exception.Custom;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends RuntimeException{
	private HttpStatus httpStatus;
	
	public InvalidTokenException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus=httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	
}
