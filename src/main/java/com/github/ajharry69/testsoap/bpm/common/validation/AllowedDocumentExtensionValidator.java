package com.github.ajharry69.testsoap.bpm.common.validation;

import com.github.ajharry69.testsoap.bpm.config.ApplicationProperties;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
class AllowedDocumentExtensionValidator extends ExtensionConstraintValidator<AllowedDocumentExtension, String> {
    public AllowedDocumentExtensionValidator(ApplicationProperties properties) {
        super(properties);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(value)) return true;

        var normalized = normalize(value);

        var allowedExtensions = allowed();
        boolean isValid = allowedExtensions.contains(normalized);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid extension '%s'. Allowed extensions are: %s".formatted(value, allowedExtensions))
                    .addConstraintViolation();
        }
        return isValid;
    }
}
