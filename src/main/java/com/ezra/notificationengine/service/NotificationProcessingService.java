package com.ezra.notificationengine.service;

import com.ezra.notificationengine.dto.NotificationChannel;
import com.ezra.notificationengine.dto.NotificationEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationProcessingService {

    private static final Logger log = LoggerFactory.getLogger(NotificationProcessingService.class);

    private final Validator validator;

    public NotificationProcessingService(Validator validator) {
        this.validator = validator;
    }

    public void process(NotificationEvent event, String sourceTopic) {
        Set<ConstraintViolation<NotificationEvent>> violations = validator.validate(event);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(v -> v.getPropertyPath() + " " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new ConstraintViolationException(message, violations);
        }
        String idempotencyKey = resolveIdempotencyKey(event);
        String traceId = event.metadata() != null ? event.metadata().traceId() : null;

        log.info(
                "notification.accepted traceId={} eventId={} idempotencyKey={} sourceTopic={} eventType={} channels={}",
                traceId,
                event.eventId(),
                idempotencyKey,
                sourceTopic,
                event.eventType(),
                event.notification().channels());

        for (NotificationChannel channel : event.notification().channels()) {
            log.debug(
                    "notification.dispatch.stub customerId={} channel={}",
                    event.customer().customerId(),
                    channel);
        }
    }

    private static String resolveIdempotencyKey(NotificationEvent event) {
        if (event.metadata() != null
                && event.metadata().idempotencyKey() != null
                && !event.metadata().idempotencyKey().isBlank()) {
            return event.metadata().idempotencyKey().trim();
        }
        return event.eventId();
    }
}
