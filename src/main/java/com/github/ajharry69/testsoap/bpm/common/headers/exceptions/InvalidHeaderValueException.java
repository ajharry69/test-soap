package com.github.ajharry69.testsoap.bpm.common.headers.exceptions;

import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;
import lombok.Getter;

@Getter
public class InvalidHeaderValueException extends HeaderException {
    private final String headerValue;

    public InvalidHeaderValueException(HeaderRule rule, String headerValue) {
        super(rule);
        this.headerValue = headerValue;
    }
}
