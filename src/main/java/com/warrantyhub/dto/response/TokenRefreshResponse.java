package com.warrantyhub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token refresh response containing new access and refresh tokens")
public class TokenRefreshResponse {
    @Schema(description = "Indicates if token refresh was successful", example = "true")
    private boolean success;

    @Schema(description = "New JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "New refresh token", example = "abc123-xyz456")
    private String refreshToken;
    
    // No-args constructor
    public TokenRefreshResponse() {
    }
    
    // All-args constructor
    public TokenRefreshResponse(boolean success, String token, String refreshToken) {
        this.success = success;
        this.token = token;
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}