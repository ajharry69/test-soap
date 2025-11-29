package com.github.ajharry69.testsoap.bpm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ajharry69.testsoap.bpm.dto.RequestPayload;
import com.github.ajharry69.testsoap.bpm.config.MultipartPayloadConverterConfig.RequestPayloadJacksonConverter;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.lang.reflect.Type;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultipartPayloadConverterConfigTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class RequestPayloadJacksonConverter_canRead {
        private final RequestPayloadJacksonConverter converter = new RequestPayloadJacksonConverter(mapper);

        record TestCase(
                Type type,
                MediaType mediaType,
                String errorMessage
        ) {

        }

        @ParameterizedTest
        @MethodSource
        void shouldAcceptRequestPayload(TestCase testCase) {
            var type = testCase.type();
            var mediaType = testCase.mediaType();

            boolean canRead = converter.canRead(type, null, mediaType);

            assertTrue(canRead, testCase.errorMessage());
        }

        static Stream<TestCase> shouldAcceptRequestPayload() {
            return Stream.of(
                    new TestCase(
                            RequestPayload.class,
                            null,
                            "Expected converter to read RequestPayload when mediaType is null"
                    ),
                    new TestCase(
                            RequestPayload.class,
                            MediaType.ALL,
                            "Expected converter to read RequestPayload when mediaType is */*"
                    ),
                    new TestCase(
                            RequestPayload.class,
                            MediaType.TEXT_PLAIN,
                            "Expected converter to read RequestPayload when mediaType is text/plain"
                    ),
                    new TestCase(
                            RequestPayload.class,
                            MediaType.APPLICATION_OCTET_STREAM,
                            "Expected converter to read RequestPayload when mediaType is application/octet-stream"
                    )
            );
        }

        @ParameterizedTest
        @MethodSource
        void shouldNotAcceptRequestPayload(TestCase testCase) {
            var type = testCase.type();
            var mediaType = testCase.mediaType();

            boolean canRead = converter.canRead(type, null, mediaType);

            assertFalse(canRead, testCase.errorMessage());
        }

        static Stream<TestCase> shouldNotAcceptRequestPayload() {
            return Stream.of(
                    new TestCase(
                            RequestPayload.class,
                            MediaType.IMAGE_PNG,
                            "Expected converter to defer to defaults and reject non-JSON media type like image/png"
                    ),
                    new TestCase(
                            RequestPayloadJacksonConverter.class,
                            null,
                            "Expected converter to defer to defaults and reject non-RequestPayload type like String"
                    )
            );
        }
    }
}
