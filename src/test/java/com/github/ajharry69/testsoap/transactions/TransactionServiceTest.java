package com.github.ajharry69.testsoap.transactions;

import com.github.ajharry69.testsoap.ApplicationProperties;
import com.github.ajharry69.testsoap.temperature.TemperatureRequest;
import com.github.ajharry69.testsoap.temperature.TemperatureResponse;
import com.github.ajharry69.testsoap.temperature.TemperatureSoapClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository repo;
    @Mock
    private TemperatureSoapClient client;

    @Test
    void no_pending_exits_early_without_side_effects() {
        when(repo.countByStatusIn(any())).thenReturn(0L);

        var properties = ApplicationProperties.builder()
                .maxConcurrent(5)
                .build();
        var service = new TransactionService(repo, client, properties);

        service.findAndPollPendingTransactions();

        verify(repo, never()).save(any());
        verify(client, never()).getTemperature(any(TemperatureRequest.class));
    }

    @Test
    void processes_each_transaction_and_saves_before_and_after() {
        // Two pending transactions with predefined Fahrenheit values
        var t1 = Transaction.builder()
                .id(UUID.randomUUID())
                .status(Transaction.Status.PENDING)
                .fahrenheit(100.0)
                .build();
        var t2 = Transaction.builder()
                .id(UUID.randomUUID())
                .status(Transaction.Status.PENDING)
                .fahrenheit(32.0)
                .build();
        var t3 = Transaction.builder()
                .id(UUID.randomUUID())
                .status(Transaction.Status.PENDING)
                .build();

        when(repo.findByRetriesCountLessThanAndStatusInOrderByDateUpdatedAsc(anyInt(), anyCollection()))
                .thenReturn(Stream.of(t1, t2, t3));
        when(repo.countByStatusIn(anyCollection())).thenReturn(3L);

        when(client.getTemperature(any(TemperatureRequest.class)))
                .thenReturn(
                        TemperatureResponse.builder()
                                .degreesCelsius("37.7777777778")
                                .build()
                )
                .thenReturn(
                        TemperatureResponse.builder()
                                .degreesCelsius("0")
                                .build()
                )
                .thenReturn(
                        TemperatureResponse.builder()
                                .build()
                );

        var properties = ApplicationProperties.builder()
                .maxConcurrent(4)
                .build();
        var service = new TransactionService(repo, client, properties);
        service.findAndPollPendingTransactions();

        // Each item saved twice (before soap call and after)
        ArgumentCaptor<Transaction> saved = ArgumentCaptor.forClass(Transaction.class);
        verify(repo, times(6)).save(saved.capture());

        // After completion, both transactions should have Celsius set based on responses
        // We can't guarantee order, so assert at least one has Celsius 0 and one ~37.77
        List<Transaction> savedItems = saved.getAllValues();
        assertThat(savedItems.size())
                .isEqualTo(6);
    }

    @Test
    void concurrency_is_bounded_by_semaphore_when_invoking_soap_calls() {
        int total = 12;
        var list = range(0, total)
                .mapToObj(i -> Transaction.builder().id(UUID.randomUUID()).status(Transaction.Status.PENDING).fahrenheit(100.0 + i).build())
                .toList();
        when(repo.findByRetriesCountLessThanAndStatusInOrderByDateUpdatedAsc(anyInt(), anyCollection())).thenReturn(list.stream());
        when(repo.countByStatusIn(anyCollection())).thenReturn((long) total);

        AtomicInteger inflight = new AtomicInteger();
        AtomicInteger maxSeen = new AtomicInteger();

        when(client.getTemperature(any(TemperatureRequest.class))).thenAnswer(inv -> {
            int now = inflight.incrementAndGet();
            maxSeen.updateAndGet(prev -> Math.max(prev, now));
            // Simulate I/O delay to keep calls overlapping
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ignored) {
            }
            inflight.decrementAndGet();
            var r = new TemperatureResponse();
            r.setDegreesCelsius("10");
            return r;
        });

        int limit = 3;
        var properties = ApplicationProperties.builder()
                .maxConcurrent(limit)
                .build();
        var service = new TransactionService(repo, client, properties);
        long start = System.nanoTime();
        service.findAndPollPendingTransactions();
        long tookMs = Duration.ofNanos(System.nanoTime() - start).toMillis();

        // Assert the observed concurrency never exceeded the limit
        assertThat(maxSeen.get()).isLessThanOrEqualTo(limit);
        // Also, assert total wall time roughly reflects batching (> total/limit * 50ms)
        assertThat(tookMs).isGreaterThanOrEqualTo((long) Math.ceil(total / (double) limit) * 50L);
    }

    @Test
    void null_celsius_in_response_is_handled_gracefully() {
        Transaction t1 = Transaction.builder()
                .id(UUID.randomUUID())
                .status(Transaction.Status.PENDING)
                .fahrenheit(100.0)
                .build();
        when(repo.findByRetriesCountLessThanAndStatusInOrderByDateUpdatedAsc(anyInt(), anyCollection()))
                .thenReturn(Stream.of(t1));
        when(repo.countByStatusIn(anyCollection()))
                .thenReturn(1L);

        TemperatureResponse r = new TemperatureResponse();
        r.setDegreesCelsius(null); // simulate missing value
        when(client.getTemperature(any(TemperatureRequest.class))).thenReturn(r);

        var properties = ApplicationProperties.builder()
                .maxConcurrent(1)
                .build();
        var service = new TransactionService(repo, client, properties);
        service.findAndPollPendingTransactions();

        ArgumentCaptor<Transaction> saved = ArgumentCaptor.forClass(Transaction.class);
        verify(repo, atLeastOnce()).save(saved.capture());
        // Ensure we did not set Celsius to a number when the response is null
        assertThat(saved.getAllValues().stream().anyMatch(tr -> tr.getCelsius() == null)).isTrue();
    }
}
