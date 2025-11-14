package com.github.ajharry69.testsoap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.OffsetDateTime;
import java.util.Optional;

@SpringBootApplication
@EnableRetry
@EnableScheduling
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class Application {

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
