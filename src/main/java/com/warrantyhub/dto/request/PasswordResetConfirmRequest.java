package com.warrantyhub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request DTO for password reset confirmation")
public class PasswordResetConfirmRequest {
    @NotBlank(message = "Token is required")
    @Schema(description = "Password reset token received via email", example = "abc123-xyz456")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "New password for the account", example = "newSecurePassword123")
    private String password;
    
    // No-args constructor
    public PasswordResetConfirmRequest() {
    }
    
    // All-args constructor
    public PasswordResetConfirmRequest(String token, String password) {
        this.token = token;
        this.password = password;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}