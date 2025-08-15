package com.warrantyhub.service.impl;

import com.warrantyhub.model.User;
import com.warrantyhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

/**
 * Service implementation for loading user details for Spring Security.
 * Spring Security to retrieve user information, such as username, password,
 * and authorities, for authentication and authorization.
 */
@Service
public class userDetailsServiceImpl implements UserDetailsService {

    // Assuming a UserRepository exists to interact with the database.
    // This repository should have a method to find a User by their email.
    private final UserRepository userRepository;

    @Autowired
    public userDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email using the UserRepository.
        // We assume the repository returns an Optional<User>.
        Optional<User> userOptional = userRepository.findByEmail(email);

        // Throw an exception if the user is not found.
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));

        // Create a UserDetails object from the found User model.
        // We use the UserDetails interface implementation provided by Spring Security.
        // The UserDetails object contains the user's email as the principal,
        // the hashed password, a list of authorities/roles, and the enabled status.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(), // Use the enabled status from the User model
                true, // account non-expired
                true, // credentials non-expired
                true, // account non-locked
                // Assign a default authority since no specific roles are defined in the User model.
                // You can expand this to load actual roles from the database later.
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
