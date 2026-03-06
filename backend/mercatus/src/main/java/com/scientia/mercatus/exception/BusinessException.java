package com.scientia.mercatus.exception;


import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorEnum error;
    private final String internalLog;

    public BusinessException(ErrorEnum error) {
        super(error.getMessage());
        this.error = error;
        this.internalLog = null;
    }
    public BusinessException(ErrorEnum errorEnum, String internalLog) {
        super(errorEnum.getMessage());
        this.error = errorEnum;
        this.internalLog = internalLog;
    }

}
