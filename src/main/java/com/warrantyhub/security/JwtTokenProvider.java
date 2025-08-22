package com.warrantyhub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    /**
     * FIXED: Generate JWT token that handles both UserDetails and OAuth2User principals
     */
    public String generateToken(Authentication authentication) {
        try {
            Object principal = authentication.getPrincipal();
            String email;
            String name = null;

            logger.debug("Generating JWT token for principal type: {}", principal.getClass().getSimpleName());

            // Handle different principal types
            if (principal instanceof UserPrincipal) {
                // Custom UserPrincipal (implements both UserDetails and OAuth2User)
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                email = userPrincipal.getEmail();
                name = userPrincipal.getName();
                logger.debug("Processing UserPrincipal: {}", maskEmail(email));

            } else if (principal instanceof OidcUser) {
                // OAuth2/OIDC user (Google login)
                OidcUser oidcUser = (OidcUser) principal;
                email = oidcUser.getEmail();
                name = oidcUser.getFullName();
                logger.debug("Processing OidcUser: {}", maskEmail(email));

            } else if (principal instanceof OAuth2User) {
                // Generic OAuth2 user
                OAuth2User oauth2User = (OAuth2User) principal;
                email = oauth2User.getAttribute("email");
                name = oauth2User.getAttribute("name");
                logger.debug("Processing OAuth2User: {}", maskEmail(email));

            } else if (principal instanceof UserDetails) {
                // Standard UserDetails (email/password login)
                UserDetails userDetails = (UserDetails) principal;
                email = userDetails.getUsername();
                logger.debug("Processing UserDetails: {}", maskEmail(email));

            } else {
                logger.error("Unsupported principal type: {}", principal.getClass());
                throw new RuntimeException("Unsupported principal type: " + principal.getClass());
            }

            // Validate email
            if (email == null || email.trim().isEmpty()) {
                logger.error("Email is null or empty for principal: {}", principal.getClass());
                throw new RuntimeException("User email cannot be null or empty");
            }

            // Generate token
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            String token = Jwts.builder()
                    .setSubject(email.trim())
                    .claim("email", email.trim())
                    .claim("name", name) // Include name if available
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();

            logger.info("JWT token generated successfully for user: {}", maskEmail(email));
            return token;

        } catch (Exception e) {
            logger.error("Error generating JWT token from authentication: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    // Generate token from username (for refresh token etc.)
    public String generateTokenFromUsername(String username) {
        try {
            // IMPROVED: Input validation
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be null or empty");
            }

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

            String token = Jwts.builder()
                    .setSubject(username.trim())
                    .claim("email", username.trim()) // 👈 ensure email is always present
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();

            logger.debug("JWT token generated successfully from username: {}", maskEmail(username));
            return token;

        } catch (Exception e) {
            logger.error("Error generating JWT token from username '{}': {}", maskEmail(username), e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token from username", e);
        }
    }

    // Get username (email) from JWT token
    public String getUsernameFromJWT(String token) {
        try {
            // IMPROVED: Input validation
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token.trim())
                    .getBody();

            // ✅ Prefer email claim if present, fallback to subject
            String email = claims.get("email", String.class);
            if (email != null && !email.trim().isEmpty()) {
                return email.trim();
            }

            String subject = claims.getSubject();
            if (subject == null || subject.trim().isEmpty()) {
                logger.error("JWT token has no valid subject or email claim");
                throw new RuntimeException("Invalid JWT token: no valid subject or email");
            }

            return subject.trim();

        } catch (ExpiredJwtException e) {
            logger.debug("JWT token is expired");
            throw new RuntimeException("JWT token is expired", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token format");
            throw new RuntimeException("Invalid JWT token format", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token");
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT token claims string is empty or invalid: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token claims", e);
        } catch (Exception e) {
            logger.error("Error extracting username from JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to extract username from JWT token", e);
        }
    }

    // Validate JWT token
    public boolean validateToken(String authToken) {
        try {
            // IMPROVED: Input validation
            if (authToken == null || authToken.trim().isEmpty()) {
                logger.debug("JWT token is null or empty");
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken.trim());

            logger.debug("JWT token validated successfully");
            return true;

        } catch (MalformedJwtException ex) {
            logger.debug("Invalid JWT token format: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.debug("Expired JWT token: expires at {}", ex.getClaims().getExpiration());
        } catch (UnsupportedJwtException ex) {
            logger.debug("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.debug("JWT claims string is empty or invalid: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error validating JWT token: {}", ex.getMessage());
        }

        return false;
    }

    // Build Authentication from JWT
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                            (claims.get("roles", String.class) != null ? claims.get("roles", String.class) : "")
                                    .split(","))
                    .filter(role -> !role.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // IMPROVED: Add default role if no roles specified
            if (authorities.isEmpty()) {
                authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
            }

            String username = getUsernameFromJWT(token);
            User principal = new User(username, "", authorities);

            logger.debug("Authentication object created for user: {}", maskEmail(username));
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);

        } catch (Exception e) {
            logger.error("Error creating authentication from JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create authentication from JWT token", e);
        }
    }

    /**
     * IMPROVED: Get expiration date from token for frontend usage
     */
    public Date getExpirationDateFromJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("Error extracting expiration date from JWT token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * IMPROVED: Check if token is expired without throwing exception
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromJWT(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            return true; // Consider invalid tokens as expired
        }
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Error creating signing key from JWT secret: {}", e.getMessage());
            throw new RuntimeException("Failed to create JWT signing key", e);
        }
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