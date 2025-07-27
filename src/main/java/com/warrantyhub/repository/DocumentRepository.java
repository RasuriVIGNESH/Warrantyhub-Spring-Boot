package com.warrantyhub.repository;

import com.warrantyhub.model.Device;
import com.warrantyhub.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    int countByDevice(Device device);
    Optional<Document> findByStoredFileName(String fileName);
}
