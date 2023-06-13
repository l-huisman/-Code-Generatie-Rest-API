package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Username or password is empty")
public class LoginDTOException extends RuntimeException implements CustomExceptionBase{
    public LoginDTOException(String message) {
        super(message);
    }

    @Override
    public org.springframework.http.HttpStatus getStatusCode() {
        return org.springframework.http.HttpStatus.BAD_REQUEST;
    }
}
