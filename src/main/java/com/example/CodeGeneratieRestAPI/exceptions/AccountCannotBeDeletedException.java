package com.example.CodeGeneratieRestAPI.exceptions;

public class AccountCannotBeDeletedException extends RuntimeException {
    public AccountCannotBeDeletedException(String message) {
        super(message);
    }
}
