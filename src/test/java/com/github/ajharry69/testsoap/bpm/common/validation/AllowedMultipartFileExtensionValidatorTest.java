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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AllowedMultipartFileExtensionValidatorTest {
    @Test
    void shouldFlagAsInvalidWhenAllowedListEmpty() {
        var emptyProps = mock(ApplicationProperties.class);
        when(emptyProps.getAllowedExtensions())
                .thenReturn("");
        var mf = new MockMultipartFile("file", "name.pdf", "application/pdf", new byte[]{1});
        var validator = new AllowedMultipartFileExtensionValidator(emptyProps);
        var context = mock(ConstraintValidatorContext.class);
        var node = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(node);
        when(node.addConstraintViolation())
                .thenReturn(context);
        var templateCaptor = ArgumentCaptor.forClass(String.class);

        var valid = validator.isValid(mf, context);

        assertFalse(valid);
        verify(context)
                .disableDefaultConstraintViolation();
        verify(context)
                .buildConstraintViolationWithTemplate(templateCaptor.capture());
        assertEquals("Invalid extension 'pdf'. Allowed extensions are: []", templateCaptor.getValue());
    }

    @Nested
    class NonEmptyProps {
        private AllowedMultipartFileExtensionValidator validator;

        static Stream<MultipartFile> shouldFlagAsValid() {
            var nullFileName = mock(MultipartFile.class);
            when(nullFileName.isEmpty()).thenReturn(false);
            when(nullFileName.getOriginalFilename()).thenReturn(null);

            return Stream.of(
                    null,
                    nullFileName,
                    new MockMultipartFile("file", "", "application/octet-stream", new byte[0]),
                    new MockMultipartFile("file", "png", "application/octet-stream", new byte[]{1}),
                    new MockMultipartFile("file", "name.PDF", "application/pdf", new byte[]{1}),
                    new MockMultipartFile("file", "name. PnG ", "application/octet-stream", new byte[]{1})
            );
        }

        static Stream<MultipartFile> shouldFlagAsInvalid() {
            return Stream.of(
                    new MockMultipartFile("file", "name", "application/octet-stream", new byte[]{1}),
                    new MockMultipartFile("file", "name.exe", "application/octet-stream", new byte[]{1})
            );
        }

        @BeforeEach
        void setUp() {
            var properties = mock(ApplicationProperties.class);
            when(properties.getAllowedExtensions())
                    .thenReturn("pdf, jpg , PnG");
            validator = new AllowedMultipartFileExtensionValidator(properties);
        }

        @ParameterizedTest
        @MethodSource
        void shouldFlagAsValid(MultipartFile file) {
            assertTrue(validator.isValid(file, null));
        }

        @ParameterizedTest
        @MethodSource
        void shouldFlagAsInvalid(MultipartFile file) {
            var context = mock(ConstraintValidatorContext.class);
            var node = mock(ConstraintViolationBuilder.class);
            when(context.buildConstraintViolationWithTemplate(anyString()))
                    .thenReturn(node);
            when(node.addConstraintViolation())
                    .thenReturn(context);
            var templateCaptor = ArgumentCaptor.forClass(String.class);

            var valid = validator.isValid(file, context);

            assertFalse(valid);
            verify(context)
                    .disableDefaultConstraintViolation();
            verify(context)
                    .buildConstraintViolationWithTemplate(templateCaptor.capture());

        }
    }
}
