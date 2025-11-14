package com.github.ajharry69.testsoap;

import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Builder
@ConfigurationProperties(prefix = "app.transactions")
public record ApplicationProperties(
        int maxConcurrent
) {
}
