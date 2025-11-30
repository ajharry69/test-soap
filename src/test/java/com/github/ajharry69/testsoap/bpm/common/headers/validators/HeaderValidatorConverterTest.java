package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HeaderValidatorConverterTest {
    static Stream<TestCase> shouldReturnNull() {
        return Stream.of(
                new TestCase(
                        "com.example.DoesNotExistValidator",
                        "Expected null when class cannot be found"
                ),
                new TestCase(
                        (PrivateConstructorValidator.class).getName(),
                        "Expected null when no-arg constructor exists but is not accessible"
                ),
                new TestCase(
                        (HeaderValidatorConverterTest.NoNoArgConstructorValidator.class).getName(),
                        "Expected null when no no-arg constructor is available"
                ),
                new TestCase(
                        AbstractValidator.class.getName(),
                        "Expected null when type is abstract and cannot be instantiated"
                ),
                new TestCase(
                        (HeaderValidatorConverterTest.ThrowingCtorValidator.class).getName(),
                        "Expected null when constructor throws an exception"
                )
        );
    }

    @Test
    void shouldCreateInstanceFromFQCN() {
        var converter = new HeaderValidatorConverter();

        var actual = converter.convert(EpochTimestampValidator.class.getName());

        assertNotNull(actual, "Expected non-null instance when a valid FQCN is provided");
        assertInstanceOf(EpochTimestampValidator.class, actual, "Expected instance to be of type EpochTimestampValidator");
    }

    @ParameterizedTest
    @MethodSource
    void shouldReturnNull(TestCase testCase) {
        var converter = new HeaderValidatorConverter();

        var actual = converter.convert(testCase.className());

        assertNull(actual, testCase.errorMessage());
    }

    public static class NoNoArgConstructorValidator implements HeaderValidator {
        public NoNoArgConstructorValidator(String ignoredAny) {
        }

        @Override
        public ValidationResult validate(String headerName, String headerValue) {
            return new ValidationResult.Success();
        }
    }

    public static class ThrowingCtorValidator implements HeaderValidator {
        public ThrowingCtorValidator() {
            throw new RuntimeException();
        }

        @Override
        public ValidationResult validate(String headerName, String headerValue) {
            return new ValidationResult.Success();
        }
    }

    public static abstract class AbstractValidator implements HeaderValidator {
    }

    public static class PrivateConstructorValidator implements HeaderValidator {
        private PrivateConstructorValidator() {
        }

        @Override
        public ValidationResult validate(String headerName, String headerValue) {
            return new ValidationResult.Success();
        }
    }

    record TestCase(String className, String errorMessage) {

    }
}
