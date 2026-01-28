package com.scientia.mercatus.exception;

public class ReservationAlreadyExpiredException extends RuntimeException {
    public ReservationAlreadyExpiredException(String message) {
        super(message);
    }
}
