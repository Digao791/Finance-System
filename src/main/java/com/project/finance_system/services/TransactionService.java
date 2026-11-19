package com.project.finance_system.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.finance_system.dto.CreateTransactionRequest;
import com.project.finance_system.dto.TransactionResponse;
import com.project.finance_system.model.Transaction;
import com.project.finance_system.repositories.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponse create(CreateTransactionRequest request){
        Transaction transaction = new Transaction(
            request.type(), 
            request.amount(), 
            request.transactionDate(), 
            request.description());
        
        Transaction saved = transactionRepository.saveAndFlush(transaction);

        return toResponse(saved);
    }

    public List<TransactionResponse> findAll(){
        List<TransactionResponse> transactions = transactionRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();

        return transactions;
    }

    private TransactionResponse toResponse(Transaction transaction){
        return new TransactionResponse(
            transaction.getId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getCurrency(),
            transaction.getTransactionDate(),
            transaction.getDescription()
        );
    }
    
}
