package com.warrantyhub.service;

import com.warrantyhub.dto.request.UserProfileUpdateRequest;
import com.warrantyhub.dto.response.UserProfileDTO;
import org.springframework.security.core.Authentication;

public interface UserService {
    UserProfileDTO getUserProfile(Authentication authentication);
    UserProfileDTO updateUserProfile(UserProfileUpdateRequest request, Authentication authentication);
}
