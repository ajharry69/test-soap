package com.github.ajharry69.testsoap;

import com.github.ajharry69.testsoap.transactions.Transaction;
import com.github.ajharry69.testsoap.transactions.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
class TestcontainersConfiguration {

    @Bean
    @RestartScope
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        var image = DockerImageName.parse("postgis/postgis:17-3.5-alpine")
                .asCompatibleSubstituteFor("postgres");
        return new PostgreSQLContainer<>(image);
    }

    @Bean
    CommandLineRunner seedTransactions(TransactionRepository repository) {
        var faker = new Faker();
        return args -> {
            try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
                List<CompletableFuture<Void>> transactions = new ArrayList<>(1_000_000);
                for (int i = 0; i < 1_000_000; i++) {
                    var transaction = Transaction.builder()
                            .orderNumber(faker.finance().creditCard())
                            .status(faker.options().option(Transaction.Status.class))
                            .build();
                    transactions.add(CompletableFuture.runAsync(() -> {
                        Thread thread = Thread.currentThread();
                        log.info("Thread={}, isVirtual={}", thread, thread.isVirtual());
                        repository.save(transaction);
                    }, exec));
                }
                transactions.forEach(CompletableFuture::join);
            }
        };
    }

}
