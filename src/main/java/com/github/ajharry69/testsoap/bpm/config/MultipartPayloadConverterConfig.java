package com.github.ajharry69.testsoap.bpm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ajharry69.testsoap.bpm.dto.RequestPayload;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Configuration
@AllArgsConstructor
public class MultipartPayloadConverterConfig implements WebMvcConfigurer {
    private final ObjectMapper objectMapper;

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addFirst(new RequestPayloadJacksonConverter(objectMapper));
    }

    static class RequestPayloadJacksonConverter extends MappingJackson2HttpMessageConverter {
        RequestPayloadJacksonConverter(ObjectMapper mapper) {
            super(mapper);
        }

        @Override
        public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
            if (type instanceof ParameterizedType parameterizedType) {
                return canRead(parameterizedType.getRawType(), contextClass, mediaType);
            }

            Class<?> raw;
            try {
                raw = (Class<?>) type;
            } catch (ClassCastException e) {
                raw = null;
            }

            boolean isRequestPayload = raw != null && RequestPayload.class.isAssignableFrom(raw);
            if (!isRequestPayload) {
                return super.canRead(type, contextClass, mediaType);
            }

            if (mediaType == null
                    || mediaType.isWildcardType()
                    || MediaType.TEXT_PLAIN.includes(mediaType)
                    || MediaType.APPLICATION_OCTET_STREAM.includes(mediaType)) {
                return true;
            }

            return super.canRead(type, contextClass, mediaType);
        }
    }
}
