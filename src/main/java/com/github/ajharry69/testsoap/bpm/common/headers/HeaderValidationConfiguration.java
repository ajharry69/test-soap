package com.github.ajharry69.testsoap.bpm.common.headers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(HeaderValidationProperties.class)
class HeaderValidationConfiguration implements WebMvcConfigurer {
    private final HeaderValidationProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HeaderValidationInterceptor(properties));
    }
}