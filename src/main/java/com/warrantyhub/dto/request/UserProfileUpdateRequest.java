package com.warrantyhub.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for updating user profile")
public class UserProfileUpdateRequest {
    @Schema(description = "User's full name", example = "John Smith")
    private String name;

    @Schema(description = "Enable/disable email notifications", example = "true")
    private boolean emailNotifications;

    @Schema(description = "Days before warranty expiration to send reminders", example = "7")
    private int warrantyExpirationReminders;
    
    // No-args constructor
    public UserProfileUpdateRequest() {
    }
    
    // All-args constructor
    public UserProfileUpdateRequest(String name, boolean emailNotifications, int warrantyExpirationReminders) {
        this.name = name;
        this.emailNotifications = emailNotifications;
        this.warrantyExpirationReminders = warrantyExpirationReminders;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public int getWarrantyExpirationReminders() {
        return warrantyExpirationReminders;
    }
    
    public void setWarrantyExpirationReminders(int warrantyExpirationReminders) {
        this.warrantyExpirationReminders = warrantyExpirationReminders;
    }
}