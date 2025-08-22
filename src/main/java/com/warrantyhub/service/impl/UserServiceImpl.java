package com.warrantyhub.service.impl;

import com.warrantyhub.dto.request.UserProfileUpdateRequest;
import com.warrantyhub.dto.response.UserProfileDTO;
import com.warrantyhub.exception.ResourceNotFoundException;
import com.warrantyhub.model.Provider;
import com.warrantyhub.model.User;
import com.warrantyhub.model.UserPreferences;
import com.warrantyhub.repository.UserRepository;
import com.warrantyhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findUserByEmailOptional(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.warn("findUserByEmailOptional called with null or empty email");
            return Optional.empty();
        }

        return userRepository.findByEmail(email.trim());
    }

    @Override
    public void saveUser(User user) {
        try {
            if (user == null) {
                logger.error("Attempt to save null user");
                throw new IllegalArgumentException("User cannot be null");
            }

            if (user.getPreferences() == null) {
                logger.debug("Creating default preferences for user: {}", maskEmail(user.getEmail()));
                user.setPreferences(new UserPreferences());
            }

            userRepository.save(user);
            logger.debug("User saved successfully: {}", maskEmail(user.getEmail()));

        } catch (Exception e) {
            logger.error("Error saving user {}: {}", maskEmail(user != null ? user.getEmail() : "null"), e.getMessage(), e);
            throw e;
        }
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

        logger.debug("Profile retrieved for user: {}", maskEmail(user.getEmail()));
        return profileDTO;
    }

    @Override
    public UserProfileDTO updateUserProfile(UserProfileUpdateRequest request, Authentication authentication) {
        try {
            User user = getUserFromAuthentication(authentication);

            boolean hasChanges = false;

            if (request.getName() != null && !request.getName().isEmpty() && !request.getName().equals(user.getName())) {
                user.setName(request.getName());
                hasChanges = true;
            }

            UserPreferences preferences = user.getPreferences();
            if (preferences.isEmailNotifications() != request.isEmailNotifications()) {
                preferences.setEmailNotifications(request.isEmailNotifications());
                hasChanges = true;
            }

            if (preferences.getWarrantyExpirationReminders() != request.getWarrantyExpirationReminders()) {
                preferences.setWarrantyExpirationReminders(request.getWarrantyExpirationReminders());
                hasChanges = true;
            }

            User updatedUser = userRepository.save(user);

            if (hasChanges) {
                logger.info("Profile updated for user: {}", maskEmail(user.getEmail()));
            } else {
                logger.debug("Profile update request with no changes for user: {}", maskEmail(user.getEmail()));
            }

            UserProfileDTO profileDTO = new UserProfileDTO();
            profileDTO.setId(updatedUser.getId().toString());
            profileDTO.setName(updatedUser.getName());
            profileDTO.setEmail(updatedUser.getEmail());
            profileDTO.setEmailNotifications(updatedUser.getPreferences().isEmailNotifications());
            profileDTO.setWarrantyExpirationReminders(updatedUser.getPreferences().getWarrantyExpirationReminders());

            return profileDTO;

        } catch (Exception e) {
            logger.error("Error updating user profile: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User findOrCreateOAuth2User(String email, String name, Provider provider, String providerId) {
        try {
            // IMPROVED: Input validation
            if (email == null || email.trim().isEmpty()) {
                logger.error("findOrCreateOAuth2User called with null or empty email");
                throw new IllegalArgumentException("Email cannot be null or empty");
            }

            if (providerId == null || providerId.trim().isEmpty()) {
                logger.error("findOrCreateOAuth2User called with null or empty providerId for email: {}", maskEmail(email));
                throw new IllegalArgumentException("Provider ID cannot be null or empty");
            }

            String normalizedEmail = email.trim().toLowerCase();
            Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                boolean needsUpdate = false;

                // IMPROVED: Update provider info if changed
                if (user.getProvider() == null || !user.getProvider().equals(provider)) {
                    logger.info("Updating provider for existing user {} from {} to {}",
                            maskEmail(email), user.getProvider(), provider);
                    user.setProvider(provider);
                    needsUpdate = true;
                }

                if (user.getProviderId() == null || !user.getProviderId().equals(providerId)) {
                    logger.debug("Updating provider ID for existing user: {}", maskEmail(email));
                    user.setProviderId(providerId);
                    needsUpdate = true;
                }

                // Update name if it was null or empty and we have a valid name now
                if ((user.getName() == null || user.getName().trim().isEmpty()) &&
                        name != null && !name.trim().isEmpty()) {
                    user.setName(name.trim());
                    needsUpdate = true;
                    logger.debug("Updated name for existing OAuth2 user: {}", maskEmail(email));
                }

                if (needsUpdate) {
                    user = userRepository.save(user);
                    logger.info("Updated existing OAuth2 user: {} with provider: {}", maskEmail(email), provider);
                }

                return user;

            } else {
                // IMPROVED: Create new OAuth2 user with better defaults
                User newUser = new User();
                newUser.setName(name != null && !name.trim().isEmpty() ? name.trim() : normalizedEmail.split("@")[0]);
                newUser.setEmail(normalizedEmail);
                newUser.setProvider(provider);
                newUser.setProviderId(providerId.trim());
                newUser.setEnabled(true);

                // Create default preferences
                UserPreferences preferences = new UserPreferences();
                preferences.setEmailNotifications(true);
                preferences.setWarrantyExpirationReminders(30);
                newUser.setPreferences(preferences);

                User savedUser = userRepository.save(newUser);
                logger.info("Created new OAuth2 user: {} with provider: {} (ID: {})",
                        maskEmail(email), provider, savedUser.getId());

                return savedUser;
            }

        } catch (Exception e) {
            logger.error("Error in findOrCreateOAuth2User for email {}, provider {}: {}",
                    maskEmail(email), provider, e.getMessage(), e);
            throw new RuntimeException("Failed to find or create OAuth2 user", e);
        }
    }

    /**
     * IMPROVED: Enhanced UserDetailsService implementation with better error handling
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // IMPROVED: Input validation with detailed logging
            if (email == null || email.trim().isEmpty()) {
                logger.error("loadUserByUsername called with null or empty email");
                throw new UsernameNotFoundException("Email cannot be null or empty");
            }

            String normalizedEmail = email.trim();
            logger.debug("Loading user details for email: {}", maskEmail(normalizedEmail));

            // Find user by email
            User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> {
                        logger.warn("User not found with email: {}", maskEmail(normalizedEmail));
                        return new UsernameNotFoundException("User not found with email: " + maskEmail(normalizedEmail));
                    });

            // IMPROVED: Database integrity validation
            String username = user.getEmail();
            if (username == null || username.trim().isEmpty()) {
                logger.error("User email is null or empty in database for user ID: {}", user.getId());
                throw new UsernameNotFoundException("User email is null or empty in database");
            }

            // Handle password properly - OAuth2 users might not have passwords
            String password = user.getPassword();
            if (password == null || password.trim().isEmpty()) {
                logger.debug("User {} has no password (OAuth2 user), using placeholder", maskEmail(username));
                password = "{noop}OAUTH_USER";
            }

            // Ensure authorities are never null or empty
            Collection<? extends GrantedAuthority> authorities = getAuthorities();

            boolean enabled = Boolean.TRUE.equals(user.isEnabled());

            logger.debug("UserDetails loaded successfully for user: {} (enabled: {})",
                    maskEmail(username), enabled);

            // Build the UserDetails object with proper null checks
            return org.springframework.security.core.userdetails.User.builder()
                    .username(username.trim())
                    .password(password)
                    .authorities(authorities)
                    .disabled(!enabled)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .build();

        } catch (UsernameNotFoundException e) {
            // Re-throw username not found exceptions
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error loading user details for email {}: {}", maskEmail(email), e.getMessage(), e);
            throw new UsernameNotFoundException("Error loading user details", e);
        }
    }

    /**
     * Helper method to get default authorities for authenticated users
     */
    private Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        return userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + maskEmail(email)));
    }

    private User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            logger.error("Authentication object or principal name is null");
            throw new ResourceNotFoundException("Authentication object or principal name is null.");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email.trim())
                .orElseThrow(() -> {
                    logger.error("User not found for authenticated principal: {}", maskEmail(email));
                    return new ResourceNotFoundException("User not found for authenticated principal: " + maskEmail(email));
                });
    }

    /**
     * ADDED: Utility method to mask email for secure logging
     */
    private String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return "***";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email.substring(0, 1) + "***";
        }

        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***" + domain;
        } else {
            return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + domain;
        }
    }
}