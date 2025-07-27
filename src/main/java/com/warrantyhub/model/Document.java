package com.warrantyhub.model;

import jakarta.persistence.*;


import java.time.LocalDate;

@Entity
@Table(name = "documents")

public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private String fileUrl;

    private String fileType;
    
    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private LocalDate uploadDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public String getStoredFileName() {
		return storedFileName;
	}

	public void setStoredFileName(String storedFileName) {
		this.storedFileName = storedFileName;
	}

	public LocalDate getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}

	public Document(Long id, String name, String filePath, String fileUrl, String fileType, String storedFileName,
			LocalDate uploadDate, Device device) {
		this.id = id;
		this.name = name;
		this.filePath = filePath;
		this.fileUrl = fileUrl;
		this.fileType = fileType;
		this.storedFileName = storedFileName;
		this.uploadDate = uploadDate;
		this.device = device;
	}

	public Document() {
	}


    
}
