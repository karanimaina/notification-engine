package com.ezra.notificationengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Metadata(
        String traceId,
        String idempotencyKey
) {
}
