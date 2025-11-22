package com.nyu.aichat.exception;

public class ConversationNotFoundException extends ApiException {
    public ConversationNotFoundException(String message) {
        super("CONVERSATION_NOT_FOUND", message);
    }
}

