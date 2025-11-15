package com.github.ajharry69.testsoap.locations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    Stream<Location> findByRetriesCountLessThanAndCelsiusIsNullOrFahrenheitIsNullOrderByDateUpdatedAsc(
            int retriesCount
    );

    long countByRetriesCountLessThanAndCelsiusIsNullOrFahrenheitIsNull(
            int retriesCount
    );
}