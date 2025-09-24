package com.securebank.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for login responses
 */
public class LoginResponse {
    
    private String token;
    private String username;
    private String type = "Bearer";
    private LocalDateTime expiresAt;
    private String role;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
    
    public LoginResponse(String token, String username, String role, LocalDateTime expiresAt) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
                "username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", role='" + role + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}