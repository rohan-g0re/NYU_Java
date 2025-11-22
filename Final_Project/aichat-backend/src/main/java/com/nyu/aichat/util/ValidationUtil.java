package com.nyu.aichat.util;

import com.nyu.aichat.exception.ValidationException;

public class ValidationUtil {
    private static final int MAX_CONVERSATIONS_PER_USER = 50;
    private static final int MAX_MESSAGES_PER_CONVERSATION = 10000;
    
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        
        if (username.length() < 3 || username.length() > 20) {
            throw new ValidationException("Username must be between 3 and 20 characters");
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("Username can only contain letters, digits, and underscores");
        }
    }
    
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be empty");
        }
        
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }
    
    public static void validateConversationLimit(long currentCount) {
        if (currentCount >= MAX_CONVERSATIONS_PER_USER) {
            throw new ValidationException("Maximum " + MAX_CONVERSATIONS_PER_USER + " conversations allowed per user");
        }
    }
    
    public static void validateMessageLimit(long currentCount) {
        if (currentCount >= MAX_MESSAGES_PER_CONVERSATION) {
            throw new ValidationException("Maximum " + MAX_MESSAGES_PER_CONVERSATION + " messages allowed per conversation");
        }
    }
}

