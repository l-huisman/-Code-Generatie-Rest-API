package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"transactions\"")
public class Transaction extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable=true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="iban", nullable=false)
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="iban", nullable=true)
    private Account toAccount;

    @Enumerated(EnumType.STRING)
    @Column(name="transactionType")
    private TransactionType transactionType;

    private String label;
    private String description;
    private Float amount;
    private Float balance_before;

    @Column(name="created_at")
    private String createdAt;
}
