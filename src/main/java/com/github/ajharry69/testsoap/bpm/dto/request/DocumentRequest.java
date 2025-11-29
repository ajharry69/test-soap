package com.github.ajharry69.testsoap.bpm.dto.request;

import com.github.ajharry69.testsoap.bpm.common.validation.AllowedDocumentExtension;
import com.github.ajharry69.testsoap.bpm.common.validation.AllowedDocumentName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentRequest {
    @NotNull(message = "Field documentName is required.")
    @NotBlank(message = "Field documentName cannot be blank.")
    @AllowedDocumentName(message = "Field documentName has invalid document name")
    private String documentName;
    @NotNull(message = "Field documentExtension is required.")
    @NotBlank(message = "Field documentExtension cannot be blank.")
    @AllowedDocumentExtension(message = "Field documentExtension has invalid document extension")
    private String documentExtension;
}
