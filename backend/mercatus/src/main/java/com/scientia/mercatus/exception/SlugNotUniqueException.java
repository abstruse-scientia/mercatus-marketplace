package com.scientia.mercatus.exception;

public class SlugNotUniqueException extends RuntimeException {
    public SlugNotUniqueException(String message) {
        super(message);
    }
}
