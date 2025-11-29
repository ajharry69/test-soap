package com.github.ajharry69.testsoap.bpm.common.validation;

import com.github.ajharry69.testsoap.bpm.DocumentTypeRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@AllArgsConstructor
class AllowedDocumentNameValidator implements ConstraintValidator<AllowedDocumentName, String> {
    private final DocumentTypeRepository repository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) return true;

        var isValid = repository.existsByDocumentNameAndActiveTrue(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("'%s' is not in the list of allowed document names".formatted(value))
                    .addConstraintViolation();
        }
        return isValid;
    }
}