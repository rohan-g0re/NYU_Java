package com.nyu.aichat.client.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a message in the chat.
 * Maps from backend MessageDto.
 */
public class MessageView {
    private Long id;
    private String role;  // "user" or "assistant"
    private String content;
    private Instant ts;
    
    public MessageView() {
    }
    
    public MessageView(Long id, String role, String content, Instant ts) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.ts = ts;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Instant getTs() {
        return ts;
    }
    
    public void setTs(Instant ts) {
        this.ts = ts;
    }
    
    /**
     * Checks if this is a user message.
     */
    public boolean isUserMessage() {
        return "user".equals(role);
    }
    
    /**
     * Checks if this is an assistant message.
     */
    public boolean isAssistantMessage() {
        return "assistant".equals(role);
    }
    
    /**
     * Formats the timestamp for display.
     * @return Formatted time string (e.g., "10:30 AM")
     */
    public String getFormattedTimestamp() {
        if (ts == null) {
            return "";
        }
        
        LocalDateTime dateTime = LocalDateTime.ofInstant(ts, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return dateTime.format(formatter);
    }
    
    @Override
    public String toString() {
        return "MessageView{id=" + id + ", role='" + role + "', content='" + 
               (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + "'}";
    }
}

