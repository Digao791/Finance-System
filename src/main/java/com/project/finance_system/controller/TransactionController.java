package com.project.finance_system.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.project.finance_system.dto.CreateTransactionRequest;
import com.project.finance_system.dto.TransactionResponse;
import com.project.finance_system.services.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/transactions")
@RequiredArgsConstructor 
public class TransactionController {
    
    private final TransactionService transactionService;

    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request){
        return transactionService.create(request);
    }

    @GetMapping 
    public List<TransactionResponse> findAll(
        @RequestParam(name = "from", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(name = "to", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
     ){
        return transactionService.findAll(from, to);
    }

    @PutMapping ("/{id}")
    public TransactionResponse update(@PathVariable Long id,
            @Valid @RequestBody CreateTransactionRequest request){

        return transactionService.update(id, request);
    }

    @DeleteMapping ("/{id}")
    @ResponseStatus (HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
         transactionService.delete(id);
    }
    
}
