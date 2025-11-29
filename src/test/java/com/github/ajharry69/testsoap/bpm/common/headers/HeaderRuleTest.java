package com.github.ajharry69.testsoap.bpm.common.headers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.util.Pair;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderRuleTest {
    @ParameterizedTest
    @MethodSource
    void shouldValidateRequired(Pair<String, Boolean> testCase) {
        var rule = new HeaderRule();

        var actual = rule.isValid(testCase.getFirst());

        assertEquals(testCase.getSecond(), actual);
    }

    static Stream<Pair<String, Boolean>> shouldValidateRequired() {
        return Stream.of(
                Pair.of("", false),
                Pair.of(" ", false),
                Pair.of("e", true),
                Pair.of(" e ", true)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldValidateOptional(Pair<String, Boolean> testCase) {
        var rule = new HeaderRule();
        rule.setRequired(false);

        var actual = rule.isValid(testCase.getFirst());

        assertEquals(testCase.getSecond(), actual);
    }

    static Stream<Pair<String, Boolean>> shouldValidateOptional() {
        return Stream.of(
                Pair.of("", false),
                Pair.of(" ", false),
                Pair.of("e", true),
                Pair.of(" e ", true)
        );
    }
}