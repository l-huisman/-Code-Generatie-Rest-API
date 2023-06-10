package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Transaction to or from account is not valid")
public class TransactionAccountNotValidException extends RuntimeException {
    public TransactionAccountNotValidException(String message) {
        super(message);
    }
}
