package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import org.springframework.util.StringUtils;

@FunctionalInterface
public interface HeaderValidator {
    HeaderValidator DEFAULT = (headerName, headerValue) -> {
        if (StringUtils.hasText(headerValue)) {
            return new ValidationResult.Success();
        } else {
            return new ValidationResult.Failure("Header value is required");
        }
    };

    ValidationResult validate(String headerName, String headerValue);
}
