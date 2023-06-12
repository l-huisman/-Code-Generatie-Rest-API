package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, reason = "There was an error while hashing the password, try again later or contact the administrator")
public class PasswordHashingException extends RuntimeException implements CustomExceptionBase{
    public PasswordHashingException(String message) {
        super(message);
    }
    @Override
    public org.springframework.http.HttpStatus getStatusCode() {
        return org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
