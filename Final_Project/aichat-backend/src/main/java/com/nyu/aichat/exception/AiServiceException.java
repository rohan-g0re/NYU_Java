package com.nyu.aichat.exception;

/**
 * Exception thrown when AI service operations fail.
 * Supports exception chaining to preserve original stack traces.
 */
public class AiServiceException extends ApiException {
    public AiServiceException(String message) {
        super("AI_SERVICE_ERROR", message);
    }
    
    public AiServiceException(String message, Throwable cause) {
        super("AI_SERVICE_ERROR", message);
        initCause(cause);
    }
}

