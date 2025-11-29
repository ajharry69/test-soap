package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpochTimestampValidatorTest {
    static Stream<TestCase> timestampProvider() {
        return Stream.of(
                new TestCase("", false),
                new TestCase("1nvalid", false),
                new TestCase("1", true),
                new TestCase(String.valueOf(Long.MIN_VALUE), true),
                new TestCase(String.valueOf(Long.MAX_VALUE), true),
                new TestCase(String.valueOf(System.currentTimeMillis()), true)
        );
    }

    @ParameterizedTest
    @MethodSource("timestampProvider")
    void shouldValidate(TestCase timestamp) {
        var validator = new EpochTimestampValidator();

        var actual = validator.isValid(null, timestamp.headerValue());

        assertEquals(timestamp.expected(), actual);
    }

    record TestCase(String headerValue, boolean expected) {
    }
}