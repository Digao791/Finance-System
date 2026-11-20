package com.project.finance_system.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.finance_system.dto.CreateTransactionRequest;
import com.project.finance_system.dto.TransactionResponse;
import com.project.finance_system.model.Transaction;
import com.project.finance_system.repositories.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional 
    public TransactionResponse create(CreateTransactionRequest request){
        Transaction transaction = new Transaction(
            request.type(), 
            request.amount(), 
            request.transactionDate(), 
            request.description());
        
        Transaction saved = transactionRepository.saveAndFlush(transaction);

        return toResponse(saved);
    }

    @Transactional 
    public TransactionResponse update(Long id, CreateTransactionRequest request){
        Transaction transaction = findRequired(id);
        transaction.updateTransaction(
            request.type(), 
            request.amount(), 
            request.transactionDate(), 
            request.description()
        );

        return toResponse(transaction);
    }

    @Transactional 
    public void delete(Long id){
        Transaction transaction = findRequired(id);
        transactionRepository.delete(transaction);
    }

    public List<TransactionResponse> findAll(){
        List<TransactionResponse> transactions = transactionRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();

        return transactions;
    }

    public List<TransactionResponse> findAll(LocalDate from, LocalDate to){
        if(from != null && to != null && from.isAfter(to)){
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "From must be on or before to"
            );
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "transactionDate", "id");
        List<Transaction> transactions;
        if(from != null && to != null){
            transactions = transactionRepository.findByTransactionDateBetween(from, to, sort);
        }else if(from != null){
            transactions = transactionRepository.findByTransactionDateGreaterThanEqual(from, sort);
        }else if(to != null){
            transactions = transactionRepository.findByTransactionDateLessThanEqual(to, sort);
        }else{
            transactions = transactionRepository.findAll(sort);
        }

        return toResponse(transactions);
    }

    private Transaction findRequired(Long id){
        Transaction transaction = transactionRepository.findById(id)
                                    .orElseThrow(() -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Not Found"
                                    ));

        return transaction;
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

    private List<TransactionResponse> toResponse(List<Transaction> transactions){
        return transactions.stream()
                    .map(this::toResponse)
                    .toList();
    }
    
}
