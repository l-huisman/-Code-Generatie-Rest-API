package com.example.CodeGeneratieRestAPI.dtos;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.TransactionType;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionResponseDTO {
    private String fromAccountIban;
    private String toAccountIban;
    private TransactionType transactionType;
    private Float amount;
    private String label;
    private String description;
    private Date createdAt;

    // Empty constructor
    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(Transaction transaction) {
        this.fromAccountIban = transaction.getFromAccount() != null ? transaction.getFromAccount().getIban() : null;
        this.toAccountIban = transaction.getToAccount() != null ? transaction.getToAccount().getIban() : null;
        this.transactionType = transaction.getTransactionType();
        this.amount = transaction.getAmount();
        this.label = transaction.getLabel();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreatedAt();
    }
}
