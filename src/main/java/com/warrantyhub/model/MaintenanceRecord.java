package com.warrantyhub.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private String type;
    private String description;
    private BigDecimal cost;
    private String serviceProvider;

    @ElementCollection
    @CollectionTable(name = "maintenance_parts_replaced",
            joinColumns = @JoinColumn(name = "maintenance_id"))
    @Column(name = "part_name")
    private List<String> partsReplaced = new ArrayList<>();

    private LocalDate nextScheduledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    
	public MaintenanceRecord() {
	}

	public MaintenanceRecord(Long id, LocalDate date, String type, String description, BigDecimal cost,
			String serviceProvider, List<String> partsReplaced, LocalDate nextScheduledDate, Device device) {
		this.id = id;
		this.date = date;
		this.type = type;
		this.description = description;
		this.cost = cost;
		this.serviceProvider = serviceProvider;
		this.partsReplaced = partsReplaced;
		this.nextScheduledDate = nextScheduledDate;
		this.device = device;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}


    
}
