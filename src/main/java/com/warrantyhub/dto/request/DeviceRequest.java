package com.warrantyhub.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request DTO for device information")
public class DeviceRequest {
    @NotBlank(message = "Device name is required")
    @Schema(description = "Name of the device", example = "Smartphone X")
    private String name;

    @Schema(description = "Manufacturer of the device", example = "TechCorp")
    private String manufacturer;

    @Schema(description = "Model of the device", example = "X-2000")
    private String model;

    @Schema(description = "Serial number of the device", example = "SN123456789")
    private String serialNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date when the device was purchased", example = "2023-01-15")
    private LocalDate purchaseDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date when the warranty expires", example = "2025-01-15")
    private LocalDate warrantyEndDate;

    @Schema(description = "Provider of the warranty", example = "Best Warranty Inc")
    private String warrantyProvider;

    @Schema(description = "Purchase price of the device", example = "999.99")
    private BigDecimal purchasePrice;

    @Schema(description = "Additional notes about the device", example = "Purchased from Amazon")
    private String notes;
    
    // No-args constructor
    public DeviceRequest() {
    }
    
    // All-args constructor
    public DeviceRequest(String name, String manufacturer, String model, String serialNumber,
                        LocalDate purchaseDate, LocalDate warrantyEndDate,
                        String warrantyProvider, BigDecimal purchasePrice, String notes) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.warrantyEndDate = warrantyEndDate;
        this.warrantyProvider = warrantyProvider;
        this.purchasePrice = purchasePrice;
        this.notes = notes;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getWarrantyEndDate() {
        return warrantyEndDate;
    }

    public void setWarrantyEndDate(LocalDate warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
    }
    
    public String getWarrantyProvider() {
        return warrantyProvider;
    }
    
    public void setWarrantyProvider(String warrantyProvider) {
        this.warrantyProvider = warrantyProvider;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}