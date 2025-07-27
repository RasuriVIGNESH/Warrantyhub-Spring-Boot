package com.warrantyhub.controller;

import com.warrantyhub.dto.request.UserProfileUpdateRequest;
import com.warrantyhub.dto.response.UserProfileDTO;
import com.warrantyhub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users" )
@Tag(name = "User Profile", description = "Operations for managing user profile")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(
            summary = "Get user profile",
            description = "Retrieves the profile information of the authenticated user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserProfileDTO.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token",
            content = @Content(schema = @Schema(implementation = com.warrantyhub.dto.response.ApiResponse.class))
    )
    public ResponseEntity<UserProfileDTO> getUserProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getUserProfile(authentication));
    }

    @PutMapping("/profile")
    @Operation(
            summary = "Update user profile",
            description = "Updates the profile information of the authenticated user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User profile updated successfully",
            content = @Content(schema = @Schema(implementation = UserProfileDTO.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid profile data",
            content = @Content(schema = @Schema(implementation = com.warrantyhub.dto.response.ApiResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token",
            content = @Content(schema = @Schema(implementation = com.warrantyhub.dto.response.ApiResponse.class))
    )
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateUserProfile(request, authentication));
    }
}
