package com.ezra.notificationengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificationEvent(
        @NotBlank String eventId,
        @NotBlank String eventType,
        @NotNull Instant eventTime,
        @NotBlank String source,
        @NotNull @Valid Customer customer,
        @Valid Loan loan,
        @NotNull @Valid Notification notification,
        @Valid Metadata metadata
) {
}
