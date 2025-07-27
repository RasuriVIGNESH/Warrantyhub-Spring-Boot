package com.warrantyhub.service;

import com.warrantyhub.dto.response.ApiResponse;
import com.warrantyhub.dto.response.DocumentDTO;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    DocumentDTO uploadDocument(Long deviceId, MultipartFile file, Authentication authentication);
    Resource downloadDocument(Long documentId, Authentication authentication);
    ApiResponse deleteDocument(Long deviceId, Long documentId, Authentication authentication);
}
