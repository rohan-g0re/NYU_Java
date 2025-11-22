package com.nyu.aichat.dto.response;

public class SendMessageResponse {
    private MessageDto assistantMessage;
    
    public SendMessageResponse() {
    }
    
    public SendMessageResponse(MessageDto assistantMessage) {
        this.assistantMessage = assistantMessage;
    }
    
    public MessageDto getAssistantMessage() {
        return assistantMessage;
    }
    
    public void setAssistantMessage(MessageDto assistantMessage) {
        this.assistantMessage = assistantMessage;
    }
}

