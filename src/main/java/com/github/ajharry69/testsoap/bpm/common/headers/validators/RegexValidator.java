package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import jakarta.validation.constraints.NotNull;

import java.util.regex.Pattern;

public record RegexValidator(@NotNull Pattern pattern) implements HeaderValidator {
    public RegexValidator(String pattern) {
        this(Pattern.compile(pattern));
    }

    @Override
    public boolean isValid(String headerName, String headerValue) {
        return pattern.matcher(headerValue.trim()).matches();
    }
}