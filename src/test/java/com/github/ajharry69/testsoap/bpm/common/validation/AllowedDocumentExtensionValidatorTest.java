package com.github.ajharry69.testsoap.bpm.common.validation;

import com.github.ajharry69.testsoap.bpm.config.ApplicationProperties;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class AllowedDocumentExtensionValidatorTest {

    @Nested
    class NonEmptyProps {
        private AllowedDocumentExtensionValidator validator;

        @BeforeEach
        void setUp() {
            var properties = mock(ApplicationProperties.class);
            when(properties.getAllowedExtensions())
                    .thenReturn("pdf, jpg , PnG");
            validator = new AllowedDocumentExtensionValidator(properties);
        }

        @ParameterizedTest
        @MethodSource
        void shouldFlagAsValid(String value) {
            assertTrue(validator.isValid(value, null));
        }

        static Stream<String> shouldFlagAsValid() {
            return Stream.of(null, "", "   ", "pdf", ".Pdf", " JPG ", "png");
        }

        @ParameterizedTest
        @MethodSource
        void shouldFlagAsInvalid(String value) {
            var context = mock(ConstraintValidatorContext.class);
            var node = mock(ConstraintViolationBuilder.class);
            when(context.buildConstraintViolationWithTemplate(anyString()))
                    .thenReturn(node);
            when(node.addConstraintViolation())
                    .thenReturn(context);
            var templateCaptor = ArgumentCaptor.forClass(String.class);

            var valid = validator.isValid(value, context);

            assertFalse(valid);
            verify(context)
                    .disableDefaultConstraintViolation();
            verify(context)
                    .buildConstraintViolationWithTemplate(templateCaptor.capture());
            var template = templateCaptor.getValue();
            assertTrue(template.startsWith("Invalid extension '%s'. Allowed extensions are: [".formatted(value)));
            assertTrue(template.contains("pdf"));
            assertTrue(template.contains("jpg"));
            assertTrue(template.contains("png"));
        }

        static Stream<String> shouldFlagAsInvalid() {
            return Stream.of("exe", ".", "docx");
        }
    }

    @Test
    void whenAllowedListEmpty_thenNonBlankValuesAreInvalid() {
        var emptyProps = mock(ApplicationProperties.class);
        when(emptyProps.getAllowedExtensions()).thenReturn("");
        var validator = new AllowedDocumentExtensionValidator(emptyProps);
        var context = mock(ConstraintValidatorContext.class);
        var node = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(node);
        when(node.addConstraintViolation())
                .thenReturn(context);

        assertAll(
                () -> {
                    var valid = validator.isValid("pdf", context);
                    verify(context)
                            .disableDefaultConstraintViolation();
                    var templateCaptor = ArgumentCaptor.forClass(String.class);
                    verify(context)
                            .buildConstraintViolationWithTemplate(templateCaptor.capture());

                    var template = templateCaptor.getValue();
                    assertFalse(valid);
                    assertEquals("Invalid extension 'pdf'. Allowed extensions are: []", template);
                },
                () -> assertTrue(validator.isValid(" ", null))
        );
    }
}
