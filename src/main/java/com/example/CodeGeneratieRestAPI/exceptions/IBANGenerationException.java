package com.example.CodeGeneratieRestAPI.exceptions;

import java.io.Serial;

public class IBANGenerationException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String errorMessage = "Something went wrong while generating an IBAN: ";
    public IBANGenerationException(String message) {
        super(errorMessage + message);
    }
    public IBANGenerationException(String message, Throwable cause) {
        super(errorMessage + message, cause);
    }
}
