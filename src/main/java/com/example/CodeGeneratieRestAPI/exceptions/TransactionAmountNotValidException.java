package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction amount is not valid")
public class TransactionAmountNotValidException extends RuntimeException {
    public TransactionAmountNotValidException(String message) {
        super(message);
    }
}
