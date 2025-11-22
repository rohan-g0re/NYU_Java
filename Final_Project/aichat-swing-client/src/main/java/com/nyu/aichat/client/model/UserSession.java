package com.nyu.aichat.client.model;

/**
 * Represents the current user session after successful login/signup.
 * Immutable class storing userId and username.
 */
public class UserSession {
    private final Long userId;
    private final String username;
    
    public UserSession(Long userId, String username) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        this.userId = userId;
        this.username = username;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return "UserSession{userId=" + userId + ", username='" + username + "'}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return userId.equals(that.userId) && username.equals(that.username);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode() * 31 + username.hashCode();
    }
}

