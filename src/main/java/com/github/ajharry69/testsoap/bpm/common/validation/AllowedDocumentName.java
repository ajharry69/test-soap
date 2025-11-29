package com.github.ajharry69.testsoap.bpm.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = AllowedDocumentNameValidator.class)
@Documented
public @interface AllowedDocumentName {
    String message() default "Invalid document name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}