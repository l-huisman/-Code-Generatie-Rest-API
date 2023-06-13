package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST, reason = "Not all required fields are filled in")
public class UserDTOException extends RuntimeException implements CustomExceptionBase {
    public UserDTOException(String message) {
        super(message);
    }

    @Override
    public org.springframework.http.HttpStatus getStatusCode() {
        return org.springframework.http.HttpStatus.BAD_REQUEST;
    }
}
