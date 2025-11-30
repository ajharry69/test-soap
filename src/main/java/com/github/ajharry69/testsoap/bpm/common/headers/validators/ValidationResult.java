package com.github.ajharry69.testsoap.bpm.common.headers.validators;

public sealed interface ValidationResult permits ValidationResult.Failure, ValidationResult.Success {
    final class Success implements ValidationResult {
    }

    record Failure(String errorMessage) implements ValidationResult {
    }
}
