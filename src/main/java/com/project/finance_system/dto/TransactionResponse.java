package com.project.finance_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.project.finance_system.model.TransactionType;

public record TransactionResponse(
    Long id,
    TransactionType type,
    BigDecimal amount,
    String currency,
    LocalDate transactionDate,
    String description
) {
    
}
