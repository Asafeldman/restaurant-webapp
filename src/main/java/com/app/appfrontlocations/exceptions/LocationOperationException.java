package com.app.appfrontlocations.exceptions;

public class LocationOperationException extends RuntimeException {
    public LocationOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
