package com.nyu.aichat.dto.request;

import javax.validation.constraints.Size;

public class CreateConversationRequest {
    @Size(max = 200, message = "Title must be at most 200 characters")
    private String title;  // Optional, defaults to "New Chat"
    
    public CreateConversationRequest() {
    }
    
    public CreateConversationRequest(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}

