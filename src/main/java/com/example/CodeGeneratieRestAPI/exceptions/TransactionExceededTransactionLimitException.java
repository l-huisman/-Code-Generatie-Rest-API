package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction exceeded transaction limit")
public class TransactionExceededTransactionLimitException extends RuntimeException{
    public TransactionExceededTransactionLimitException(String message) {
        super(message);
    }
}
