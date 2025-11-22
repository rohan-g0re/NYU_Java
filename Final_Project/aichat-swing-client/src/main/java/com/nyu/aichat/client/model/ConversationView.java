package com.nyu.aichat.client.model;

import java.time.Instant;

/**
 * Represents a conversation in the UI.
 * Maps from backend ConversationDto.
 */
public class ConversationView {
    private Long id;
    private String title;
    private Instant createdAt;
    
    public ConversationView() {
    }
    
    public ConversationView(Long id, String title, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Formats the creation date for display in the UI.
     * @return Formatted date string (e.g., "2h ago", "Yesterday", "Jan 15")
     */
    public String getFormattedDate() {
        if (createdAt == null) {
            return "";
        }
        
        Instant now = Instant.now();
        long diffSeconds = now.getEpochSecond() - createdAt.getEpochSecond();
        
        if (diffSeconds < 60) {
            return "Just now";
        } else if (diffSeconds < 3600) {
            long minutes = diffSeconds / 60;
            return minutes + "m ago";
        } else if (diffSeconds < 86400) {
            long hours = diffSeconds / 3600;
            return hours + "h ago";
        } else if (diffSeconds < 604800) {
            long days = diffSeconds / 86400;
            return days + "d ago";
        } else {
            // Format as date: "Jan 15" or "2024-01-15"
            return createdAt.toString().substring(0, 10); // Simple format
        }
    }
    
    @Override
    public String toString() {
        return title != null ? title : "Untitled";
    }
}

