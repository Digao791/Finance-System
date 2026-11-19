package com.project.finance_system.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.project.finance_system.model.TransactionType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTransactionRequest(
    @NotNull TransactionType type,
    @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal amount,
    @NotNull LocalDate transactionDate,
    @Size(max = 255) String description
) {
    
}
