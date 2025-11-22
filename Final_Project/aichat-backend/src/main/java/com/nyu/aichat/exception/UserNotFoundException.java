package com.nyu.aichat.exception;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(String message) {
        super("USER_NOT_FOUND", message);
    }
}

