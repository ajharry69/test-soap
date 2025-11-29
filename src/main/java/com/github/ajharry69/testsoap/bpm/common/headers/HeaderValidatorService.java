package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
class HeaderValidatorService {
    public void validate(HeaderRule rule, String headerValue) throws HeaderException {
        if (!rule.isRequired() && !StringUtils.hasText(headerValue)) return;

        if (rule.isRequired() && !StringUtils.hasText(headerValue)) {
            throw new MissingHeaderException(rule);
        }

        if (!rule.isValid(headerValue)) {
            throw new InvalidHeaderValueException(rule, headerValue);
        }
    }
}