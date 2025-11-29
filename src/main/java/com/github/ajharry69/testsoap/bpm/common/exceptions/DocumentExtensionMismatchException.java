package com.github.ajharry69.testsoap.bpm.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class DocumentExtensionMismatchException extends ResponseStatusException {
    private final String documentExtension;
    private final String fileExtension;
    public DocumentExtensionMismatchException(String documentExtension, String fileExtension) {
        super(HttpStatus.BAD_REQUEST);
        this.documentExtension = documentExtension;
        this.fileExtension = fileExtension;
    }
}
