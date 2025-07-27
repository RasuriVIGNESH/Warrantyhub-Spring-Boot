package com.warrantyhub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request DTO for refreshing authentication token")
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token for obtaining new access token", example = "abc123-xyz456")
    private String refreshToken;
    
    // No-args constructor
    public TokenRefreshRequest() {
    }
    
    // All-args constructor
    public TokenRefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}