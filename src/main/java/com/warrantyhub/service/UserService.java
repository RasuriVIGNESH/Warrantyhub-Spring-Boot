package com.warrantyhub.service;

import com.warrantyhub.dto.request.UserProfileUpdateRequest;
import com.warrantyhub.dto.response.UserProfileDTO;
import com.warrantyhub.model.Provider;
import com.warrantyhub.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional; // <-- ADDED IMPORT

public interface UserService {
    UserProfileDTO getUserProfile(Authentication authentication);
    UserProfileDTO updateUserProfile(UserProfileUpdateRequest request, Authentication authentication);
    User findOrCreateOAuth2User(String email, String name, Provider provider, String providerId);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User findByEmail(String email);

    // --- ADDED METHODS ---
    Optional<User> findUserByEmailOptional(String email);
    void saveUser(User user);
}