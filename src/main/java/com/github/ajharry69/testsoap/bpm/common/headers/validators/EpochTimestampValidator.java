package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import java.util.Date;

public final class EpochTimestampValidator implements HeaderValidator {
    @Override
    public ValidationResult validate(String headerName, String headerValue) {
        try {
            new Date(Long.parseLong(headerValue));
            return new ValidationResult.Success();
        } catch (NumberFormatException e) {
            return new ValidationResult.Failure("Header value is not a valid epoch timestamp");
        }
    }
}