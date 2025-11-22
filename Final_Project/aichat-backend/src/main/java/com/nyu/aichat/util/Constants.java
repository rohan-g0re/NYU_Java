package com.nyu.aichat.util;

/**
 * Centralized constants to avoid magic strings and improve maintainability.
 * All constants are final and the class cannot be instantiated.
 */
public final class Constants {
    private Constants() {
        // Prevent instantiation
    }
    
    // Role strings
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    
    // Error messages
    public static final String ERROR_CONVERSATION_NOT_FOUND = "Conversation not found";
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_AI_FALLBACK = "I'm sorry, I couldn't generate a response.";
    public static final String ERROR_USERNAME_EXISTS = "Username already exists";
    public static final String ERROR_INVALID_CREDENTIALS = "Username or password is incorrect";
    public static final String ERROR_UNAUTHORIZED_CONVERSATION = "You do not have access to this conversation";
    public static final String ERROR_USER_ID_NULL = "User ID cannot be null";
    public static final String ERROR_CONVERSATION_ID_NULL = "Conversation ID cannot be null";
    public static final String ERROR_MESSAGE_TEXT_NULL = "Message text cannot be null";
    public static final String ERROR_PREVIOUS_MESSAGE_NOT_FOUND = "Previous message not found";
    
    // Titles
    public static final String DEFAULT_TITLE_NEW = "New Chat";
    public static final String DEFAULT_TITLE_UNTITLED = "Untitled Chat";
}

