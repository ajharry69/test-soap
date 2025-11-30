package com.github.ajharry69.testsoap.bpm.common.headers.exceptions;

import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.ValidationResult;

public final class MissingHeaderException extends HeaderException {
    public MissingHeaderException(HeaderRule rule) {
        super(rule, new ValidationResult.Failure("Missing required header"));
    }
}
