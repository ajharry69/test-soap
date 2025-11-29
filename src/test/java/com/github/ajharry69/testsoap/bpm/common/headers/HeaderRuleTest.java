package com.github.ajharry69.testsoap.bpm.common.headers;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderRuleTest {
    @Nested
    class isValid {
        static Stream<TestCase> shouldValidateRequired() {
            return Stream.of(
                    new TestCase("", false),
                    new TestCase(" ", false),
                    new TestCase("e", true),
                    new TestCase(" e ", true)
            );
        }

        static Stream<TestCase> shouldValidateOptional() {
            return Stream.of(
                    new TestCase("", false),
                    new TestCase(" ", false),
                    new TestCase("e", true),
                    new TestCase(" e ", true)
            );
        }

        @ParameterizedTest
        @MethodSource
        void shouldValidateRequired(TestCase testCase) {
            var rule = new HeaderRule();

            var actual = rule.isValid(testCase.headerValue());

            assertEquals(testCase.expected(), actual);
        }

        @ParameterizedTest
        @MethodSource
        void shouldValidateOptional(TestCase testCase) {
            var rule = HeaderRule.builder()
                    .required(false)
                    .build();

            var actual = rule.isValid(testCase.headerValue());

            assertEquals(testCase.expected(), actual);
        }

        record TestCase(String headerValue, boolean expected) {
        }
    }

    @Nested
    class equals {
        static Stream<TestCase> shouldCorrectlyEvaluateEquals() {
            return Stream.of(
                    new TestCase(new HeaderRule(), new HeaderRule(), true),
                    new TestCase(
                            HeaderRule.builder()
                                    .build(),
                            HeaderRule.builder()
                                    .build(),
                            true
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .build(),
                            null,
                            false
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .build(),
                            "",
                            false
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .build(),
                            false
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .build(),
                            HeaderRule.builder()
                                    .build(),
                            false
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .build(),
                            true
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName("X-Featurecode")
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .build(),
                            true
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName("X-Featurecode")
                                    .required(true)
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .required(false)
                                    .build(),
                            true
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName("X-Featurecode")
                                    .required(true)
                                    .validator((headerName, headerValue) -> true)
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode")
                                    .required(false)
                                    .validator((headerName, headerValue) -> false)
                                    .build(),
                            true
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName("X-Featurecode")
                                    .required(true)
                                    .validator((headerName, headerValue) -> true)
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode1")
                                    .required(false)
                                    .validator((headerName, headerValue) -> false)
                                    .build(),
                            false
                    ),
                    new TestCase(
                            HeaderRule.builder()
                                    .headerName(" X-Featurecode")
                                    .required(true)
                                    .validator((headerName, headerValue) -> true)
                                    .build(),
                            HeaderRule.builder()
                                    .headerName("X-FeatureCode  ")
                                    .required(false)
                                    .validator((headerName, headerValue) -> false)
                                    .build(),
                            true
                    )
            );
        }

        @ParameterizedTest
        @MethodSource
        void shouldCorrectlyEvaluateEquals(TestCase testCase) {
            var actual = testCase.rule1.equals(testCase.rule2);

            assertEquals(testCase.expected(), actual);
        }

        record TestCase(HeaderRule rule1, Object rule2, boolean expected) {
        }
    }
}