package com.github.ajharry69.testsoap.bpm.common.headers.exceptions;

import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;

public class MissingHeaderException extends HeaderException {
    public MissingHeaderException(HeaderRule rule) {
        super(rule);
    }
}
