package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import jakarta.validation.constraints.NotNull;

import java.util.regex.Pattern;

public record RegexValidator(@NotNull Pattern pattern) implements HeaderValidator {
    public RegexValidator(String pattern) {
        this(Pattern.compile(pattern));
    }

    @Override
    public ValidationResult validate(String headerName, String headerValue) {
        return pattern.matcher(headerValue).matches()
                ? new ValidationResult.Success()
                : new ValidationResult.Failure("Header value '%s' does not match pattern '%s'".formatted(headerValue, pattern.pattern()));
    }
}