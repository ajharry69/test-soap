package com.github.ajharry69.testsoap.bpm.common.headers;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeaderValidationConfigurationTest {

    @Test
    void addInterceptors_registersHeaderValidationInterceptor() {
        var properties = new HeaderValidationProperties();
        var service = new HeaderValidatorService();
        var interceptor = new HeaderValidationInterceptor(properties, service);
        var config = new HeaderValidationConfiguration(interceptor);
        var registry = mock(InterceptorRegistry.class);
        var captor = ArgumentCaptor.forClass(HandlerInterceptor.class);
        when(registry.addInterceptor(captor.capture()))
                .thenReturn(null);

        config.addInterceptors(registry);

        var added = captor.getValue();
        assertAll(
                () -> assertNotNull(added),
                () -> assertInstanceOf(HeaderValidationInterceptor.class, added)
        );
    }
}
