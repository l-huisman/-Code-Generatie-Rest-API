package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction exceeded absolute limit")
public class TransactionExceededAbsoluteLimitException extends RuntimeException {
    public TransactionExceededAbsoluteLimitException(String message) {
        super(message);
    }
}
