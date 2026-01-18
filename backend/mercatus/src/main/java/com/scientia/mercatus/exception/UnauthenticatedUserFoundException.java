package com.scientia.mercatus.exception;

public class UnauthenticatedUserFoundException extends RuntimeException {
    public UnauthenticatedUserFoundException(String message) {
        super(message);
    }
}
