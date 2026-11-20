package com.project.finance_system.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.project.finance_system.repositories.TransactionRepository;

class TransactionServiceDateFilterTest {

    private static final Sort DATE_AND_ID_DESCENDING =
        Sort.by(Sort.Direction.DESC, "transactionDate", "id");

    private final TransactionRepository transactionRepository = mock(TransactionRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository);

    @Test
    void findAllUsesInclusiveBetweenQueryWhenBothDatesAreProvided() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 30);
        when(transactionRepository.findByTransactionDateBetween(from, to, DATE_AND_ID_DESCENDING))
            .thenReturn(List.of());

        assertEquals(List.of(), transactionService.findAll(from, to));

        verify(transactionRepository).findByTransactionDateBetween(from, to, DATE_AND_ID_DESCENDING);
    }

    @Test
    void findAllUsesInclusiveLowerBoundWhenOnlyFromDateIsProvided() {
        LocalDate from = LocalDate.of(2026, 11, 1);
        when(transactionRepository.findByTransactionDateGreaterThanEqual(from, DATE_AND_ID_DESCENDING))
            .thenReturn(List.of());

        assertEquals(List.of(), transactionService.findAll(from, null));

        verify(transactionRepository).findByTransactionDateGreaterThanEqual(from, DATE_AND_ID_DESCENDING);
    }

    @Test
    void findAllUsesInclusiveUpperBoundWhenOnlyToDateIsProvided() {
        LocalDate to = LocalDate.of(2026, 11, 30);
        when(transactionRepository.findByTransactionDateLessThanEqual(to, DATE_AND_ID_DESCENDING))
            .thenReturn(List.of());

        assertEquals(List.of(), transactionService.findAll(null, to));

        verify(transactionRepository).findByTransactionDateLessThanEqual(to, DATE_AND_ID_DESCENDING);
    }

    @Test
    void findAllUsesSortedRepositoryQueryWhenNoDatesAreProvided() {
        when(transactionRepository.findAll(DATE_AND_ID_DESCENDING)).thenReturn(List.of());

        assertEquals(List.of(), transactionService.findAll(null, null));

        verify(transactionRepository).findAll(DATE_AND_ID_DESCENDING);
    }

    @Test
    void findAllRejectsReversedDateRangeWithoutQueryingRepository() {
        LocalDate from = LocalDate.of(2026, 11, 30);
        LocalDate to = LocalDate.of(2026, 11, 1);

        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> transactionService.findAll(from, to)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(transactionRepository);
    }
}
