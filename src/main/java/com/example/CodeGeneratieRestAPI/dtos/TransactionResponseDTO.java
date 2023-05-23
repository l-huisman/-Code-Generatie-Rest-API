package com.example.CodeGeneratieRestAPI.dtos;

import com.example.CodeGeneratieRestAPI.models.TransactionType;

public class TransactionResponseDTO {
    private long id;
    private long fromAccountIban;
    private long toAccountIban;
    private TransactionType transactionType;
    private Float amount;
    private String label;
    private String description;
}
