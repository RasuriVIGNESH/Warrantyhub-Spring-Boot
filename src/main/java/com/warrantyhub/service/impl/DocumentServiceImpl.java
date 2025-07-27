package com.warrantyhub.service.impl;

import com.warrantyhub.config.FileStorageConfig;
import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.DocumentDTO;
import com.warrantyhub.model.Device;
import com.warrantyhub.model.Document;
import com.warrantyhub.model.User;
import com.warrantyhub.exception.FileStorageException;
import com.warrantyhub.exception.ResourceNotFoundException;
import com.warrantyhub.exception.UnauthorizedException;
import com.warrantyhub.repository.DeviceRepository;
import com.warrantyhub.repository.DocumentRepository;
import com.warrantyhub.repository.UserRepository;
import com.warrantyhub.service.DocumentService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final Path fileStorageLocation;
    private final DocumentRepository documentRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DocumentServiceImpl(
            FileStorageConfig fileStorageConfig,
            DocumentRepository documentRepository,
            DeviceRepository deviceRepository,
            UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;

        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public DocumentDTO uploadDocument(Long deviceId, MultipartFile file, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        // Check if device belongs to user
        if (!device.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to upload documents to this device");
        }

        // Check if device already has 10 documents
        if (device.getDocuments().size() >= 10) {
            throw new FileStorageException("Maximum number of documents reached for this device");
        }

        // Normalize file name
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Check if the file's name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
        }

        // Generate unique file name to prevent overwriting
        String extension = FilenameUtils.getExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + "." + extension;

        try {
            // Copy file to the target location (replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Create file URL
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(storedFileName)
                    .toUriString();

            // Save document metadata to database
            Document document = new Document();
            document.setName(originalFileName);
            document.setFilePath(targetLocation.toString());
            document.setFileUrl(fileUrl);
            document.setFileType(file.getContentType());
            document.setUploadDate(LocalDate.now());
            document.setDevice(device);

            Document savedDocument = documentRepository.save(document);

            // Convert to DTO
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setId(savedDocument.getId().toString());
            documentDTO.setName(savedDocument.getName());
            documentDTO.setFileUrl(savedDocument.getFileUrl());
            documentDTO.setFileType(savedDocument.getFileType());
            documentDTO.setUploadDate(savedDocument.getUploadDate());

            return documentDTO;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource downloadDocument(Long documentId, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        // Check if document belongs to user's device
        if (!document.getDevice().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to download this document");
        }

        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + document.getName());
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + document.getName(), ex);
        }
    }

    @Override
    public ApiResponse deleteDocument(Long deviceId, Long documentId, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        // Check if device belongs to user
        if (!device.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You don't have permission to delete documents from this device");
        }

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        // Check if document belongs to device
        if (!document.getDevice().getId().equals(deviceId)) {
            throw new UnauthorizedException("Document does not belong to the specified device");
        }

        try {
            // Delete file from storage
            Path filePath = Paths.get(document.getFilePath());
            Files.deleteIfExists(filePath);

            // Delete document from database
            documentRepository.delete(document);

            return new ApiResponse(true, "Document deleted successfully");
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file. Please try again!", ex);
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
