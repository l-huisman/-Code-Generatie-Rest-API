package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transaction exceeded absolute limit")
public class TransactionExceededAbsoluteLimitException extends RuntimeException implements CustomExceptionBase {
    public TransactionExceededAbsoluteLimitException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
