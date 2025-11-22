package com.nyu.aichat.client.model;

import java.time.Instant;

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
}

