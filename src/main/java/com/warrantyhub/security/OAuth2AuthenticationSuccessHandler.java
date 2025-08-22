package com.warrantyhub.security;

import com.warrantyhub.model.RefreshToken;
import com.warrantyhub.model.User;
import com.warrantyhub.repository.RefreshTokenRepository;
import com.warrantyhub.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Value("${app.oauth2.defaultFrontendUrl}")
    private String defaultFrontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String referer = request.getHeader("Referer");
        logger.info("OAuth2 success handler called. Referer: {}", referer);

        // Check if request is from Swagger UI
        if (referer != null && referer.contains("swagger-ui")) {
            handleSwaggerSuccess(request, response, authentication);
        } else {
            handleReactSuccess(request, response, authentication);
        }
    }

    private void handleSwaggerSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        try {
            // Generate JWT token for Swagger
            String token = tokenProvider.generateToken(authentication);

            // Extract user information from OAuth2User
            String email = extractEmail(authentication);
            String name = extractName(authentication);
            Long userId = getUserIdFromAuthentication(authentication);

            // Create response body with token info that Swagger UI can use
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("access_token", token);  // This is the JWT token
            tokenInfo.put("token_type", "Bearer");
            tokenInfo.put("expires_in", 86400); // 24 hours
            tokenInfo.put("user", Map.of(
                    "id", userId != null ? userId : "unknown",
                    "name", name != null ? name : "Unknown User",
                    "email", email != null ? email : "unknown@example.com"
            ));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);

            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(tokenInfo);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            logger.info("✅ Swagger OAuth2 success: JWT token generated for user: {}", maskEmail(email));
            logger.debug("JWT Token for Swagger: {}", token.substring(0, Math.min(50, token.length())) + "...");

        } catch (Exception e) {
            logger.error("❌ Error handling Swagger OAuth2 success: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Token generation failed\",\"message\":\"" + e.getMessage() + "\"}");
            response.getWriter().flush();
        }
    }

    private void handleReactSuccess(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        try {
            // Generate JWT token for OAuth2 user
            String token = tokenProvider.generateToken(authentication);
            logger.info("✅ JWT token generated successfully for OAuth2 user");

            // Extract user information from OAuth2User/OidcUser
            String email = extractEmail(authentication);
            logger.info("🔍 Extracted email from OAuth2 authentication: {}", maskEmail(email));

            // Create refresh token - this now handles user creation if needed
            String refreshToken = createRefreshTokenForOAuth2User(authentication);
            logger.info("✅ Refresh token created successfully for OAuth2 user");

            // Build redirect URL with tokens
            return UriComponentsBuilder.fromUriString(defaultFrontendUrl + "/oauth2/callback")
                    .queryParam("token", token)
                    .queryParam("refreshToken", refreshToken)
                    .queryParam("success", "true")
                    .build().toUriString();

        } catch (Exception e) {
            logger.error("Error generating tokens for OAuth2 success: {}", e.getMessage(), e);
            return UriComponentsBuilder.fromUriString(defaultFrontendUrl + "/login")
                    .queryParam("error", "token_generation_failed")
                    .queryParam("message", e.getMessage())
                    .build().toUriString();
        }
    }

    /**
     * Extract email from different OAuth2User types
     */
    private String extractEmail(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getEmail();
        } else if (principal instanceof OidcUser) {
            return ((OidcUser) principal).getEmail();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("email");
        }

        logger.warn("Unable to extract email from principal type: {}", principal.getClass());
        return null;
    }

    /**
     * Extract name from different OAuth2User types
     */
    private String extractName(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getName();
        } else if (principal instanceof OidcUser) {
            return ((OidcUser) principal).getFullName();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("name");
        }

        logger.warn("Unable to extract name from principal type: {}", principal.getClass());
        return null;
    }

    /**
     * Extract provider ID from OAuth2User
     */
    private String extractProviderId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return null; // Not needed for UserPrincipal
        } else if (principal instanceof OidcUser) {
            return ((OidcUser) principal).getSubject();
        } else if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getAttribute("sub");
        }

        return null;
    }

    /**
     * Get user ID from authentication (may not be available for OAuth2 users)
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getId();
        }

        // For OAuth2 users, we need to find the user in the database
        String email = extractEmail(authentication);
        if (email != null) {
            try {
                User user = userService.findByEmail(email);
                return user.getId();
            } catch (Exception e) {
                logger.debug("Could not find user ID for OAuth2 user: {}", maskEmail(email));
            }
        }

        return null;
    }

    /**
     * FIXED: Create or update refresh token to avoid database constraint violations
     */
    @Transactional
    private String createRefreshTokenForOAuth2User(Authentication authentication) {
        try {
            String email = extractEmail(authentication);
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }

            User user;
            try {
                // Try to find existing user
                user = userService.findByEmail(email.trim());
                logger.info("✅ Found existing user in database: {}", maskEmail(email));
            } catch (Exception e) {
                // User doesn't exist - create them
                logger.info("🔍 User not found in database, creating new OAuth2 user: {}", maskEmail(email));

                String name = extractName(authentication);
                String providerId = extractProviderId(authentication);

                user = userService.findOrCreateOAuth2User(
                        email.trim(),
                        name,
                        com.warrantyhub.model.Provider.GOOGLE,
                        providerId
                );

                logger.info("✅ Created new OAuth2 user in database: {} (ID: {})", maskEmail(email), user.getId());
            }

            // FIXED: Find existing refresh token or create new one to avoid constraint violation
            RefreshToken refreshToken;
            Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByUser(user);

            if (existingTokenOptional.isPresent()) {
                // Update existing refresh token
                refreshToken = existingTokenOptional.get();
                logger.debug("📝 Updating existing refresh token for user: {}", maskEmail(email));
            } else {
                // Create new refresh token
                refreshToken = new RefreshToken();
                refreshToken.setUser(user);
                logger.debug("🆕 Creating new refresh token for user: {}", maskEmail(email));
            }

            // Set/update token data (same for both new and existing tokens)
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days

            // Save the token (will either INSERT new or UPDATE existing)
            RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

            logger.info("✅ Refresh token saved successfully for OAuth2 user: {}", maskEmail(email));
            return savedToken.getToken();

        } catch (DataIntegrityViolationException e) {
            // Handle any remaining constraint violations gracefully
            logger.error("❌ Database constraint violation for refresh token: {}", e.getMessage());
            throw new RuntimeException("Database constraint violation while creating refresh token", e);
        } catch (Exception e) {
            logger.error("❌ Error creating/updating refresh token for OAuth2 user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create refresh token", e);
        }
    }

    /**
     * Utility method to mask email for secure logging
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