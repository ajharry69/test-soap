package com.github.ajharry69.testsoap.bpm.common.validation;

import com.github.ajharry69.testsoap.bpm.config.ApplicationProperties;
import jakarta.validation.ConstraintValidator;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
abstract class ExtensionConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
    private final ApplicationProperties properties;

    protected Set<String> allowed() {
        if (properties == null) return Collections.emptySet();

        var commaSeparatedExtensions = properties.getAllowedExtensions();
        if (!StringUtils.hasText(commaSeparatedExtensions)) return Collections.emptySet();

        return Arrays.stream(commaSeparatedExtensions.split(","))
                .map(this::normalize)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    protected String normalize(String ext) {
        var e = ext.trim();
        if (e.startsWith(".")) e = e.substring(1);
        return e.toLowerCase().trim();
    }
}