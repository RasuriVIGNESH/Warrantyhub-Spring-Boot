package com.warrantyhub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic API response structure")
public class ApiResponse {
    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "Message describing the operation result", example = "Operation completed successfully")
    private String message;
    
    // No-args constructor
    public ApiResponse() {
    }
    
    // All-args constructor
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}