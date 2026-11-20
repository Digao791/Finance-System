package com.project.finance_system.services;

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
import com.project.finance_system.model.Transaction;
import com.project.finance_system.model.TransactionType;
import com.project.finance_system.repositories.TransactionRepository;

class TransactionServiceTest {

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository);

    @Test
    void createSavesTransactionAndMapsSavedValuesToResponse() {
        LocalDate date = LocalDate.of(2026, 11, 20);
        CreateTransactionRequest request = new CreateTransactionRequest(
            TransactionType.EXPENSE,
            new BigDecimal("125.50"),
            date,
            "Groceries"
        );
        when(transactionRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(Transaction.class)))
            .thenAnswer(invocation -> {
                Transaction transaction = invocation.getArgument(0);
                transaction.setId(12L);
                return transaction;
            });

        TransactionResponse response = transactionService.create(request);

        assertEquals(new TransactionResponse(
            12L,
            TransactionType.EXPENSE,
            new BigDecimal("125.50"),
            "BRL",
            date,
            "Groceries"
        ), response);
        verify(transactionRepository).saveAndFlush(org.mockito.ArgumentMatchers.argThat(transaction ->
            transaction.getType() == TransactionType.EXPENSE
                && transaction.getAmount().equals(new BigDecimal("125.50"))
                && transaction.getTransactionDate().equals(date)
                && transaction.getDescription().equals("Groceries")
        ));
    }

    @Test
    void findAllMapsEveryTransactionToResponse() {
        Transaction income = new Transaction(
            TransactionType.INCOME,
            new BigDecimal("2400.00"),
            LocalDate.of(2026, 11, 1),
            "Salary"
        );
        income.setId(1L);
        Transaction expense = new Transaction(
            TransactionType.EXPENSE,
            new BigDecimal("35.75"),
            LocalDate.of(2026, 11, 2),
            "Lunch"
        );
        expense.setId(2L);
        when(transactionRepository.findAll()).thenReturn(List.of(income, expense));

        List<TransactionResponse> responses = transactionService.findAll();

        assertEquals(List.of(
            new TransactionResponse(
                1L, TransactionType.INCOME, new BigDecimal("2400.00"), "BRL",
                LocalDate.of(2026, 11, 1), "Salary"
            ),
            new TransactionResponse(
                2L, TransactionType.EXPENSE, new BigDecimal("35.75"), "BRL",
                LocalDate.of(2026, 11, 2), "Lunch"
            )
        ), responses);
    }

    @Test
    void findAllReturnsEmptyListWhenRepositoryHasNoTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), transactionService.findAll());
    }
}
