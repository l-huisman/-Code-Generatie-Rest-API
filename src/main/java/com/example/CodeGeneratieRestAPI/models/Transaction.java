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
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID", nullable=true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FROM_ACCOUNT_ID", nullable=true)
    private Account from_account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TO_ACCOUNT_ID", nullable=true)
    private Account to_account;

    @Enumerated(EnumType.STRING)
    @Column(name="transactionType")
    private TransactionType transactionType;

    private String label;
    private String description;
    private Float amount;
    private Float balance_before;
    private String created_at;
}
