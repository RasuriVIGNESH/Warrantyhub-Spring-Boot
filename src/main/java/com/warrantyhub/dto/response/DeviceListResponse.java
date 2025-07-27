package com.warrantyhub.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "List of devices response")
public class DeviceListResponse {
    @Schema(description = "Collection of devices")
    private List<DeviceDTO> devices;
    
    // No-args constructor
    public DeviceListResponse() {
    }
    
    // All-args constructor
    public DeviceListResponse(List<DeviceDTO> devices) {
        this.devices = devices;
    }
    
    // Getters and Setters
    public List<DeviceDTO> getDevices() {
        return devices;
    }
    
    public void setDevices(List<DeviceDTO> devices) {
        this.devices = devices;
    }
}