package com.nyu.aichat.util;

import com.nyu.aichat.exception.ValidationException;

/**
 * Utility for validating path variables.
 * Centralizes path validation (DRY principle).
 */
public final class PathValidator {
    private PathValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates conversation ID is positive.
     * 
     * @param conversationId The conversation ID from path
     * @throws ValidationException if conversationId is null or non-positive
     */
    public static void validateConversationId(Long conversationId) {
        if (conversationId == null || conversationId <= 0) {
            throw new ValidationException("Conversation ID must be a positive integer");
        }
    }
}

