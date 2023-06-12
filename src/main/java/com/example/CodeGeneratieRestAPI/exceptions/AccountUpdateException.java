package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;

public class AccountUpdateException extends RuntimeException implements CustomExceptionBase {
    public AccountUpdateException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
