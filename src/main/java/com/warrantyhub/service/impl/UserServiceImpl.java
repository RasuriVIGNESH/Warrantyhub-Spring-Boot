package com.warrantyhub.service.impl;

import com.warrantyhub.dto.request.UserProfileUpdateRequest;
import com.warrantyhub.dto.response.UserProfileDTO;
import com.warrantyhub.model.User;
import com.warrantyhub.model.UserPreferences;
import com.warrantyhub.exception.ResourceNotFoundException;
import com.warrantyhub.repository.UserRepository;
import com.warrantyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserProfileDTO getUserProfile(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(user.getId().toString());
        profileDTO.setName(user.getName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setEmailNotifications(user.getPreferences().isEmailNotifications());
        profileDTO.setWarrantyExpirationReminders(user.getPreferences().getWarrantyExpirationReminders());

        return profileDTO;
    }

    @Override
    public UserProfileDTO updateUserProfile(UserProfileUpdateRequest request, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        // Update user name
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        // Update preferences
        UserPreferences preferences = user.getPreferences();
        preferences.setEmailNotifications(request.isEmailNotifications());
        preferences.setWarrantyExpirationReminders(request.getWarrantyExpirationReminders());

        // Save user
        User updatedUser = userRepository.save(user);

        // Convert to DTO
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setId(updatedUser.getId().toString());
        profileDTO.setName(updatedUser.getName());
        profileDTO.setEmail(updatedUser.getEmail());
        profileDTO.setEmailNotifications(updatedUser.getPreferences().isEmailNotifications());
        profileDTO.setWarrantyExpirationReminders(updatedUser.getPreferences().getWarrantyExpirationReminders());

        return profileDTO;
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
