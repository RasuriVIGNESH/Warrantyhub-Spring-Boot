package com.warrantyhub.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    private String resetPasswordToken;

    private Instant resetPasswordTokenExpiry;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Device> devices = new ArrayList<>();

    @Embedded
    private UserPreferences preferences = new UserPreferences();

    // ADD THIS LINE
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    // OAuth2 specific fields
    @Column(name = "provider")
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    // No-args constructor
    public User() {
    }

    // All-args constructor
    public User(Long id, String name, String email, String password, String resetPasswordToken,
                Instant resetPasswordTokenExpiry, List<Device> devices, UserPreferences preferences,
                boolean enabled, Provider provider, String providerId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.resetPasswordToken = resetPasswordToken;
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
        this.devices = devices;
        this.preferences = preferences;
        this.enabled = enabled;
        this.provider = provider;
        this.providerId = providerId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public Instant getResetPasswordTokenExpiry() {
        return resetPasswordTokenExpiry;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setResetPasswordTokenExpiry(Instant resetPasswordTokenExpiry) {
        this.resetPasswordTokenExpiry = resetPasswordTokenExpiry;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}