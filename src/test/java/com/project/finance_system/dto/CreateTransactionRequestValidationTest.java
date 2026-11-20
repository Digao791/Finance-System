package com.project.finance_system.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.project.finance_system.model.TransactionType;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CreateTransactionRequestValidationTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @AfterEach
    void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void acceptsValidTransactionAtMaximumAmountPrecision() {
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.INCOME,
            new BigDecimal("99999999999999999.99"),
            LocalDate.of(2026, 11, 20),
            "Monthly income"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsMissingRequiredFields() {
        CreateTransactionRequest request = new CreateTransactionRequest(null, null, null, null);

        Set<String> invalidProperties = validator.validate(request).stream()
            .map(violation -> violation.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertTrue(invalidProperties.containsAll(Set.of("type", "amount", "transactionDate")));
    }

    @Test
    void rejectsNonPositiveAmountsAndAmountsOutsideSupportedPrecision() {
        assertInvalidAmount(new BigDecimal("0"));
        assertInvalidAmount(new BigDecimal("-0.01"));
        assertInvalidAmount(new BigDecimal("100000000000000000.00"));
        assertInvalidAmount(new BigDecimal("1.001"));
    }

    @Test
    void rejectsDescriptionsLongerThan255Characters() {
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.EXPENSE,
            new BigDecimal("1.00"),
            LocalDate.of(2026, 11, 20),
            "x".repeat(256)
        );

        assertFalse(validator.validate(request).isEmpty());
        assertTrue(validator.validate(request).stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("description")));
    }

    private void assertInvalidAmount(BigDecimal amount) {
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.EXPENSE,
            amount,
            LocalDate.of(2026, 11, 20),
            null
        );

        assertTrue(validator.validate(request).stream()
            .anyMatch(violation -> violation.getPropertyPath().toString().equals("amount")));
    }
}
