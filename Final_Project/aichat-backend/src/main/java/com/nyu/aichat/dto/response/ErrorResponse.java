package com.nyu.aichat.dto.response;

public class ErrorResponse {
    private String error;      // Error code (e.g., "USER_NOT_FOUND")
    private String message;    // Human-readable message
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

