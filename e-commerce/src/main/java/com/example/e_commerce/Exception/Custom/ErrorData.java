package com.example.e_commerce.Exception.Custom;

import org.springframework.http.HttpStatus;

public class ErrorData extends RuntimeException{
	Object dataError;
	private HttpStatus status;

    public ErrorData(String message,HttpStatus status,Object o) {
        super(message);
        this.status = status; 
        this.dataError=o;
    }

	public Object getDataError() {
		return dataError;
	}

	public void setDataError(Object dataError) {
		this.dataError = dataError;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
    
    
}
