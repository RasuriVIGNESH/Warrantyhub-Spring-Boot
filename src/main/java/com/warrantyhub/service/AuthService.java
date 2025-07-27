package com.warrantyhub.service;

import com.warrantyhub.dto.request.LoginRequest;
import com.warrantyhub.dto.request.PasswordResetConfirmRequest;
import com.warrantyhub.dto.request.RegisterRequest;
import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.AuthResponse;
import com.warrantyhub.dto.response.TokenRefreshResponse;
import com.warrantyhub.dto.response.UserProfileDTO;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(String refreshToken);
    ApiResponse requestPasswordReset(String email);
    ApiResponse resetPassword(String token, String newPassword);
    ApiResponse logout();
    UserProfileDTO getProfile(Authentication authentication);
}
