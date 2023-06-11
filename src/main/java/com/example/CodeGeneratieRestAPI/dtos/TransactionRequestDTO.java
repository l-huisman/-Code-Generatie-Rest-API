package com.example.CodeGeneratieRestAPI.dtos;

import lombok.Data;

@Data
public class TransactionRequestDTO {
    private String fromAccountIban;
    private String toAccountIban;
    private String transactionType;
    private Float amount;
    private String label;
    private String description;

    public TransactionRequestDTO() {
    }

    public TransactionRequestDTO(String fromAccountIban, String toAccountIban, String transactionType, Float amount, String label, String description) {
        this.fromAccountIban = fromAccountIban;
        this.toAccountIban = toAccountIban;
        this.transactionType = transactionType;
        this.amount = amount;
        this.label = label;
        this.description = description;
    }
}
