package com.warrantyhub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing user data and token")
public class AuthResponse {
    @Schema(description = "Indicates if authentication was successful", example = "true")
    private boolean success;

    @Schema(description = "Authenticated user information")
    private UserDTO user;

    @Schema(description = "JWT access token for authenticated requests", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    // No-args constructor
    public AuthResponse() {
    }
    
    // All-args constructor
    public AuthResponse(boolean success, UserDTO user, String token) {
        this.success = success;
        this.user = user;
        this.token = token;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public UserDTO getUser() {
        return user;
    }
    
    public void setUser(UserDTO user) {
        this.user = user;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}