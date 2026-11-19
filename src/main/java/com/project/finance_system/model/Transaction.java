package com.project.finance_system.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table (name = "transactions")
@Data 
public class Transaction {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 7)
    private TransactionType type;

    @Column (nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column (nullable = false, length = 3)
    private String currency = "BRL";

    @Column (nullable = false, name = "transaction_date")
    private LocalDate transactionDate;

    @Column (length = 255)
    private String description;

    protected Transaction(){}
    public Transaction(TransactionType type, 
                        BigDecimal amount,
                        LocalDate transactionDate,
                        String description
    ){
        this.type = type;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
    }
}
