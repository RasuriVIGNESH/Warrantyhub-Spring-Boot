package com.warrantyhub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Basic user information")
public class UserDTO {
    @Schema(description = "Unique identifier of the user", example = "65a8f4e3b8d1c12e3f4a5b6f")
    private String id;

    @Schema(description = "Full name of the user", example = "John Doe")
    private String name;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;
    
    // No-args constructor
    public UserDTO() {
    }
    
    // All-args constructor
    public UserDTO(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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
}