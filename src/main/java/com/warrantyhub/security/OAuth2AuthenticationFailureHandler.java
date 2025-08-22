package com.warrantyhub.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    @Value("${app.oauth2.defaultFrontendUrl}")
    private String defaultFrontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String referer = request.getHeader("Referer");
        logger.error("OAuth2 authentication failure. Referer: {}, Error: {}", referer, exception.getMessage());

        // Check if request is from Swagger UI
        if (referer != null && referer.contains("swagger-ui")) {
            handleSwaggerFailure(request, response, exception);
        } else {
            handleReactFailure(request, response, exception);
        }
    }

    private void handleSwaggerFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
        logger.error("❌ Swagger OAuth2 authentication failed: {}", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format(
                "{\"error\":\"oauth2_authentication_failed\",\"message\":\"%s\"}",
                exception.getMessage().replace("\"", "\\\"")
        );

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    private void handleReactFailure(HttpServletRequest request, HttpServletResponse response,
                                    AuthenticationException exception) throws IOException {
        String targetUrl = UriComponentsBuilder.fromUriString(defaultFrontendUrl + "/login")
                .queryParam("error", "oauth2_authentication_failed")
                .queryParam("message", exception.getMessage())
                .build().toUriString();

        logger.info("Redirecting to frontend with OAuth2 failure: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}