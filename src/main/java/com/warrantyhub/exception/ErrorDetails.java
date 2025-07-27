package com.warrantyhub.exception;

import java.util.Date;
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;
    private int status;
    
    // No-args constructor
    public ErrorDetails() {
    }
    
    // All-args constructor
    public ErrorDetails(Date timestamp, String message, String details, int status) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.status = status;
    }
    
    // Getters and Setters
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
}
