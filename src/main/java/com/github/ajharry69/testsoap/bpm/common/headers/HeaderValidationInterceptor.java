package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeadersValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
class HeaderValidationInterceptor implements HandlerInterceptor {
    private final HeaderValidationProperties properties;
    private final HeaderValidatorService validatorService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var exception = new HeadersValidationException();

        for (var rule : properties.getHeaders()) {
            var headerValue = request.getHeader(rule.getHeaderName());
            try {
                validatorService.validate(rule, headerValue);
            } catch (HeaderException e) {
                exception.addHeaderException(e);
            }
        }
        if (!exception.getHeaderExceptions().isEmpty()) throw exception;
        return true;
    }
}