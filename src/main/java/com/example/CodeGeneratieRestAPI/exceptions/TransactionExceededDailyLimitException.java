package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction exceeded daily limit")
public class TransactionExceededDailyLimitException extends RuntimeException{
    public TransactionExceededDailyLimitException(String message) {
        super(message);
    }
}
