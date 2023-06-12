package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "There was an error while validating the password, try again later or contact the administrator")
public class PasswordValidationException extends RuntimeException implements CustomExceptionBase {
    public PasswordValidationException(String message) {
        super(message);
    }

    @Override
    public org.springframework.http.HttpStatus getStatusCode() {
        return org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
