package com.example.CodeGeneratieRestAPI.exceptions;

import com.example.CodeGeneratieRestAPI.exceptions.AccountCreationException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotAccessibleException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotFoundException;
import com.example.CodeGeneratieRestAPI.exceptions.IBANGenerationException;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //  Manually handle the exceptions that are thrown by the API
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountNotFoundException(AccountNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, e.getMessage()));
    }
    @ExceptionHandler(AccountNotAccessibleException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountNotAccessibleException(AccountNotAccessibleException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
    }
    @ExceptionHandler(AccountCreationException.class)
    public ResponseEntity<ApiResponse<String>> handleAccountCreationException(AccountCreationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Error whilst creating the account: " + e.getMessage()));
    }
    @ExceptionHandler(IBANGenerationException.class)
    public ResponseEntity<ApiResponse<String>> handleIBANGenerationException(IBANGenerationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Error whilst generating the IBAN: " + e.getMessage()));
    }


    //  Handle all exceptions that are not handled by other handlers
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "An error occurred: " + e.getMessage()));
    }
}