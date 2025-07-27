package com.warrantyhub.service.impl;

import com.warrantyhub.dto.request.DeviceRequest;
import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.DeviceDTO;
import com.warrantyhub.dto.response.DeviceListResponse;
import com.warrantyhub.dto.response.DocumentDTO;
import com.warrantyhub.dto.response.MaintenanceRecordDTO;
import com.warrantyhub.model.Device;
import com.warrantyhub.model.Document;
import com.warrantyhub.model.MaintenanceRecord;
import com.warrantyhub.model.User;
import com.warrantyhub.exception.ResourceNotFoundException;
import com.warrantyhub.exception.UnauthorizedException;
import com.warrantyhub.repository.DeviceRepository;
import com.warrantyhub.repository.UserRepository;
import com.warrantyhub.service.DeviceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public DeviceServiceImpl(
            DeviceRepository deviceRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public DeviceListResponse getAllDevicesByUser(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Device> devices = deviceRepository.findByUser(user);

        List<DeviceDTO> deviceDTOs = devices.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        DeviceListResponse response = new DeviceListResponse();
        response.setDevices(deviceDTOs);
        return response;
    }

    @Override
    public DeviceDTO getDeviceById(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        // Check if device belongs to user
        if (!device.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to access this device");
        }

        return convertToDTO(device);
    }

    @Override
    public DeviceDTO createDevice(DeviceRequest deviceRequest, Authentication authentication) {
        logger.info("Creating device with data: {}", deviceRequest);
        User user = getUserFromAuthentication(authentication);

        Device device = new Device();
        device.setName(deviceRequest.getName());
        device.setManufacturer(deviceRequest.getManufacturer());
        device.setModel(deviceRequest.getModel());
        device.setSerialNumber(deviceRequest.getSerialNumber());
        device.setPurchaseDate(deviceRequest.getPurchaseDate());
        device.setWarrantyEndDate(deviceRequest.getWarrantyEndDate());
        device.setWarrantyProvider(deviceRequest.getWarrantyProvider());
        device.setPurchasePrice(deviceRequest.getPurchasePrice());
        device.setNotes(deviceRequest.getNotes());
        device.setUser(user);

        // Set warranty status based on end date
        if (device.getWarrantyEndDate() != null) {
            if (device.getWarrantyEndDate().isAfter(LocalDate.now())) {
                device.setWarrantyStatus("active");
            } else {
                device.setWarrantyStatus("expired");
            }
        } else {
            device.setWarrantyStatus("unknown");
        }

        Device savedDevice = deviceRepository.save(device);
        return convertToDTO(savedDevice);
    }

    @Override
    public DeviceDTO updateDevice(Long id, DeviceRequest deviceRequest, Authentication authentication) {
        logger.info("Updating device id {} with data: {}", id, deviceRequest);
        User user = getUserFromAuthentication(authentication);

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        // Check if device belongs to user
        if (!device.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to update this device");
        }

        // Update device fields
        device.setName(deviceRequest.getName());
        device.setManufacturer(deviceRequest.getManufacturer());
        device.setModel(deviceRequest.getModel());
        device.setSerialNumber(deviceRequest.getSerialNumber());
        device.setPurchaseDate(deviceRequest.getPurchaseDate());
        device.setWarrantyEndDate(deviceRequest.getWarrantyEndDate());
        device.setWarrantyProvider(deviceRequest.getWarrantyProvider());
        device.setPurchasePrice(deviceRequest.getPurchasePrice());
        device.setNotes(deviceRequest.getNotes());

        // Update warranty status based on end date
        if (device.getWarrantyEndDate() != null) {
            if (device.getWarrantyEndDate().isAfter(LocalDate.now())) {
                device.setWarrantyStatus("active");
            } else {
                device.setWarrantyStatus("expired");
            }
        } else {
            device.setWarrantyStatus("unknown");
        }

        Device updatedDevice = deviceRepository.save(device);
        return convertToDTO(updatedDevice);
    }

    @Override
    public ApiResponse deleteDevice(Long id, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        // Check if device belongs to user
        if (!device.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to delete this device");
        }

        deviceRepository.delete(device);
        return new ApiResponse(true, "Device deleted successfully");
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private DeviceDTO convertToDTO(Device device) {
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setId(device.getId().toString());
        deviceDTO.setName(device.getName());
        deviceDTO.setManufacturer(device.getManufacturer());
        deviceDTO.setModel(device.getModel());
        deviceDTO.setSerialNumber(device.getSerialNumber());
        deviceDTO.setPurchaseDate(device.getPurchaseDate());
        deviceDTO.setWarrantyEndDate(device.getWarrantyEndDate());
        deviceDTO.setWarrantyStatus(device.getWarrantyStatus());
        deviceDTO.setWarrantyProvider(device.getWarrantyProvider());
        deviceDTO.setPurchasePrice(device.getPurchasePrice());
        deviceDTO.setNotes(device.getNotes());

        // Convert maintenance records
        List<MaintenanceRecordDTO> maintenanceRecordDTOs = device.getMaintenanceHistory().stream()
                .map(this::convertToMaintenanceDTO)
                .collect(Collectors.toList());
        deviceDTO.setMaintenanceHistory(maintenanceRecordDTOs);

        // Convert documents
        List<DocumentDTO> documentDTOs = device.getDocuments().stream()
                .map(this::convertToDocumentDTO)
                .collect(Collectors.toList());
        deviceDTO.setDocuments(documentDTOs);

        return deviceDTO;
    }

    private MaintenanceRecordDTO convertToMaintenanceDTO(MaintenanceRecord record) {
        MaintenanceRecordDTO dto = new MaintenanceRecordDTO();
        dto.setId(record.getId().toString());
        dto.setDate(record.getDate());
        dto.setType(record.getType());
        dto.setDescription(record.getDescription());
        dto.setCost(record.getCost());
        dto.setServiceProvider(record.getServiceProvider());
        dto.setPartsReplaced(record.getPartsReplaced());
        dto.setNextScheduledDate(record.getNextScheduledDate());
        return dto;
    }

    private DocumentDTO convertToDocumentDTO(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId().toString());
        dto.setName(document.getName());
        dto.setFileUrl(document.getFileUrl());
        dto.setFileType(document.getFileType());
        dto.setUploadDate(document.getUploadDate());
        return dto;
    }
}
