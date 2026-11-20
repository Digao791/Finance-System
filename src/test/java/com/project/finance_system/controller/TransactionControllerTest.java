package com.project.finance_system.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.project.finance_system.dto.CreateTransactionRequest;
import com.project.finance_system.dto.TransactionResponse;
import com.project.finance_system.model.TransactionType;
import com.project.finance_system.services.TransactionService;

class TransactionControllerTest {

    private final TransactionService transactionService = mock(TransactionService.class);
    private final TransactionController transactionController = new TransactionController(transactionService);

    @Test
    void createDelegatesRequestAndReturnsServiceResponse() {
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.EXPENSE,
            new BigDecimal("20.50"),
            LocalDate.of(2026, 11, 20),
            "Lunch"
        );
        TransactionResponse expected = new TransactionResponse(
            3L,
            TransactionType.EXPENSE,
            new BigDecimal("20.50"),
            "BRL",
            LocalDate.of(2026, 11, 20),
            "Lunch"
        );
        when(transactionService.create(request)).thenReturn(expected);

        assertEquals(expected, transactionController.create(request));
        verify(transactionService).create(request);
    }

    @Test
    void findAllReturnsServiceResults() {
        List<TransactionResponse> expected = List.of(new TransactionResponse(
            3L,
            TransactionType.EXPENSE,
            new BigDecimal("20.50"),
            "BRL",
            LocalDate.of(2026, 11, 20),
            "Lunch"
        ));
        when(transactionService.findAll()).thenReturn(expected);

        assertEquals(expected, transactionController.findAll());
        verify(transactionService).findAll();
    }

    @Test
    void updateAndDeleteDelegateTransactionId() {
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.INCOME,
            new BigDecimal("100.00"),
            LocalDate.of(2026, 11, 20),
            "Refund"
        );
        TransactionResponse expected = new TransactionResponse(
            3L,
            TransactionType.INCOME,
            new BigDecimal("100.00"),
            "BRL",
            LocalDate.of(2026, 11, 20),
            "Refund"
        );
        when(transactionService.update(3L, request)).thenReturn(expected);

        assertEquals(expected, transactionController.update(3L, request));
        transactionController.delete(3L);

        verify(transactionService).update(3L, request);
        verify(transactionService).delete(3L);
    }
}
