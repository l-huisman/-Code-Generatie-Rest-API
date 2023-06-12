package com.example.CodeGeneratieRestAPI.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class IBANGenerationException extends RuntimeException implements CustomExceptionBase {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String errorMessage = "Something went wrong while generating an IBAN: ";

    public IBANGenerationException(String message) {
        super(errorMessage + message);
    }

    public IBANGenerationException(String message, Throwable cause) {
        super(errorMessage + message, cause);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
