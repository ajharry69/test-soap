package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.regex.Pattern;

@FunctionalInterface
public interface HeaderValidator {
    HeaderValidator DEFAULT = (headerName, headerValue) -> StringUtils.hasText(headerValue);

    boolean isValid(String headerName, String headerValue);

    final class EpochTimestamp implements HeaderValidator {
        @Override
        public boolean isValid(String headerName, String headerValue) {
            try {
                new Date(Long.parseLong(headerValue));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    record Regex(@NotNull Pattern pattern) implements HeaderValidator {
        public Regex(String pattern) {
            this(Pattern.compile(pattern));
        }

        @Override
        public boolean isValid(String headerName, String headerValue) {
            return pattern.matcher(headerValue.trim()).matches();
        }
    }
}
