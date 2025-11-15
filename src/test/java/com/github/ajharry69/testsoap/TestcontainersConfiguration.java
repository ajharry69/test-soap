package com.github.ajharry69.testsoap;

import com.github.ajharry69.testsoap.transactions.Transaction;
import com.github.ajharry69.testsoap.transactions.TransactionRepository;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
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
import java.util.concurrent.Semaphore;

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
    @ConditionalOnBooleanProperty(name = "app.seed.transactions")
    CommandLineRunner seedTransactions(TransactionRepository repo, HikariDataSource ds) {
        return args -> {
            int rows = Integer.parseInt(System.getProperty("seed.rows", "10000"));
            int maxConcurrent = ds.getMaximumPoolSize(); // honor pool capacity
            log.info("Seeding {} rows with {} concurrent threads...", rows, maxConcurrent);
            var sem = new Semaphore(Math.max(1, maxConcurrent));
            try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
                List<CompletableFuture<Void>> inFlight = new ArrayList<>(maxConcurrent);
                for (int i = 0; i < rows; i++) {
                    sem.acquireUninterruptibly();
                    var t = Transaction.builder()
                            .orderNumber("ORD-" + i)
                            .status(Transaction.Status.PENDING)
                            .build();
                    var f = CompletableFuture.runAsync(() -> {
                        try {
                            repo.save(t);
                        } finally {
                            sem.release();
                        }
                    }, exec);
                    inFlight.add(f);
                    if (inFlight.size() >= maxConcurrent) {
                        // join one batch and remove completed to keep memory bounded
                        CompletableFuture.anyOf(inFlight.toArray(new CompletableFuture[0])).join();
                        inFlight.removeIf(CompletableFuture::isDone);
                    }
                }
                // join remaining
                CompletableFuture.allOf(inFlight.toArray(new CompletableFuture[0])).join();
            }
        };
    }

}
