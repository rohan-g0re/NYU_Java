package com.nyu.aichat.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conv_id", nullable = false)
    private Conversation conversation;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageRole role;
    
    @Column(nullable = false, length = 4000)
    private String content;
    
    @Column(name = "ts")
    private Instant timestamp;
    
    @Column(name = "prev_message_id")
    private Long prevMessageId;
    
    @Column(name = "next_message_id")
    private Long nextMessageId;
    
    // Constructors
    public Message() {
    }
    
    public Message(Conversation conversation, MessageRole role, String content) {
        this.conversation = conversation;
        this.role = role;
        this.content = content;
    }
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Conversation getConversation() {
        return conversation;
    }
    
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
    
    public MessageRole getRole() {
        return role;
    }
    
    public void setRole(MessageRole role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public Long getPrevMessageId() {
        return prevMessageId;
    }
    
    public void setPrevMessageId(Long prevMessageId) {
        this.prevMessageId = prevMessageId;
    }
    
    public Long getNextMessageId() {
        return nextMessageId;
    }
    
    public void setNextMessageId(Long nextMessageId) {
        this.nextMessageId = nextMessageId;
    }
}

