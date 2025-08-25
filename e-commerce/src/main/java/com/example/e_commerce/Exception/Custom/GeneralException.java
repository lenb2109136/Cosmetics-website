package com.example.e_commerce.Exception.Custom;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class GeneralException extends RuntimeException {
    private HttpStatus status;

    public GeneralException(String message,HttpStatus status) {
        super(message);
        this.status = status; 
    }

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
}
