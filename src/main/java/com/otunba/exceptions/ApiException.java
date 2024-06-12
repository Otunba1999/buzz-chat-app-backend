package com.otunba.exceptions;

public class ApiException extends IllegalStateException{
    public ApiException(String message) {
        super(message);
    }
}
