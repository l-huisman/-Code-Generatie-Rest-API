package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "This account is not accessible")
public class AccountNotAccessibleException extends RuntimeException {
    public AccountNotAccessibleException(String message) {
        super(message);
    }
}
