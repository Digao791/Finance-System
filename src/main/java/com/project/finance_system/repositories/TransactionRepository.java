package com.project.finance_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.finance_system.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
}
