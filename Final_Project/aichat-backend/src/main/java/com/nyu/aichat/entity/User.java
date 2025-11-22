package com.nyu.aichat.entity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(name = "pass_hash", nullable = false)
    private String passHash;
    
    @Column(name = "created_at")
    private Instant createdAt;
    
    // Constructors
    public User() {
    }
    
    public User(String username, String passHash) {
        this.username = username;
        this.passHash = passHash;
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassHash() {
        return passHash;
    }
    
    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

