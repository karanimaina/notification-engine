# Notification Engine

`notification-engine` is an event-driven Spring Boot service that consumes Kafka messages from upstream services and turns them into standardized customer notification workflows.

The service currently focuses on:
- consuming notification requests from multiple producer topics,
- validating and normalizing event payloads,
- preparing channel-based dispatch processing (Email, SMS, Push),
- providing a foundation for idempotency, retries, and delivery tracking.

## Why This Service Exists

In the lending platform, multiple services (for example customer and loan services) need to notify customers at different lifecycle stages.  
This project centralizes notification ingestion and processing so communication rules are consistent, observable, and easy to evolve.

## Topic Contracts

Upstream producer destinations:
- Customer service: `notifications`
- Loan service: `loan-notifications`

This engine consumes both topics using Spring Cloud Stream function bindings:
- `notifications-in-0` -> destination `notifications`
- `loanNotifications-in-0` -> destination `loan-notifications`

Consumer group:
- `notification-engine-v1`

## High-Level Architecture

1. **Kafka Consumers**  
   `NotificationStreamConfiguration` defines two `Consumer<NotificationEvent>` beans:
   - `notifications(...)`
   - `loanNotifications(...)`

2. **Processing Service**  
   `NotificationProcessingService`:
   - validates incoming events via Jakarta Validation,
   - derives idempotency key (`metadata.idempotencyKey` fallback to `eventId`),
   - logs accepted events and channel-level dispatch stubs.

3. **Canonical DTO Model**  
   Incoming JSON is mapped into typed records under `dto`:
   - `NotificationEvent`
   - `Customer`
   - `Loan`
   - `Notification`
   - `Metadata`
   - `NotificationChannel`

## Event Payload Shape

The engine expects a canonical JSON event similar to:

```json
{
  "eventId": "evt-1001",
  "eventType": "LOAN_CREATED",
  "eventTime": "2026-04-27T10:15:30Z",
  "source": "loan-service",
  "customer": {
    "customerId": "CUST-001",
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane@example.com",
    "phone": "+254700000000",
    "pushToken": "device-token"
  },
  "loan": {
    "loanId": "LN-001",
    "productId": "PRD-30D",
    "amount": 12000,
    "currency": "KES",
    "dueDate": "2026-05-10",
    "daysPastDue": 0
  },
  "notification": {
    "templateCode": "LOAN_CREATED_V1",
    "channels": ["EMAIL", "SMS"],
    "locale": "en",
    "priority": "NORMAL",
    "variables": {
      "dueDate": "2026-05-10",
      "amount": "12000"
    }
  },
  "metadata": {
    "traceId": "trace-abc-123",
    "idempotencyKey": "idem-evt-1001"
  }
}
```

Minimum required data:
- `eventId`
- `eventType`
- `eventTime`
- `source`
- `customer.customerId`
- non-empty `notification.channels`

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Spring Cloud Stream (Kafka binder)
- Jakarta Validation
- Maven Wrapper
- JUnit 5 + Embedded Kafka test support

## Configuration

Main config: `src/main/resources/application.yml`

Important properties:
- `spring.cloud.function.definition=notifications;loanNotifications`
- topic destinations and consumer group bindings
- `spring.cloud.stream.kafka.binder.brokers=${KAFKA_BROKERS:localhost:9092}`

To use an external Kafka cluster:

```bash
export KAFKA_BROKERS="kafka-1:9092,kafka-2:9092"
```

## Run Locally

Build and test:

```bash
./mvnw test
```

Run application:

```bash
./mvnw spring-boot:run
```

Package jar:

```bash
./mvnw clean package
```

## Current Status

Implemented:
- multi-topic Kafka consumers (`notifications`, `loan-notifications`)
- event validation and basic processing workflow
- canonical notification DTO model
- starter test coverage for service validation and Spring context load

Planned next:
- persistent idempotency store
- template management and message rendering
- real channel adapters (Email/SMS/Push providers)
- retry, DLQ, and delivery state persistence
- operational APIs and metrics dashboards

## Repository Structure

- `src/main/java/com/ezra/notificationengine/consumer` - stream consumer configuration
- `src/main/java/com/ezra/notificationengine/service` - processing logic
- `src/main/java/com/ezra/notificationengine/dto` - canonical event contracts
- `src/main/resources` - runtime configuration
- `src/test` - unit and integration tests

## Contribution Notes

When extending this service:
- keep inbound payloads backward compatible,
- validate all externally sourced fields,
- preserve idempotent behavior for at-least-once Kafka delivery,
- prefer additive schema and event changes to avoid producer breakage.
