package com.github.ajharry69.testsoap.bpm;

import com.github.ajharry69.testsoap.bpm.dto.RequestPayload;
import com.github.ajharry69.testsoap.bpm.dto.request.DocumentRequest;
import com.github.ajharry69.testsoap.bpm.dto.request.WorkItemQueryRequest;
import com.github.ajharry69.testsoap.bpm.dto.request.WorkItemRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
class BpmService {

    public ResponseEntity<?> addDocument(RequestPayload<DocumentRequest> payload, @NotNull MultipartFile file) {
        return null;
    }

    public ResponseEntity<?> createWorkItem(RequestPayload<WorkItemRequest> requestPayload) {
        return null;
    }

    public ResponseEntity<?> queryWorkItem(RequestPayload<WorkItemQueryRequest> requestPayload) {
        return null;
    }
}
