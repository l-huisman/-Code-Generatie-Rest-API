package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Something went wrong while creating an account")
public class AccountCreationException extends RuntimeException{
    public AccountCreationException(String message) {
        super(message);
    }
}
