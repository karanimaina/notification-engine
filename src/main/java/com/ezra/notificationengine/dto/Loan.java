package com.ezra.notificationengine.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Loan(
        String loanId,
        String productId,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        Integer daysPastDue
) {
}
