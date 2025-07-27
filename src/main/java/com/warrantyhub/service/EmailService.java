package com.warrantyhub.service;

import com.warrantyhub.model.Device;
import com.warrantyhub.model.MaintenanceRecord;
import com.warrantyhub.model.User;

import java.util.List;

public interface EmailService {
    void sendWelcomeEmail(User user);
    void sendPasswordResetEmail(User user, String resetToken);
    void sendWarrantyExpirationReminder(User user, Device device, int daysRemaining);
    void sendMaintenanceReminder(User user, Device device, MaintenanceRecord maintenance);
    void sendMonthlySummary(User user, List<Device> expiringDevices);
}
