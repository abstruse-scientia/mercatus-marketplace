package com.scientia.mercatus.exception;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(String message) {
        super(message);
    }
}
