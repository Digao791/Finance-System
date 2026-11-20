package com.project.finance_system.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.project.finance_system.dto.CreateTransactionRequest;
import com.project.finance_system.dto.TransactionResponse;
import com.project.finance_system.model.Transaction;
import com.project.finance_system.model.TransactionType;
import com.project.finance_system.repositories.TransactionRepository;

class TransactionServiceUpdateDeleteTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository);

    @Test
    void updateMutatesExistingTransactionAndReturnsUpdatedValues() {
        Transaction transaction = new Transaction(
            TransactionType.EXPENSE,
            new BigDecimal("45.00"),
            LocalDate.of(2026, 11, 1),
            "Old description"
        );
        transaction.setId(7L);
        transaction.setCurrency("USD");
        when(transactionRepository.findById(7L)).thenReturn(Optional.of(transaction));

        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.INCOME,
            new BigDecimal("125.75"),
            LocalDate.of(2026, 11, 20),
            "Updated description"
        );

        TransactionResponse response = transactionService.update(7L, request);

        assertEquals(new TransactionResponse(
            7L,
            TransactionType.INCOME,
            new BigDecimal("125.75"),
            "USD",
            LocalDate.of(2026, 11, 20),
            "Updated description"
        ), response);
        verify(transactionRepository).findById(7L);
    }

    @Test
    void deleteRemovesTheTransactionFoundById() {
        Transaction transaction = new Transaction(
            TransactionType.EXPENSE,
            new BigDecimal("45.00"),
            LocalDate.of(2026, 11, 1),
            "Groceries"
        );
        when(transactionRepository.findById(7L)).thenReturn(Optional.of(transaction));

        transactionService.delete(7L);

        verify(transactionRepository).delete(transaction);
    }

    @Test
    void updateAndDeleteReturnNotFoundWhenTransactionDoesNotExist() {
        when(transactionRepository.findById(7L)).thenReturn(Optional.empty());
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.INCOME,
            new BigDecimal("125.75"),
            LocalDate.of(2026, 11, 20),
            "Updated description"
        );

        ResponseStatusException updateException = assertThrows(
            ResponseStatusException.class,
            () -> transactionService.update(7L, request)
        );
        ResponseStatusException deleteException = assertThrows(
            ResponseStatusException.class,
            () -> transactionService.delete(7L)
        );

        assertEquals(HttpStatus.NOT_FOUND, updateException.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, deleteException.getStatusCode());
    }
}
