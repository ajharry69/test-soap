package com.github.ajharry69.testsoap.bpm.common.headers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ajharry69.testsoap.bpm.dto.RequestPayload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;

@FunctionalInterface
public interface RequestMessageIdRetriever {
    String retrieveMessageID(HttpServletRequest request);

    @Slf4j
    @RequiredArgsConstructor
    class Default implements RequestMessageIdRetriever {
        private final ObjectMapper objectMapper;
        @Override
        public String retrieveMessageID(HttpServletRequest request) {
            MediaType mediaType;
            try {
                mediaType = MediaType.valueOf(request.getContentType());
            } catch (InvalidMediaTypeException ignore) {
                return null;
            }

            if (!mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                var payloadParam = request.getParameter("payload");
                if (StringUtils.hasText(payloadParam)) {
                    try {
                        RequestPayload<?> payload = objectMapper.readValue(payloadParam, new TypeReference<>() {
                        });
                        return payload.getMessageID();
                    } catch (JsonProcessingException e) {
                        log.error("Failed to retrieve messageID from '{}' request to '{}' of type '{}'",
                                request.getMethod(), request.getRequestURI(), request.getContentType(), e);
                    }
                }
                return null;
            }

            try {
                RequestPayload<?> payload = objectMapper.readValue(request.getReader(), new TypeReference<>() {
                });
                return payload.getMessageID();
            } catch (IOException e) {
                log.error("Failed to retrieve messageID from '{}' request to '{}' of type '{}'",
                        request.getMethod(), request.getRequestURI(), request.getContentType(), e);
                return null;
            }
        }
    }
}
