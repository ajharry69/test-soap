# Test Soap API

## Overview

This project demonstrates several approaches to consuming public SOAP APIs in a modern Spring Boot application. In
addition, it showcases how to leverage Java 21 virtual threads to optimize performance in two common I/O-heavy
scenarios:

- Seeding data into a PostgreSQL database using high concurrency with low overhead
- Running scheduled polling/update tasks that call remote SOAP services with bounded concurrency and a maximum retry
  strategy

The codebase is intentionally small and focused, so you can compare patterns, wire up SOAP clients, and observe
performance characteristics when switching between platform threads and virtual threads.

## Technology Stack

- Language: Java 21 (Project Loom virtual threads enabled)
- Build/Package Manager: Maven (with Maven Wrapper `mvnw`/`mvnw.cmd`)
- Frameworks/Libraries:
    - Spring Boot 3.5.x
    - Spring Web (REST controller)
    - Spring Web Services (SOAP client)
    - Spring Data JPA (PostgreSQL)
    - Spring Scheduling and Spring Retry (for scheduled tasks and retries)
    - JAXB (XML binding)
    - Apache HttpClient 5 (HTTP client used by SOAP stack)
- Database: PostgreSQL
- Testing: JUnit 5, Spring Boot Test, Testcontainers (PostgreSQL)

See `pom.xml` for the full list of dependencies and plugin configuration.

## Project Highlights

- Virtual threads are enabled globally via `spring.threads.virtual.enabled=true` and used explicitly within a scheduled
  job to parallelize SOAP calls with controlled concurrency.
- A scheduled worker finds locations that need temperature updates and processes them concurrently, calling an external
  SOAP endpoint to convert temperature values, then persists updates.
- Concurrency is bounded by a semaphore whose permits are driven by a configurable property (
  `app.locations.max-concurrent`).

## Entry Points and Key Components

- Application entry point: `com.github.ajharry69.testsoap.Application`
    - Annotations: `@SpringBootApplication`, `@EnableScheduling`, `@EnableRetry`, and `@EnableConfigurationProperties`

- REST endpoint: `GET /temperature?fahrenheitReading=...`
    - Controller: `com.github.ajharry69.testsoap.temperature.TemperatureController`
    - Service: `TemperatureService`
    - SOAP client: `TemperatureSoapClient` with implementation `TemperatureSoapClientImpl`

- Scheduled job: `LocationService.updateLocationTemperatures()`
    - Runs on a cron schedule (default every minute) and uses a virtual-thread-per-task executor to process locations
      concurrently
    - Bounded by `app.locations.max-concurrent` permits
    - Updates each `Location` with Fahrenheit/Celsius and increments a retry counter; pending items are selected while
      `retriesCount < 6`

> NOTE: The specific public SOAP endpoints/WSDLs used by the `TemperatureSoapClientImpl` and any related clients should
> be documented. See TODOs below.

## Requirements

- Java 21 (JDK 21+)
- Maven 3.9+ (or simply use the bundled Maven Wrapper)
- Docker (for running tests with Testcontainers)

## Configuration

Default application properties (see `src/main/resources/application.properties`):

```
spring.application.name=test-soap
spring.threads.virtual.enabled=true
app.locations.max-concurrent=20
# Cron for the scheduled temperature updater (default: every minute)
app.jobs.update-location-temperatures.cron=0 */1 * * * *
```

Environment variables and JVM/system properties you may care about:

- `SPRING_PROFILES_ACTIVE` — to select a profile if you add more (e.g., `dev`, `prod`)
- `APP_LOCATIONS_MAX_CONCURRENT` — can override `app.locations.max-concurrent` via relaxed binding
- `APP_JOBS_UPDATE_LOCATION_TEMPERATURES_CRON` — can override the cron expression for the scheduled job
- Standard Spring datasource properties (see TODO) if you want to run against a real PostgreSQL locally:
    - `SPRING_DATASOURCE_URL`
    - `SPRING_DATASOURCE_USERNAME`
    - `SPRING_DATASOURCE_PASSWORD`

### Database

At runtime, no datasource properties are provided by default. Tests rely on Testcontainers to provision PostgreSQL
automatically.

- For development/test data seeding you can use the flag found in `src/test/resources/application-dev.properties`:
    - `app.seed.locations=true`

- TODO: Provide example `application-dev.properties` under `src/main/resources/` with PostgreSQL configuration for local
  development.
- TODO: Document schema/migrations strategy if you add Flyway or Liquibase.

## Setup and Run

Using the Maven Wrapper (recommended):

```shell
./mvnw clean package
./mvnw spring-boot:run
```

Or with a local Maven installation:

```
mvn clean package
mvn spring-boot:run
```

Run using the test source set with Testcontainers (recommended for development):

```shell
./mvnw spring-boot:test-run
```

Notes for test-run:

- This goal uses the test classpath and `src/test/resources`, so Testcontainers will start PostgreSQL for you.
- With the `dev` profile active, settings like `app.seed.locations=true` (from
  `src/test/resources/application-dev.properties`) will seed sample data automatically.

Once running, try:

```shell
curl "http://localhost:8080/temperature?fahrenheitReading=98.6"
```

## Common Scripts and Tasks

- Build: `./mvnw clean package`
- Run: `./mvnw spring-boot:run`
- Run from test source set (dev, Testcontainers): `./mvnw spring-boot:test-run`
- Run tests: `./mvnw test`
- Run with debug logs (example): `./mvnw spring-boot:run -Dspring-boot.run.arguments=--logging.level.root=DEBUG`

> The project uses the Maven Wrapper, so you don't need to install Maven. On Windows, use `mvnw.cmd`.

## Testing

Tests are JUnit 5-based and rely on Spring Boot Test and Testcontainers. When you run the test suite, a disposable
PostgreSQL container will be started automatically.

```shell
./mvnw test
```

Notes:

- Testcontainers require Docker to be available and running.
- Example test configuration lives under `src/test/resources/` (e.g., `application-dev.properties`).

## Observing Virtual Threads in Action

The scheduled worker in `LocationService` uses a virtual-thread-per-task executor:

- A semaphore limits concurrency to `app.locations.max-concurrent` (default 20)
- Each task performs: generate/read a Fahrenheit reading → call a SOAP service to convert → persist updates
- Pending locations are polled every minute (configurable via cron); items with `retriesCount < 6` are selected and
  updated

You can experiment by changing `app.locations.max-concurrent` and measuring throughput/latency under load.
