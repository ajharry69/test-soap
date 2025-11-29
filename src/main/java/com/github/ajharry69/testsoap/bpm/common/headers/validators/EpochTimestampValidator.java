package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import java.util.Date;

public final class EpochTimestampValidator implements HeaderValidator {
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