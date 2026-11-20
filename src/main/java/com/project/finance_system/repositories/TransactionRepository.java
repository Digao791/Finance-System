package com.project.finance_system.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.finance_system.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByTransactionDateBetween(
        LocalDate from, LocalDate to, Sort sort
    );

    List<Transaction> findByTransactionDateGreaterThanEqual(
        LocalDate from, Sort sort
    );

    List<Transaction> findByTransactionDateLessThanEqual(
        LocalDate from, Sort sort
    );
}

