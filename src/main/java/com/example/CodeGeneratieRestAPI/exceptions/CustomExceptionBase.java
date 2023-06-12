package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;

public interface CustomExceptionBase {
    public HttpStatus getStatusCode();
}
