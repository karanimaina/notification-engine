package com.ezra.notificationengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Notification(
        String templateCode,
        @NotEmpty List<NotificationChannel> channels,
        String locale,
        String priority,
        Map<String, Object> variables
) {
    public Notification {
        channels = channels == null ? List.of() : List.copyOf(channels);
        variables = variables == null ? Map.of() : Map.copyOf(variables);
    }
}
