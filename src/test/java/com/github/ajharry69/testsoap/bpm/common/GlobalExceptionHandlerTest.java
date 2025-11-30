package com.github.ajharry69.testsoap.bpm.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.github.ajharry69.testsoap.bpm.common.exceptions.DocumentExtensionMismatchException;
import com.github.ajharry69.testsoap.bpm.common.headers.HeaderRule;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.HeadersValidationException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.ValidationResult;
import com.github.ajharry69.testsoap.bpm.dto.ResponsePayload;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    @AfterEach
    void tearDown() {
        try {
            KCBRequestContextHolder.clear();
        } catch (Exception ignored) {
        }
    }

    @Nested
    class handleDocumentExtensionMismatchException {
        static Stream<String> shouldMapExceptionToUnsupportedMediaTypePayload() {
            return Stream.of(null, "", "   ", "pdf", ".Pdf", " JPG ", "png");
        }

        @ParameterizedTest
        @MethodSource
        void shouldMapExceptionToUnsupportedMediaTypePayload(String fileExtension) {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-001", "msg-001"));

            var ex = new DocumentExtensionMismatchException("exe", fileExtension);
            var handler = new GlobalExceptionHandler();

            var actual = handler.handleDocumentExtensionMismatchException(ex);

            var body = Objects.requireNonNull(actual.getBody());
            assertAll(
                    () -> assertEquals(HttpStatus.BAD_REQUEST.value(), actual.getStatusCode().value()),
                    () -> assertEquals("msg-001", body.getMessageID()),
                    () -> assertEquals("conv-001", body.getConversationID()),
                    () -> assertEquals("4000453", body.getMessageCode()),
                    () -> assertEquals("Document extension 'exe' does not match file extension '%s'".formatted(ex.getFileExtension()), body.getMessageDescription()),
                    () -> assertNull(body.getErrorInfo())
            );
        }
    }

    @Nested
    class handleHttpMediaTypeNotSupportedException {
        @Test
        void shouldMapExceptionToUnsupportedMediaTypePayload() {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-001", "msg-001"));

            var ex = new HttpMediaTypeNotSupportedException("Error");
            var handler = new GlobalExceptionHandler();

            var actual = handler.handleHttpMediaTypeNotSupportedException(ex);

            var body = Objects.requireNonNull(actual.getBody());
            assertAll(
                    () -> assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), actual.getStatusCode().value()),
                    () -> assertEquals("msg-001", body.getMessageID()),
                    () -> assertEquals("conv-001", body.getConversationID()),
                    () -> assertEquals("ERR-UNSUPPORTED_CONTENT_TYPE", body.getMessageCode()),
                    () -> assertNull(body.getErrorInfo())
            );
        }
    }

    @Nested
    class handleHeadersValidationException {
        @Test
        void shouldMapExceptionsToBadRequestPayload() {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-001", "msg-001"));

            var missingRule = HeaderRule.builder().headerName("X-FeatureName").required(true).build();
            var invalidRule = HeaderRule.builder().headerName("X-MinorServiceVersion").required(true).build();

            var ex = new HeadersValidationException();
            ex.addHeaderException(new MissingHeaderException(missingRule));
            ex.addHeaderException(new InvalidHeaderValueException(invalidRule, new ValidationResult.Failure("Invalid value")));
            var handler = new GlobalExceptionHandler();

            var actual = handler.handleHeadersValidationException(ex);

            var body = Objects.requireNonNull(actual.getBody());
            assertAll(
                    () -> assertEquals(400, actual.getStatusCode().value()),
                    () -> assertEquals("msg-001", body.getMessageID()),
                    () -> assertEquals("conv-001", body.getConversationID()),
                    () -> assertEquals("4000453", body.getMessageCode()),
                    () -> assertNotNull(body.getErrorInfo()),
                    () -> assertEquals(2, body.getErrorInfo().size()),
                    () -> assertTrue(body.getErrorInfo().stream().anyMatch(e ->
                            e.getErrorCode().equals("X-FeatureName") &&
                                    e.getErrorDescription().equals("Missing required header"))),
                    () -> assertLinesMatch(
                            Stream.of("Invalid value"),
                            body.getErrorInfo()
                                    .stream()
                                    .filter(e -> e.getErrorCode().equals("X-MinorServiceVersion"))
                                    .map(ResponsePayload.ErrorInfo::getErrorDescription)
                    )

            );
        }
    }

    @Nested
    class handleBindException {
        @Test
        void shouldReturnStructuredPayload() {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-abc", "mid-123"));

            var target = new Object();
            var binding = new BeanPropertyBindingResult(target, "target");
            binding.addError(new FieldError("target", "file", "Invalid extension 'exe'. Allowed extensions are: [pdf, jpg]"));
            var ex = new BindException(binding);

            var handler = new GlobalExceptionHandler();
            var resp = handler.handleBindException(ex);

            var body = Objects.requireNonNull(resp.getBody());
            assertAll(
                    () -> assertEquals(400, resp.getStatusCode().value()),
                    () -> assertEquals("mid-123", body.getMessageID()),
                    () -> assertEquals("conv-abc", body.getConversationID()),
                    () -> assertNotNull(body.getErrorInfo()),
                    () -> assertTrue(body.getErrorInfo().stream().anyMatch(e ->
                            e.getErrorCode().equals("file") && e.getErrorDescription().startsWith("Invalid extension")))
            );
        }
    }

    @Nested
    class handleMultipartExceptions {
        @Test
        void shouldReturnStructuredPayload() {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-555", "mid-123"));

            var ex = new MissingServletRequestPartException("file");
            var handler = new GlobalExceptionHandler();
            var resp = handler.handleMultipartExceptions(ex);

            var body = Objects.requireNonNull(resp.getBody());
            assertAll(
                    () -> assertEquals(400, resp.getStatusCode().value()),
                    () -> assertEquals("mid-123", body.getMessageID()),
                    () -> assertEquals("conv-555", body.getConversationID()),
                    () -> assertNotNull(body.getErrorInfo()),
                    () -> assertEquals("file", body.getErrorInfo().getFirst().getErrorCode())
            );
        }
    }

    @Nested
    class handleConstraintViolationException {
        @Test
        void shouldReturnStructuredPayload() {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-999", "mid-901"));

            ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
            when(violation.getMessage())
                    .thenReturn("must be a valid file extension");
            Path path = mock(Path.class);
            when(path.toString())
                    .thenReturn("addDocument.file");
            when(violation.getPropertyPath())
                    .thenReturn(path);

            var ex = new ConstraintViolationException(Set.of(violation));
            var handler = new GlobalExceptionHandler();
            var resp = handler.handleConstraintViolationException(ex);

            var body = Objects.requireNonNull(resp.getBody());
            assertAll(
                    () -> assertEquals(400, resp.getStatusCode().value()),
                    () -> assertEquals("mid-901", body.getMessageID()),
                    () -> assertEquals("conv-999", body.getConversationID()),
                    () -> assertTrue(body.getErrorInfo().stream().anyMatch(e ->
                            e.getErrorCode().equals("file") && e.getErrorDescription().contains("valid file extension")))
            );
        }
    }

    @Nested
    class handleHttpMessageNotReadable {
        @Test
        void shouldMapUnknownPropertyTo400WithDescriptiveMessage() {
            KCBRequestContextHolder.setContext(new KCBRequestContext("conv-xyz", "msg-xyz"));
            var cause = mock(UnrecognizedPropertyException.class);
            when(cause.getPropertyName())
                    .thenReturn("unknownFieldName");
            when(cause.getPath())
                    .thenReturn(List.of(new JsonMappingException.Reference("example", "unknownFieldName")));
            var handler = new GlobalExceptionHandler();
            var exception = new HttpMessageNotReadableException("JSON parse error", cause, null);

            var actual = handler.handleHttpMessageNotReadable(exception);

            var body = Objects.requireNonNull(actual.getBody());
            assertAll(
                    () -> assertEquals(400, actual.getStatusCode().value()),
                    () -> assertEquals("msg-xyz", body.getMessageID()),
                    () -> assertEquals("conv-xyz", body.getConversationID()),
                    () -> assertEquals("4000453", body.getMessageCode()),
                    () -> assertNotNull(body.getErrorInfo()),
                    () -> assertFalse(body.getErrorInfo().isEmpty()),
                    () -> assertEquals("Unknown properties: 'unknownFieldName'", body.getErrorInfo().getFirst().getErrorDescription())
            );
        }
    }
}