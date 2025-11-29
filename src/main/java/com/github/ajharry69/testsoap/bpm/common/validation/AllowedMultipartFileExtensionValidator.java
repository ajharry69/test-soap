package com.github.ajharry69.testsoap.bpm.common.validation;

import com.github.ajharry69.testsoap.bpm.config.ApplicationProperties;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class AllowedMultipartFileExtensionValidator extends ExtensionConstraintValidator<AllowedMultipartFileExtension, MultipartFile> {
    public AllowedMultipartFileExtensionValidator(ApplicationProperties properties) {
        super(properties);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true;

        var filename = file.getOriginalFilename();
        if (filename == null) return true;

        var ext = filename.substring(filename.lastIndexOf('.') + 1);
        var normalized = normalize(ext);
        var allowedExtensions = allowed();
        var isValid = allowedExtensions.contains(normalized);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid extension '%s'. Allowed extensions are: %s".formatted(ext, allowedExtensions))
                    .addConstraintViolation();
        }
        return isValid;
    }
}