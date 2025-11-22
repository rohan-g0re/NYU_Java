package com.nyu.aichat.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    @Column(name = "head_message_id")
    private Long headMessageId;
    
    @Column(name = "last_message_id")
    private Long lastMessageId;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    // Constructors
    public Conversation() {
        // Field initialization handles isDeleted default
    }
    
    public Conversation(User user, String title) {
        this.user = user;
        this.title = title;
        // Field initialization handles isDeleted default
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        // Field initialization handles isDeleted default - no need to check here
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public Long getHeadMessageId() {
        return headMessageId;
    }
    
    public void setHeadMessageId(Long headMessageId) {
        this.headMessageId = headMessageId;
    }
    
    public Long getLastMessageId() {
        return lastMessageId;
    }
    
    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}

