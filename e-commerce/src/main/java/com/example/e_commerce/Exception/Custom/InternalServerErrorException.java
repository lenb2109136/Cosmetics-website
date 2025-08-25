package com.example.e_commerce.Exception.Custom;

public class InternalServerErrorException extends RuntimeException {
    private int statusCode;

    public InternalServerErrorException(String message) {
        super(message);
        this.statusCode = 500; 
    }
    public int getStatusCode() {
        return statusCode;
    }
}
