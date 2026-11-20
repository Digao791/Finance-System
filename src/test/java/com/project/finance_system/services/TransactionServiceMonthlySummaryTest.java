package com.project.finance_system.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.project.finance_system.dto.MonthlySummaryResponse;
import com.project.finance_system.model.Transaction;
import com.project.finance_system.model.TransactionType;
import com.project.finance_system.repositories.TransactionRepository;

class TransactionServiceMonthlySummaryTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository);

    @Test
    void summarizeMonthQueriesMonthRangeAndReturnsIncomeExpenseAndBalance() {
        YearMonth month = YearMonth.of(2026, 11);
        Transaction income = new Transaction(
            TransactionType.INCOME,
            new BigDecimal("2400.00"),
            LocalDate.of(2026, 11, 1),
            "Salary"
        );
        Transaction expense = new Transaction(
            TransactionType.EXPENSE,
            new BigDecimal("175.50"),
            LocalDate.of(2026, 11, 30),
            "Utilities"
        );
        when(transactionRepository.findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 12, 1)
        )).thenReturn(List.of(income, expense));

        MonthlySummaryResponse summary = transactionService.summarizeMonth(month);

        assertEquals(new MonthlySummaryResponse(
            "2026-11",
            new BigDecimal("2400.00"),
            new BigDecimal("175.50"),
            new BigDecimal("2224.50"),
            "BRL"
        ), summary);
        verify(transactionRepository).findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(
            LocalDate.of(2026, 11, 1),
            LocalDate.of(2026, 12, 1)
        );
    }

    @Test
    void summarizeMonthReturnsZeroTotalsWhenNoTransactionsExist() {
        YearMonth month = YearMonth.of(2026, 2);
        when(transactionRepository.findByTransactionDateGreaterThanEqualAndTransactionDateLessThan(
            LocalDate.of(2026, 2, 1),
            LocalDate.of(2026, 3, 1)
        )).thenReturn(List.of());

        assertEquals(new MonthlySummaryResponse(
            "2026-02",
            new BigDecimal("0.00"),
            new BigDecimal("0.00"),
            new BigDecimal("0.00"),
            "BRL"
        ), transactionService.summarizeMonth(month));
    }
}
