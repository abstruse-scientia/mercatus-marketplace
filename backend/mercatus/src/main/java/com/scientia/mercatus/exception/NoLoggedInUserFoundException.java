package com.scientia.mercatus.exception;

public class NoLoggedInUserFoundException extends RuntimeException {
    public NoLoggedInUserFoundException(String message) {
        super(message);
    }
}
