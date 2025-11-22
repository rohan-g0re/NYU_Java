package com.nyu.aichat.client.api;

/**
 * Custom exception for API errors.
 * Contains error code and message from backend ErrorResponse.
 */
public class ApiException extends Exception {
    private final String errorCode;
    private final int httpStatus;
    
    public ApiException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public ApiException(String errorCode, String message) {
        this(errorCode, message, 0);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
    
    @Override
    public String toString() {
        return "ApiException{errorCode='" + errorCode + "', message='" + getMessage() + 
               "', httpStatus=" + httpStatus + "}";
    }
}

