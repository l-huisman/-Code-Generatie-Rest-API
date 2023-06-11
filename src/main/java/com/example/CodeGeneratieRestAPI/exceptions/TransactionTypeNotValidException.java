package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction type is not valid")
public class TransactionTypeNotValidException extends RuntimeException {
    public TransactionTypeNotValidException(String message) {
        super(message);
    }
}
