package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "You do not own this account")
public class AccountNotOwnedException extends RuntimeException{
    public AccountNotOwnedException(String message) {
        super(message);
    }
}
