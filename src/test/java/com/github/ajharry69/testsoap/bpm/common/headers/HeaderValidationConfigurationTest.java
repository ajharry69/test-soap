package com.github.ajharry69.testsoap.bpm.common.headers;

import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.InvalidHeaderValueException;
import com.github.ajharry69.testsoap.bpm.common.headers.exceptions.MissingHeaderException;
import com.github.ajharry69.testsoap.bpm.common.headers.validators.RegexValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeaderValidationConfigurationTest {

    @Test
    void addInterceptors_registersHeaderValidationInterceptor() {
        var properties = new HeaderValidationProperties();
        var config = new HeaderValidationConfiguration(properties);
        var registry = mock(InterceptorRegistry.class);
        var captor = ArgumentCaptor.forClass(HandlerInterceptor.class);
        when(registry.addInterceptor(captor.capture()))
                .thenReturn(null);

        config.addInterceptors(registry);

        var added = captor.getValue();
        assertAll(
                () -> assertNotNull(added),
                () -> assertInstanceOf(HeaderValidationInterceptor.class, added)
        );
    }

    @Nested
    class validate {
        private final HeaderValidationInterceptor interceptor = new HeaderValidationInterceptor(new HeaderValidationProperties());

        @Test
        void whenRequiredAndMissing_thenThrowsMissingHeaderException() {
            var rule = HeaderRule.builder().headerName("X-Req").required(true).build();

            assertAll(
                    () -> assertThrows(MissingHeaderException.class, () -> interceptor.validate(rule, "")),
                    () -> assertThrows(MissingHeaderException.class, () -> interceptor.validate(rule, null)),
                    () -> assertThrows(MissingHeaderException.class, () -> interceptor.validate(rule, " "))
            );
        }

        @Test
        void whenOptionalAndMissing_thenDoesNothing() {
            var rule = HeaderRule.builder().headerName("X-Opt").required(false).build();

            assertAll(
                    () -> assertDoesNotThrow(() -> interceptor.validate(rule, "")),
                    () -> assertDoesNotThrow(() -> interceptor.validate(rule, null)),
                    () -> assertDoesNotThrow(() -> interceptor.validate(rule, " "))
            );
        }

        @Test
        void whenPresentButInvalid_thenThrowsInvalidHeaderValueException() {
            var rule = HeaderRule.builder().headerName("X-Regex").required(true)
                    .validator(new RegexValidator("^v\\d+$")).build();

            assertThrows(InvalidHeaderValueException.class, () -> interceptor.validate(rule, "bad"));
        }

        @Test
        void whenPresentAndValid_thenPasses() {
            var rule = HeaderRule.builder().headerName("X-Regex").required(true)
                    .validator(new RegexValidator("^v\\d+$")).build();

            assertDoesNotThrow(() -> interceptor.validate(rule, "v1"));
        }
    }
}
