package com.warrantyhub.service.impl;

import com.warrantyhub.dto.request.UserProfileUpdateRequest;
import com.warrantyhub.dto.response.UserProfileDTO;
import com.warrantyhub.exception.ResourceNotFoundException;
import com.warrantyhub.model.Provider;
import com.warrantyhub.model.User;
import com.warrantyhub.model.UserPreferences;
import com.warrantyhub.repository.UserRepository;
import com.warrantyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // --- NEW METHOD ---
    @Override
    public Optional<User> findUserByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }

    // --- NEW METHOD ---
    @Override
    public void saveUser(User user) {
        // Ensure preferences are not null for new users
        if (user.getPreferences() == null) {
            user.setPreferences(new UserPreferences());
        }
        userRepository.save(user);
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

    @Override
    public User findOrCreateOAuth2User(String email, String name, Provider provider, String providerId) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        User user = null;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (user.getProvider() == null) {
                // --- FIX #1 ---
                user.setProvider(provider);
                user.setProviderId(providerId);
                return userRepository.save(user);
            }
            return user;
        } else {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            // --- FIX #2 ---
            user.setProvider(provider);
            newUser.setProviderId(providerId);
            newUser.setEnabled(true);
            newUser.setPreferences(new UserPreferences());
            return userRepository.save(newUser);
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}