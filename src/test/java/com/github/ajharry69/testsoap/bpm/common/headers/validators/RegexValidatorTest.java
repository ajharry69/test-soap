package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexValidatorTest {
    static Stream<TestCase> regexProvider() {
        return Stream.of(
                new TestCase("", false),
                new TestCase("1nvalid", false),
                new TestCase("v", false),
                new TestCase("v1", true),
                new TestCase("v1.", false),
                new TestCase("v1.12", true),
                new TestCase("v1.123.3", true),
                new TestCase("1.123.3", false),
                new TestCase("v1.123.3-alpha01", false),
                new TestCase("1.123.3-alpha01", false)
        );
    }

    @ParameterizedTest
    @MethodSource("regexProvider")
    void shouldValidate(TestCase testCase) {
        var validator = new RegexValidator("v\\d+(.\\d+){0,2}");

        var actual = validator.isValid(null, testCase.headerValue());

        assertEquals(testCase.expected(), actual);
    }

    record TestCase(String headerValue, boolean expected) {
    }
}