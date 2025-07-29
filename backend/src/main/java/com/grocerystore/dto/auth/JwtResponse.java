package com.grocerystore.dto.auth;

import com.grocerystore.entity.UserRole;

import java.util.UUID;

/**
 * DTO for JWT authentication response.
 * 
 * @author Chirag Singhal
 * @version 1.0.0
 */
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private UUID id;
    private String fullName;
    private String email;
    private UserRole role;
    private long expiresIn;

    // Constructors
    public JwtResponse() {}

    public JwtResponse(String token, UUID id, String fullName, String email, UserRole role, long expiresIn) {
        this.token = token;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "token='[PROTECTED]'" +
                ", type='" + type + '\'' +
                ", id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
