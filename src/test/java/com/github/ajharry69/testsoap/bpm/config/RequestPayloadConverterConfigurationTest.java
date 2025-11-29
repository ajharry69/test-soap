package com.github.ajharry69.testsoap.bpm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ajharry69.testsoap.bpm.common.KCBRequestContext;
import com.github.ajharry69.testsoap.bpm.common.KCBRequestContextHolder;
import com.github.ajharry69.testsoap.bpm.config.RequestPayloadConverterConfiguration.RequestPayloadJacksonConverter;
import com.github.ajharry69.testsoap.bpm.dto.RequestPayload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RequestPayloadConverterConfigurationTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final RequestPayloadJacksonConverter converter = new RequestPayloadJacksonConverter(mapper);

    @AfterEach
    void tearDown() {
        KCBRequestContextHolder.clear();
    }

    @Test
    void shouldAddCustomConverterAsFirst() {
        var config = new RequestPayloadConverterConfiguration(mapper);
        var converters = new ArrayList<HttpMessageConverter<?>>(List.of(new MappingJackson2HttpMessageConverter(mapper)));

        config.extendMessageConverters(converters);

        assertInstanceOf(
                RequestPayloadJacksonConverter.class,
                converters.getFirst(),
                "Expected RequestPayloadJacksonConverter to be added first"
        );
    }

    @Nested
    class canRead {
        static Stream<TestCase> shouldAcceptRequestPayload() throws NoSuchFieldException {
            class Types {
                RequestPayload<String> field;
            }
            Field f = Types.class.getDeclaredField("field");
            Type t = f.getGenericType();
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
                    ),
                    new TestCase(
                            RequestPayload.class,
                            MediaType.APPLICATION_JSON,
                            "Expected converter to read RequestPayload when mediaType is application/json (via super)"
                    ),
                    new TestCase(
                            t,
                            MediaType.TEXT_PLAIN,
                            "Expected converter to unwrap ParameterizedType and accept RequestPayload raw type"
                    )
            );
        }

        static Stream<TestCase> shouldNotAcceptRequestPayload() {
            return Stream.of(
                    new TestCase(
                            RequestPayload.class,
                            MediaType.IMAGE_PNG,
                            "Expected converter to defer to defaults and reject non-JSON media type like image/png"
                    ),
                    new TestCase(
                            RequestPayload.class,
                            new MediaType("application", "xml"),
                            "Expected converter to reject non-JSON like application/xml for RequestPayload (via super)"
                    )
            );
        }

        static Stream<TestCase> shouldDeferToSuper() {
            return Stream.of(
                    new TestCase(
                            RequestPayload.class.getTypeParameters()[0], // TypeVariable for <T>
                            MediaType.APPLICATION_JSON,
                            "Expected converter to defer to super for non-class/parameterized types"
                    ),
                    new TestCase(
                            String.class,
                            null,
                            "Expected converter to defer to super for non-RequestPayload type when mediaType is null"
                    ),
                    new TestCase(
                            Object.class,
                            MediaType.APPLICATION_JSON,
                            "Expected converter to defer to super for non-RequestPayload type for application/json"
                    ),
                    new TestCase(
                            Integer.class,
                            new MediaType("application", "vnd.test+json"),
                            "Expected converter to defer to super for vendor+json media type"
                    )
            );
        }

        @ParameterizedTest
        @MethodSource
        void shouldAcceptRequestPayload(TestCase testCase) {
            var type = testCase.type();
            var mediaType = testCase.mediaType();

            boolean canRead = converter.canRead(type, null, mediaType);

            assertTrue(canRead, testCase.errorMessage());
        }

        @ParameterizedTest
        @MethodSource
        void shouldNotAcceptRequestPayload(TestCase testCase) {
            var type = testCase.type();
            var mediaType = testCase.mediaType();

            boolean canRead = converter.canRead(type, null, mediaType);

            assertFalse(canRead, testCase.errorMessage());
        }

        @ParameterizedTest
        @MethodSource
        void shouldDeferToSuper(TestCase testCase) {
            var superConverter = new MappingJackson2HttpMessageConverter(mapper);

            boolean expected = superConverter.canRead(testCase.type(), null, testCase.mediaType());
            boolean actual = converter.canRead(testCase.type(), null, testCase.mediaType());

            assertEquals(expected, actual, testCase.errorMessage());
        }

        record TestCase(
                Type type,
                MediaType mediaType,
                String errorMessage
        ) {

        }
    }

    @Nested
    class read {
        private HttpInputMessage jsonMessage(String json) {
            return new HttpInputMessage() {
                private final HttpHeaders headers = new HttpHeaders();

                @Override
                public InputStream getBody() {
                    return new ByteArrayInputStream(json.getBytes());
                }

                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            };
        }

        @Test
        void shouldSetNewRequestContextWhenAbsent() throws Exception {
            assertNull(KCBRequestContextHolder.getContext(), "Precondition: context should be null");

            // language=JSON
            String json = """
                    {"messageID":"mid-1"}""";
            Object payload = converter.read(RequestPayload.class, null, jsonMessage(json));

            assertInstanceOf(RequestPayload.class, payload, "Expected payload to be deserialized as RequestPayload");

            var context = KCBRequestContextHolder.getContext();

            assertAll(
                    () -> assertNotNull(context, "Expected request context to be set"),
                    () -> assertNotNull(context.conversationID(), "Expected new conversationID to be generated"),
                    () -> assertEquals("mid-1", context.messageID(), "Expected messageID to be taken from payload")
            );
        }

        @Test
        void shouldPreserveConversationIdAndUpdateMessageIdWhenContextExists() throws Exception {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-xyz"));

            // language=JSON
            String json = """
                    {"messageID":"mid-2"}""";
            converter.read(RequestPayload.class, null, jsonMessage(json));

            var context = KCBRequestContextHolder.getContext();
            assertAll(
                    () -> assertNotNull(context, "Expected context to remain set"),
                    () -> assertEquals("conv-xyz", context.conversationID(), "Expected conversationID to be preserved"),
                    () -> assertEquals("mid-2", context.messageID(), "Expected messageID to be updated from payload")
            );
        }

        @Test
        void shouldNotAlterContextWhenPayloadIsNotRequestPayload() throws Exception {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-123", "mid-orig"));

            // language=JSON
            String json = """
                    "hello\"""";
            Object result = converter.read(String.class, null, jsonMessage(json));
            assertEquals("hello", result);

            var context = KCBRequestContextHolder.getContext();
            assertAll(
                    () -> assertNotNull(context),
                    () -> assertEquals("conv-123", context.conversationID()),
                    () -> assertEquals("mid-orig", context.messageID())
            );
        }
    }
}
