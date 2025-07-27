package com.warrantyhub.service;

import com.warrantyhub.dto.request.MaintenanceRecordRequest;
import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.DeviceDTO;
import org.springframework.security.core.Authentication;

public interface MaintenanceService {
    DeviceDTO addMaintenanceRecord(Long deviceId, MaintenanceRecordRequest request, Authentication authentication);
    DeviceDTO updateMaintenanceRecord(Long deviceId, Long recordId, MaintenanceRecordRequest request, Authentication authentication);
    ApiResponse deleteMaintenanceRecord(Long deviceId, Long recordId, Authentication authentication);
}
