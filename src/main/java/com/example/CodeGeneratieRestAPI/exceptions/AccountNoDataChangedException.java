package com.example.CodeGeneratieRestAPI.exceptions;

public class AccountNoDataChangedException extends RuntimeException {
    public AccountNoDataChangedException(String message) {
        super(message);
    }
}
