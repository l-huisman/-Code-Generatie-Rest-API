package com.example.CodeGeneratieRestAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"transactions\"")
public class Transaction extends BaseEntity {
    private User user;
    private Account from_account;
    private Account to_account;
    private Float amount;
    private TransactionType email;
    private Float balance_before;
    private String description;
    private String created_at;
}
