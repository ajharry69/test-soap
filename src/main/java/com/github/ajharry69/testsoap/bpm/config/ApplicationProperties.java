package com.github.ajharry69.testsoap.bpm.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * @project kcb-sfa-bpm-st
 * @author Chris Watia - KENCONT211390
 */
@Data
@Component
public class ApplicationProperties {
    @Value("${Document.allowed_extensions:pdf,doc,docx,png,jpeg,jpg}")
    private String allowedExtensions;
}
