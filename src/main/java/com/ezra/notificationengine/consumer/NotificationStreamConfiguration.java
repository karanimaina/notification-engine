package com.ezra.notificationengine.consumer;

import com.ezra.notificationengine.dto.NotificationEvent;
import com.ezra.notificationengine.service.NotificationProcessingService;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class NotificationStreamConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NotificationStreamConfiguration.class);

    @Bean
    public Consumer<NotificationEvent> notifications(NotificationProcessingService processingService) {
        return event -> consume("notifications", processingService, event);
    }

    @Bean
    public Consumer<NotificationEvent> loanNotifications(NotificationProcessingService processingService) {
        return event -> consume("loan-notifications", processingService, event);
    }

    private static void consume(String logicalTopic, NotificationProcessingService processingService, NotificationEvent event) {
        try {
            processingService.process(event, logicalTopic);
        } catch (ConstraintViolationException ex) {
            log.warn("notification.invalid topic={} eventId={} reason={}", logicalTopic, event.eventId(), ex.getMessage());
        }
    }
}
