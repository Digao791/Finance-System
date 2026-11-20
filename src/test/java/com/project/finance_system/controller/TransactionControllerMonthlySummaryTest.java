package com.project.finance_system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.project.finance_system.dto.MonthlySummaryResponse;
import com.project.finance_system.services.TransactionService;

class TransactionControllerMonthlySummaryTest {

    private final TransactionService transactionService = mock(TransactionService.class);
    private final TransactionController transactionController = new TransactionController(transactionService);

    @Test
    void summarizeMonthParsesValidMonthAndDelegatesToService() {
        YearMonth month = YearMonth.of(2026, 11);
        MonthlySummaryResponse expected = new MonthlySummaryResponse(
            "2026-11",
            new BigDecimal("2400.00"),
            new BigDecimal("175.50"),
            new BigDecimal("2224.50"),
            "BRL"
        );
        when(transactionService.summarizeMonth(month)).thenReturn(expected);

        assertEquals(expected, transactionController.summarizeMonth("2026-11"));
        verify(transactionService).summarizeMonth(month);
    }

    @Test
    void summarizeMonthRejectsMalformedMonth() {
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> transactionController.summarizeMonth("2026/11")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void summarizeMonthRejectsInvalidCalendarMonth() {
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> transactionController.summarizeMonth("2026-13")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
