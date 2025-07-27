package com.warrantyhub.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Document information associated with a device")
public class DocumentDTO {
    @Schema(description = "Unique identifier of the document", example = "65a8f4e3b8d1c12e3f4a5b6d")
    private String id;

    @Schema(description = "Name of the document", example = "Purchase Receipt")
    private String name;

    @Schema(description = "URL to access the document file", example = "https://example.com/documents/receipt123.pdf")
    private String fileUrl;

    @Schema(description = "Type of the document file", example = "application/pdf")
    private String fileType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date when the document was uploaded", example = "2023-01-16")
    private LocalDate uploadDate;

    // No-args constructor
    public DocumentDTO() {
    }
    
    // All-args constructor
    public DocumentDTO(String id, String name, String fileUrl, String fileType, LocalDate uploadDate) {
        this.id = id;
        this.name = name;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.uploadDate = uploadDate;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFileUrl() {
        return fileUrl;
    }
    
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    
    public String getFileType() {
        return fileType;
    }
    
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    
    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }
}