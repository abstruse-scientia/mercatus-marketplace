package com.scientia.mercatus.exception;

public class ReservationNotExistsException extends RuntimeException {
    public ReservationNotExistsException(String message) {
        super(message);
    }
}
