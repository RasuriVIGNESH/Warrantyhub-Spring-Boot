package com.warrantyhub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Extended user profile information")
public class UserProfileDTO {
    @Schema(description = "Unique identifier of the user", example = "65a8f4e3b8d1c12e3f4a5b6f")
    private String id;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Whether email notifications are enabled", example = "true")
    private boolean emailNotifications;

    @Schema(description = "Days before warranty expiration to send reminders", example = "7")
    private int warrantyExpirationReminders;
    
    // No-args constructor
    public UserProfileDTO() {
    }
    
    // All-args constructor
    public UserProfileDTO(String id, String name, String email, boolean emailNotifications, int warrantyExpirationReminders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.emailNotifications = emailNotifications;
        this.warrantyExpirationReminders = warrantyExpirationReminders;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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