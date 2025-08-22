package com.warrantyhub.security;

import com.warrantyhub.exception.OAuth2AuthenticationProcessingException;
import com.warrantyhub.model.Provider;
import com.warrantyhub.model.User;
import com.warrantyhub.security.oauth2.user.OAuth2UserInfo;
import com.warrantyhub.security.oauth2.user.OAuth2UserInfoFactory;
import com.warrantyhub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        logger.info("🔍 CustomOAuth2UserService.loadUser() called for provider: {}", registrationId);

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        logger.info("🔍 OAuth2User loaded from provider, attributes: {}", oAuth2User.getAttributes().keySet());

        try {
            OAuth2User processedUser = processOAuth2User(oAuth2UserRequest, oAuth2User);
            logger.info("✅ OAuth2User processed successfully");
            return processedUser;
        } catch (AuthenticationException ex) {
            logger.error("❌ Authentication exception in OAuth2 processing: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("❌ Unexpected error during OAuth2 user processing", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        logger.info("🔍 Processing OAuth2 user for provider: {}", registrationId);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            logger.error("❌ Email not found from OAuth2 provider: {}", registrationId);
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        String providerId = oAuth2UserInfo.getId();

        logger.info("🔍 OAuth2 user info extracted - Email: {}, Name: {}, Provider ID: {}",
                maskEmail(email), name, providerId);

        User user;
        try {
            logger.info("🔍 Finding or creating OAuth2 user in database...");

            user = userService.findOrCreateOAuth2User(
                    email,
                    name,
                    Provider.valueOf(registrationId.toUpperCase()),
                    providerId
            );

            logger.info("✅ OAuth2 user successfully processed in database - ID: {}, Email: {}",
                    user.getId(), maskEmail(user.getEmail()));

        } catch (Exception e) {
            logger.error("❌ Error processing OAuth2 user with email: {}, error: {}",
                    maskEmail(email), e.getMessage(), e);
            throw new OAuth2AuthenticationProcessingException("Error processing OAuth2 user", e);
        }

        UserPrincipal userPrincipal = UserPrincipal.create(user, oAuth2User.getAttributes());
        logger.info("✅ UserPrincipal created successfully for OAuth2 user: {}", maskEmail(email));

        return userPrincipal;
    }

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