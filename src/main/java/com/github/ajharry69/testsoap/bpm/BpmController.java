package com.github.ajharry69.testsoap.bpm;

import com.github.ajharry69.testsoap.bpm.dto.RequestPayload;
import com.github.ajharry69.testsoap.bpm.common.validation.AllowedMultipartFileExtension;
import com.github.ajharry69.testsoap.bpm.dto.request.DocumentRequest;
import com.github.ajharry69.testsoap.bpm.dto.request.WorkItemQueryRequest;
import com.github.ajharry69.testsoap.bpm.dto.request.WorkItemRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/kcb-sfa-bpm-st/api/v1/bpm")
@Slf4j
@Validated
@RequiredArgsConstructor
public class BpmController {
    private final BpmService bpmService;
    @PostMapping(value = "/add-document", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addDocument(
            @Validated
            @RequestPart(value = "payload")
            RequestPayload<DocumentRequest> payload,
            @Validated
            @NotNull
            @AllowedMultipartFileExtension
            @RequestPart(value = "file") MultipartFile file) {
        log.info("====INCOMING ADD DOCUMENT REQUEST==== \n{}", payload);
        log.info("====INCOMING FILE==== \n{}", file.getOriginalFilename());
        return bpmService.addDocument(payload, file);
    }

    @PostMapping("create-workItem")
    public ResponseEntity<?> createWorkItem(@Validated @RequestBody RequestPayload<WorkItemRequest> requestPayload) {
        log.info("====INCOMING CREATE WORK-ITEM REQUEST==== \n{}", requestPayload);
        return bpmService.createWorkItem(requestPayload);
    }

    @PostMapping("query-workItem")
    public ResponseEntity<?> queryWorkItem(@Validated @RequestBody RequestPayload<WorkItemQueryRequest> requestPayload) {
        log.info("====INCOMING QUERY WORK-ITEM REQUEST==== \n{}", requestPayload);
        return bpmService.queryWorkItem(requestPayload);
    }
}
