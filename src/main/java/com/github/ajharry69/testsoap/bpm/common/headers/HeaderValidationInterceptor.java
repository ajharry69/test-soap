package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeadersValidationException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.ValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

record HeaderValidationInterceptor(HeaderValidationProperties properties) implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var exception = new HeadersValidationException();

        for (var rule : properties.getHeaders()) {
            var headerValue = request.getHeader(rule.getHeaderName());
            try {
                validate(rule, headerValue);
            } catch (HeaderException e) {
                exception.addHeaderException(e);
            }
        }
        if (!exception.getHeaderExceptions().isEmpty()) throw exception;
        return true;
    }

    void validate(HeaderRule rule, String headerValue) throws HeaderException {
        if (!rule.isRequired() && !StringUtils.hasText(headerValue)) return;

        if (rule.isRequired() && !StringUtils.hasText(headerValue)) {
            throw new MissingHeaderException(rule);
        }

        switch (rule.validate(headerValue)) {
            case ValidationResult.Success ignored -> {
            }
            case ValidationResult.Failure failure -> throw new InvalidHeaderValueException(
                    rule,
                    failure
            );
        }
    }
}