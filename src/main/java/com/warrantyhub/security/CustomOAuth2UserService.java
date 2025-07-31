package com.warrantyhub.security;

import com.warrantyhub.model.Provider; // <-- ADD IMPORT
import com.warrantyhub.model.User;
import com.warrantyhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // --- Add a logger instance ---
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // --- DEBUGGING: Log the user's name and all attributes ---
        logger.info("OAuth2User Name from .getName(): {}", oAuth2User.getName());
        logger.info("OAuth2User Attributes: {}", oAuth2User.getAttributes());

        String providerString = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(providerString.toUpperCase());

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = oAuth2User.getName();

        User user = userService.findOrCreateOAuth2User(email, name, provider, providerId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes, "email");
    }
}