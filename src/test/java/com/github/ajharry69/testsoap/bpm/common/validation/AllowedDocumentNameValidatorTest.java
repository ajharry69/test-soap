package com.github.ajharry69.testsoap.bpm.common.validation;

import com.github.ajharry69.testsoap.bpm.DocumentTypeRepository;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AllowedDocumentNameValidatorTest {

    private DocumentTypeRepository repository;
    private AllowedDocumentNameValidator validator;

    @BeforeEach
    void setUp() {
        repository = mock(DocumentTypeRepository.class);
        validator = new AllowedDocumentNameValidator(repository);
    }

    @ParameterizedTest
    @MethodSource
    void shouldFlagBlankOrNullInputAsValid(String input) {
        var valid = validator.isValid(input, mock(ConstraintValidatorContext.class));

        assertTrue(valid);
        verify(repository, never())
                .existsByDocumentNameAndActiveTrue(anyString());
    }

    static Stream<String> shouldFlagBlankOrNullInputAsValid() {
        return Stream.of(null, "", "   ");
    }

    @Test
    void whenRepositoryReturnsTrue_thenValid() {
        when(repository.existsByDocumentNameAndActiveTrue("National ID"))
                .thenReturn(true);

        var result = validator.isValid("National ID", mock(ConstraintValidatorContext.class));

        assertTrue(result);
        verify(repository)
                .existsByDocumentNameAndActiveTrue("National ID");
    }

    @Test
    void whenRepositoryReturnsFalse_thenInvalid_andBuildsCustomMessage() {
        when(repository.existsByDocumentNameAndActiveTrue("Passport"))
                .thenReturn(false);
        var context = mock(ConstraintValidatorContext.class);
        var node = mock(ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(node);
        when(node.addConstraintViolation())
                .thenReturn(context);

        var valid = validator.isValid("Passport", context);

        assertFalse(valid);
        verify(context)
                .disableDefaultConstraintViolation();
        verify(context)
                .buildConstraintViolationWithTemplate("'Passport' is not in the list of allowed document names");
    }
}
