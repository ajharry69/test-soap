package com.github.ajharry69.testsoap.bpm.common.headers.exceptions;

import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.ValidationResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public abstract sealed class HeaderException extends Exception permits MissingHeaderException, InvalidHeaderValueException {
    private final HeaderRule rule;
    private final ValidationResult.Failure failure;
}
