package com.scientia.mercatus.exception;

import com.scientia.mercatus.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception, WebRequest webRequest){
        log.error("Unexpected error occurred", exception);
        ErrorResponseDto errorResponseDto = getErrorResponseDto(
                webRequest,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException exception,
                                                                          WebRequest webRequest)
    {
        Map<String, String> errors = new HashMap<>();
        List<FieldError> fieldErrorsList = exception.getBindingResult().getFieldErrors();
        fieldErrorsList.forEach(fieldError ->
            errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        ErrorResponseDto errorResponseDto = getErrorResponseDto(
                webRequest,
                HttpStatus.BAD_REQUEST,
                "Method Argument Validation Failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException exception, WebRequest webRequest) {
        log.error("An exception occurred due to:", exception);
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> constraintViolationSet = exception.getConstraintViolations();
        constraintViolationSet.forEach(constraintViolation ->
                errors.put(constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage()));
        ErrorResponseDto errorResponseDto = getErrorResponseDto(
                webRequest,
                HttpStatus.BAD_REQUEST,
                "Constraint validation failed",
                errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException exception, WebRequest webRequest){
         ErrorResponseDto errorResponseDto  = getErrorResponseDto(
                webRequest,
                exception.getError().getStatus(),
                exception.getError().getMessage(),
                null
        );
        return  ResponseEntity.status(exception.getError().getStatus()).body(errorResponseDto);
    }

    private ErrorResponseDto getErrorResponseDto(WebRequest webRequest,
                                                 HttpStatus httpStatus,
                                                 String message,
                                                 Map<String, String> validationError){
        return new ErrorResponseDto(
                webRequest.getDescription(false),
                httpStatus,
                message,
                LocalDateTime.now(),
                validationError
        );
    }

}

