package com.warrantyhub.service;

import com.warrantyhub.dto.request.DeviceRequest;
import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.DeviceDTO;
import com.warrantyhub.dto.response.DeviceListResponse;
import org.springframework.security.core.Authentication;

public interface DeviceService {
    DeviceListResponse getAllDevicesByUser(Authentication authentication);
    DeviceDTO getDeviceById(Long id, Authentication authentication);
    DeviceDTO createDevice(DeviceRequest deviceRequest, Authentication authentication);
    DeviceDTO updateDevice(Long id, DeviceRequest deviceRequest, Authentication authentication);
    ApiResponse deleteDevice(Long id, Authentication authentication);
}
