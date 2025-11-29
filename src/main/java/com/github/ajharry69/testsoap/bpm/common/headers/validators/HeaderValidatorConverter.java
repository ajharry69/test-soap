package com.github.ajharry69.testsoap.bpm.common.headers.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Component
@ConfigurationPropertiesBinding
class HeaderValidatorConverter implements Converter<String, HeaderValidator> {

    @Override
    public HeaderValidator convert(String source) {
        try {
            return (HeaderValidator) Class.forName(source)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ClassNotFoundException
                 | InvocationTargetException
                 | InstantiationException
                 | IllegalAccessException
                 | NoSuchMethodException e) {
            log.error("Failed to create an instance of '{}' from '{}'.", HeaderValidator.class.getName(), source, e);
            return null;
        }
    }
}
