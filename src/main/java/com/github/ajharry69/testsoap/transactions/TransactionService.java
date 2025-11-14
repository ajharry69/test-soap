package com.github.ajharry69.testsoap.transactions;

import com.github.ajharry69.testsoap.ApplicationProperties;
import com.github.ajharry69.testsoap.temperature.TemperatureRequest;
import com.github.ajharry69.testsoap.temperature.TemperatureSoapClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
class TransactionService {
    private final TransactionRepository repository;
    private final TemperatureSoapClient soapClient;
    private final ApplicationProperties properties;

    @Transactional(readOnly = true)
    @Scheduled(fixedDelay = 10, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void findAndPollPendingTransactions() {
        log.info("[Scheduler] Waking up to find pending transactions...");
        Set<Transaction.Status> pendingStatuses = Set.of(Transaction.Status.PENDING);
        Stream<Transaction> pendingTransactions = repository.findByStatusInOrderByDateUpdatedAsc(pendingStatuses);
        var pendingTransactionsCount = repository.countByStatusIn(pendingStatuses);

        if (pendingTransactionsCount == 0) {
            log.info("[Scheduler] No pending transactions found. Going back to sleep.");
            return;
        }

        log.info("[Scheduler] Found {} pending transactions. Processing...", pendingTransactionsCount);

        var permits = new Semaphore(Math.max(1, properties.maxConcurrent()));
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            pendingTransactions.map(transaction ->
                    CompletableFuture.runAsync(() -> {
                        permits.acquireUninterruptibly();
                        try {
                            var f = transaction.getFahrenheit();
                            if (f == null) {
                                f = new Random().nextDouble(40, 1000);
                            }
                            var temperatureRequest = TemperatureRequest.builder()
                                    .fahrenheitReading(String.valueOf(f))
                                    .build();

                            transaction.setFahrenheit(Double.parseDouble(temperatureRequest.getFahrenheitReading()));
                            repository.save(transaction);

                            var temperatureResponse = soapClient.getTemperature(temperatureRequest);
                            var d = temperatureResponse.getDegreesCelsius();
                            if (d != null) {
                                transaction.setCelsius(Double.parseDouble(d));
                            }
                            Transaction.Status[] statuses = Transaction.Status.values();
                            transaction.setStatus(statuses[new Random().nextInt(statuses.length)]);
                            repository.save(transaction);
                        } finally {
                            permits.release();
                        }
                    }, exec)
            ).forEach(CompletableFuture::join);
        }
    }
}