package com.github.ajharry69.testsoap;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.SimpleHttpComponents5MessageSender;

@Configuration
class SoapClientConfig {
    @Bean
    public Jaxb2Marshaller jaxbMarshaller() {
        var marshaller = new Jaxb2Marshaller();
        // Scan ALL packages that contain JAXB models
        marshaller.setPackagesToScan(
                "com.github.ajharry69.testsoap"
        );
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller, SimpleHttpComponents5MessageSender messageSender) {
        var template = new WebServiceTemplate();
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);

        // Use the high-performance, pooled HTTP client
        template.setMessageSender(messageSender);
        return template;
    }

    @Bean
    public SimpleHttpComponents5MessageSender httpComponentsMessageSender() {
        // 1. Configure Connection Pooling (CRITICAL for high traffic)
        var pool = new PoolingHttpClientConnectionManager();
        pool.setMaxTotal(200); // Max total connections in the pool
        pool.setDefaultMaxPerRoute(50); // Max concurrent connections to a single host (e.g., domain.com)

        // 2. Configure Timeouts (NEVER go to production without these)
        var config = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(10))
                .build();

        // 3. Build the client
        var client = HttpClients.custom()
                .setConnectionManager(pool)
                .setDefaultRequestConfig(config)
                .build();

        return new SimpleHttpComponents5MessageSender(client);
    }
}