package com.ezra.notificationengine.service;

import com.ezra.notificationengine.dto.CustomerPayload;
import com.ezra.notificationengine.dto.Metadata;
import com.ezra.notificationengine.dto.NotificationChannel;
import com.ezra.notificationengine.dto.NotificationEvent;
import com.ezra.notificationengine.dto.Notification;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationProcessingServiceTest {

    private Validator validator;
    private NotificationProcessingService service;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        service = new NotificationProcessingService(validator);
    }

    @Test
    void processThrowsWhenCustomerIdMissing() {
        NotificationEvent event = new NotificationEvent(
                "e1",
                "LOAN_CREATED",
                Instant.parse("2026-04-27T10:00:00Z"),
                "loan-service",
                new CustomerPayload("", "A", "B", "a@b.c", "+1", null),
                null,
                new Notification("T1", List.of(NotificationChannel.EMAIL), "en", "NORMAL", null),
                new Metadata("t1", null));

        assertThatThrownBy(() -> service.process(event, "notifications")).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void processAcceptsMinimalValidEvent() {
        NotificationEvent event = new NotificationEvent(
                "e1",
                "LOAN_CREATED",
                Instant.parse("2026-04-27T10:00:00Z"),
                "loan-service",
                new CustomerPayload("c1", "A", "B", "a@b.c", "+1", null),
                null,
                new Notification("T1", List.of(NotificationChannel.EMAIL), "en", "NORMAL", null),
                new Metadata("t1", "idem-1"));

        service.process(event, "notifications");
    }
}
