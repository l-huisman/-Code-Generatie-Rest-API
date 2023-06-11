package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// User creation exception
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "User could not be created, try again later or contact the administrator")
public class UserCreationException extends RuntimeException implements CustomExceptionBase{
    public UserCreationException(String message) {
        super(message);
    }
    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}