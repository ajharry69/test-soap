package com.github.ajharry69.testsoap.bpm.common.headers.exceptions;

import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.ValidationResult;
import lombok.Getter;

@Getter
public final class InvalidHeaderValueException extends HeaderException {
    public InvalidHeaderValueException(HeaderRule rule, ValidationResult.Failure failure) {
        super(rule, failure);
    }
}
