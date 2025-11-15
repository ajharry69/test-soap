package com.github.ajharry69.testsoap.locations;

import com.github.ajharry69.testsoap.ApplicationProperties;
import com.github.ajharry69.testsoap.temperature.TemperatureRequest;
import com.github.ajharry69.testsoap.temperature.TemperatureSoapClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@AllArgsConstructor
class LocationService {
    private static final int MAX_RETRIES = 6;
    private final LocationRepository repository;
    private final TemperatureSoapClient soapClient;
    private final ApplicationProperties properties;

    @Transactional(readOnly = true)
    @Scheduled(cron = "${app.jobs.update-location-temperatures.cron:0 */1 * * * *}", timeUnit = TimeUnit.SECONDS)
    public void updateLocationTemperatures() {
        log.info("Looking up locations with missing or outdated temperatures...");
        var locationsPendingTemperatureUpdates = repository.findByRetriesCountLessThanAndCelsiusIsNullOrFahrenheitIsNullOrderByDateUpdatedAsc(MAX_RETRIES);
        var locationsPendingTemperatureUpdatesCount = repository.countByRetriesCountLessThanAndCelsiusIsNullOrFahrenheitIsNull(MAX_RETRIES);

        if (locationsPendingTemperatureUpdatesCount == 0) {
            log.info("Skipping updates. No locations with missing or outdated temperatures found.");
            return;
        }

        log.info("Proceeding with the update - found {} locations with missing or outdated temperatures...", locationsPendingTemperatureUpdatesCount);

        var permits = new Semaphore(Math.max(1, properties.maxConcurrent()));
        var updatesCount = new AtomicInteger(0);
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            locationsPendingTemperatureUpdates.map(location ->
                    CompletableFuture.runAsync(() -> {
                        permits.acquireUninterruptibly();
                        try {
                            var f = location.getFahrenheit();
                            if (f == null) {
                                f = new Random().nextDouble(40, 1000);
                            }
                            var temperatureRequest = TemperatureRequest.builder()
                                    .fahrenheitReading(String.valueOf(f))
                                    .build();

                            location.setFahrenheit(Double.parseDouble(temperatureRequest.getFahrenheitReading()));
                            repository.save(location);

                            var temperatureResponse = soapClient.getTemperature(temperatureRequest);
                            var d = temperatureResponse.getDegreesCelsius();
                            if (d != null) {
                                location.setCelsius(Double.parseDouble(d));
                            }
                            location.setRetriesCount(location.getRetriesCount() + 1);
                            repository.save(location);
                            updatesCount.incrementAndGet();
                        } finally {
                            permits.release();
                        }
                    }, exec)
            ).forEach(CompletableFuture::join);
        } finally {
            log.info("Finished updating temperatures for {} locations.", updatesCount.get());
        }
    }
}