package com.warrantyhub.service.impl;

import com.warrantyhub.model.Device;
import com.warrantyhub.model.MaintenanceRecord;
import com.warrantyhub.model.User;
import com.warrantyhub.service.EmailService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendWelcomeEmail(User user) {
        // Stub implementation for development
        System.out.println("Sending welcome email to: " + user.getEmail());
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        // Stub implementation for development
        System.out.println("Sending password reset email to: " + user.getEmail() + " with token: " + resetToken);
    }

    @Override
    public void sendWarrantyExpirationReminder(User user, Device device, int daysRemaining) {
        // Stub implementation for development
        System.out.println("Sending warranty expiration reminder to: " + user.getEmail() + " for device: " + device.getName());
    }

    @Override
    public void sendMaintenanceReminder(User user, Device device, MaintenanceRecord maintenance) {
        // Stub implementation for development
        System.out.println("Sending maintenance reminder to: " + user.getEmail() + " for device: " + device.getName());
    }

    @Override
    public void sendMonthlySummary(User user, List<Device> expiringDevices) {
        // Stub implementation for development
        System.out.println("Sending monthly summary to: " + user.getEmail() + " with " + expiringDevices.size() + " expiring devices");
    }
}

