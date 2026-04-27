package com.ezra.notificationengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Customer(
        @NotBlank String customerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String pushToken
) {
}
