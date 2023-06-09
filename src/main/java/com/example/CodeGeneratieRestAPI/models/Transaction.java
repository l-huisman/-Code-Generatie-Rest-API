package com.example.CodeGeneratieRestAPI.models;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"transactions\"")
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "from_iban", nullable = true)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "to_iban", nullable = true)
    private Account toAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transactionType")
    private TransactionType transactionType;

    private String label;
    private String description;
    private Float amount;
    private Float balance_before;

    @Column(name = "created_at")
    private Date createdAt;

    public Transaction(Account fromAccount, Account toAccount, Float amount, String label, String description, TransactionType transactionType) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.label = label;
        this.description = description;
        this.transactionType = transactionType;
        this.createdAt = new Date();
    }
}
