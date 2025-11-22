package com.nyu.aichat.util;

import com.nyu.aichat.exception.ValidationException;

/**
 * Utility for validating HTTP headers.
 * Centralizes header validation (DRY principle).
 */
public final class HeaderValidator {
    private HeaderValidator() {
        // Prevent instantiation
    }
    
    /**
     * Validates userId header is present and positive.
     * 
     * @param userId The user ID from header
     * @throws ValidationException if userId is null or non-positive
     */
    public static void validateUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("X-User-Id header is required");
        }
        if (userId <= 0) {
            throw new ValidationException("X-User-Id must be a positive integer");
        }
    }
}

