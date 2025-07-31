package com.warrantyhub.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrantyhub.model.Provider;
import com.warrantyhub.model.User;
import com.warrantyhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional; // <-- ADD IMPORT

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // --- START: MODIFICATION ---

        // Use Optional to safely handle user lookup
        Optional<User> userOptional = userService.findUserByEmailOptional(email);

        User user;
        if (userOptional.isEmpty()) {
            // 1. User does not exist: Create a new user (Just-In-Time Provisioning)
            user = new User();
            user.setEmail(email);
            user.setName(oAuth2User.getAttribute("name"));
            user.setProvider(Provider.GOOGLE);// Or determine provider dynamically
            // Set any other default fields you require
            userService.saveUser(user); // Assumes you have a save method in your service
        } else {
            // 2. User exists: Update their details (optional but good practice)
            user = userOptional.get();
            user.setName(oAuth2User.getAttribute("name")); // Update name in case it changed
            // You can also update other attributes like profile picture here
            userService.saveUser(user);
        }

        // --- END: MODIFICATION ---

        // OPTIONAL: Generate JWT token for SPA, etc.
        // String token = jwtTokenProvider.generateToken(user);


        // Write user info to response as JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), user);
    }
}