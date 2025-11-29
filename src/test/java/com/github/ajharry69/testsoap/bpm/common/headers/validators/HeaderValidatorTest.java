package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.util.Pair;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderValidatorTest {
    @Nested
    class EpochTimestamp {
        static Stream<Pair<String, Boolean>> timestampProvider() {
            return Stream.of(
                    Pair.of("", false),
                    Pair.of("1nvalid", false),
                    Pair.of("1", true),
                    Pair.of(String.valueOf(Long.MIN_VALUE), true),
                    Pair.of(String.valueOf(Long.MAX_VALUE), true),
                    Pair.of(String.valueOf(System.currentTimeMillis()), true)
            );
        }

        @ParameterizedTest
        @MethodSource("timestampProvider")
        void shouldValidate(Pair<String, Boolean> timestamp) {
            var validator = new HeaderValidator.EpochTimestamp();

            var actual = validator.isValid(null, timestamp.getFirst());

            assertEquals(timestamp.getSecond(), actual);
        }
    }

    @Nested
    class Regex {
        static Stream<Pair<String, Boolean>> regexProvider() {
            return Stream.of(
                    Pair.of("", false),
                    Pair.of("1nvalid", false),
                    Pair.of("v", false),
                    Pair.of("v1", true),
                    Pair.of("v1.", false),
                    Pair.of("v1.12", true),
                    Pair.of("v1.123.3", true),
                    Pair.of("1.123.3", false),
                    Pair.of("v1.123.3-alpha01", false),
                    Pair.of("1.123.3-alpha01", false)
            );
        }

        @ParameterizedTest
        @MethodSource("regexProvider")
        void shouldValidate(Pair<String, Boolean> regex) {
            var validator = new HeaderValidator.Regex("v\\d+(.\\d+){0,2}");

            var actual = validator.isValid(null, regex.getFirst());

            assertEquals(regex.getSecond(), actual);
        }
    }
}