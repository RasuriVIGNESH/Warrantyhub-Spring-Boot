package com.warrantyhub.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Request DTO for maintenance record information")
public class MaintenanceRecordRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Date when maintenance was performed", example = "2023-06-15")
    private LocalDate date;

    @Schema(description = "Type of maintenance performed", example = "Routine Checkup")
    private String type;

    @Schema(description = "Description of maintenance work", example = "Replaced battery and screen")
    private String description;

    @Schema(description = "Cost of maintenance", example = "150.00")
    private BigDecimal cost;

    @Schema(description = "Service provider who performed maintenance", example = "QuickFix Repairs")
    private String serviceProvider;

    @Schema(description = "List of parts replaced during maintenance", example = "[\"Battery\", \"Screen\"]")
    private List<String> partsReplaced;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Next scheduled maintenance date", example = "2024-06-15")
    private LocalDate nextScheduledDate;

    // No-args constructor
    public MaintenanceRecordRequest() {
    }
    
    // All-args constructor
    public MaintenanceRecordRequest(LocalDate date, String type, String description, BigDecimal cost,
                                   String serviceProvider, List<String> partsReplaced, LocalDate nextScheduledDate) {
        this.date = date;
        this.type = type;
        this.description = description;
        this.cost = cost;
        this.serviceProvider = serviceProvider;
        this.partsReplaced = partsReplaced;
        this.nextScheduledDate = nextScheduledDate;
    }
    
    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    public String getServiceProvider() {
        return serviceProvider;
    }
    
    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    
    public List<String> getPartsReplaced() {
        return partsReplaced;
    }
    
    public void setPartsReplaced(List<String> partsReplaced) {
        this.partsReplaced = partsReplaced;
    }
    
    public LocalDate getNextScheduledDate() {
        return nextScheduledDate;
    }
    
    public void setNextScheduledDate(LocalDate nextScheduledDate) {
        this.nextScheduledDate = nextScheduledDate;
    }
}