package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "This transaction to or from this savings account is not valid")
public class TransactionTransferSavingsException extends RuntimeException {
    public TransactionTransferSavingsException(String message) {
        super(message);
    }
}
