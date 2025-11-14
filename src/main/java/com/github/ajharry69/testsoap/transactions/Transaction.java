package com.github.ajharry69.testsoap.transactions;

import com.github.ajharry69.testsoap.temperature.TemperatureRequest;
import com.github.ajharry69.testsoap.temperature.TemperatureSoapClient;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Entity
@Table(
        name = "transactions",
        indexes = {
                @Index(columnList = "status"),
                @Index(columnList = "date_created"),
                @Index(columnList = "date_updated")
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    private String orderNumber;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    @CreatedDate
    @Column(updatable = false)
    private OffsetDateTime dateCreated;
    @LastModifiedDate
    @Column(insertable = false)
    private OffsetDateTime dateUpdated;
    private Double fahrenheit;
    private Double celsius;

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED
    }
}

@Service
@Slf4j
@AllArgsConstructor
class TransactionService {
    private final TransactionRepository repository;
    private final TemperatureSoapClient soapClient;

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

        log.info("[Scheduler] Found {} pending transactions. Polling all in parallel...", pendingTransactionsCount);

        pendingTransactions.parallel().forEach(transaction -> {
            var f = transaction.getFahrenheit();
            if (f == null) {
                f = new Random().nextDouble(40, 1000);
            }
            var temperatureRequest = TemperatureRequest.builder()
                    .fahrenheitReading(String.valueOf(f))
                    .build();

            transaction.setFahrenheit(Double.parseDouble(temperatureRequest.getFahrenheitReading()));
            repository.save(transaction);

            Thread thread = Thread.currentThread();
            log.info(
                    "ThreadGroup={}, Thread={}, isVirtual={}, isDaemon={}",
                    thread.getThreadGroup(),
                    thread,
                    thread.isVirtual(),
                    thread.isDaemon());

            var sleepDuration = Duration.ofSeconds(new Random().nextLong(2, 10));
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                log.error("[Scheduler Task] Error sleeping for {}.", sleepDuration, e);
            } finally {
                var temperatureResponse = soapClient.getTemperature(temperatureRequest);
                transaction.setCelsius(Double.parseDouble(temperatureResponse.getDegreesCelsius()));
                Transaction.Status[] statuses = Transaction.Status.values();
                transaction.setStatus(statuses[new Random().nextInt(statuses.length)]);
                repository.save(transaction);
            }
        });
    }
}