package com.nyu.aichat.exception;

public class ValidationException extends ApiException {
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}

