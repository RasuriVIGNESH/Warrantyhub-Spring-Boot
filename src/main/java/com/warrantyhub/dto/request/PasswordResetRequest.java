package com.warrantyhub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request DTO for initiating password reset")
public class PasswordResetRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address associated with the account", example = "user@example.com")
    private String email;
    
    // No-args constructor
    public PasswordResetRequest() {
    }
    
    // All-args constructor
    public PasswordResetRequest(String email) {
        this.email = email;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}