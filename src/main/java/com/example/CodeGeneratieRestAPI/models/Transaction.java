package com.example.CodeGeneratieRestAPI.models;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_iban", nullable = false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
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
}
