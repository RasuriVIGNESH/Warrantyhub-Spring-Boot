package com.warrantyhub.controller;

import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.DocumentDTO;
import com.warrantyhub.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/devices/{deviceId}/documents" )
@Tag(name = "Document Management", description = "Operations for managing device documents")
@SecurityRequirement(name = "Bearer Authentication")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @Operation(
            summary = "Upload a document",
            description = "Uploads a document for a specific device"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Document uploaded successfully",
            content = @Content(schema = @Schema(implementation = DocumentDTO.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid file or request",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Device not found or doesn\"t belong to the user",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<DocumentDTO> uploadDocument(
            @Parameter(description = "ID of the device to attach the document to", required = true)
            @PathVariable Long deviceId,
            @Parameter(description = "Document file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        return ResponseEntity.ok(documentService.uploadDocument(deviceId, file, authentication));
    }

    @GetMapping("/{documentId}")
    @Operation(
            summary = "Download a document",
            description = "Downloads a specific document by its ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Document downloaded successfully",
            content = @Content(schema = @Schema(implementation = Resource.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Document not found or doesn\"t belong to the user",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(description = "ID of the document to download", required = true)
            @PathVariable Long documentId,
            Authentication authentication) {
        Resource resource = documentService.downloadDocument(documentId, authentication);

        String contentType = null;
        try {
            contentType = resource.getURL().openConnection().getContentType();
        } catch (IOException ex) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    @Operation(
            summary = "Delete a document",
            description = "Deletes a specific document by its ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Document deleted successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing authentication token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Document not found or doesn\"t belong to the user",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
    )
    public ResponseEntity<ApiResponse> deleteDocument(
            @Parameter(description = "ID of the device that owns the document", required = true)
            @PathVariable Long deviceId,
            @Parameter(description = "ID of the document to delete", required = true)
            @PathVariable Long documentId,
            Authentication authentication) {
        return ResponseEntity.ok(documentService.deleteDocument(deviceId, documentId, authentication));
    }
}
