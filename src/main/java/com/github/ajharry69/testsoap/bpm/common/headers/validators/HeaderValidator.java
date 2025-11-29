package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import org.springframework.util.StringUtils;

@FunctionalInterface
public interface HeaderValidator {
    HeaderValidator DEFAULT = (headerName, headerValue) -> StringUtils.hasText(headerValue);

    boolean isValid(String headerName, String headerValue);
}
