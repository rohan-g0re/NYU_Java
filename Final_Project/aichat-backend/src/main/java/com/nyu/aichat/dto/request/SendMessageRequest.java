package com.nyu.aichat.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SendMessageRequest {
    @NotBlank(message = "Message text is required")
    @Size(min = 1, max = 4000, message = "Message must be between 1 and 4000 characters")
    private String text;
    
    public SendMessageRequest() {
    }
    
    public SendMessageRequest(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}

