package com.warrantyhub.model;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devices")

public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String manufacturer;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate warrantyEndDate;

    @Column(nullable = false)
    private String warrantyStatus;

    private String warrantyProvider;
    private BigDecimal purchasePrice;
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceRecord> maintenanceHistory = new ArrayList<>();

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getWarrantyStatus() {
		return warrantyStatus;
	}

	public void setWarrantyStatus(String warrantyStatus) {
		this.warrantyStatus = warrantyStatus;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<MaintenanceRecord> getMaintenanceHistory() {
		return maintenanceHistory;
	}

	public void setMaintenanceHistory(List<MaintenanceRecord> maintenanceHistory) {
		this.maintenanceHistory = maintenanceHistory;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public Device(Long id, String name, String manufacturer, String model, String serialNumber,
			LocalDate purchaseDate, LocalDate warrantyEndDate, String warrantyStatus, String warrantyProvider,
			BigDecimal purchasePrice, String notes, User user, List<MaintenanceRecord> maintenanceHistory,
			List<Document> documents) {
		this.id = id;
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		this.serialNumber = serialNumber;
		this.purchaseDate = purchaseDate;
		this.warrantyEndDate = warrantyEndDate;
		this.warrantyStatus = warrantyStatus;
		this.warrantyProvider = warrantyProvider;
		this.purchasePrice = purchasePrice;
		this.notes = notes;
		this.user = user;
		this.maintenanceHistory = maintenanceHistory;
		this.documents = documents;
	}

    public Device() {
    }
    
    
}
