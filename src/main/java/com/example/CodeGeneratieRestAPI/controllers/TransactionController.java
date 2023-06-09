package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionResponseDTO;
import com.example.CodeGeneratieRestAPI.helpers.ServiceHelper;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    ModelMapper modelMapper;
    @Autowired
    private TransactionService transactionService;

    public TransactionController() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date start_date, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date end_date, @RequestParam String search) {
        try {
            User user = ServiceHelper.getLoggedInUser();
            List<Transaction> transactions = transactionService.getAll(user, start_date, end_date, search);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "All transactions retrieved", Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getAllByUserId() {
        try {
            User user = ServiceHelper.getLoggedInUser();

            List<Transaction> transactions = transactionService.getAllByUser(user);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "All transactions retrieved", Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        try {
            User user = ServiceHelper.getLoggedInUser();
            //  Retrieve the data
            Transaction transaction = transactionService.getById(user, id);

            //  Return the data
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Transaction retrieved", new TransactionResponseDTO(transaction)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/accounts/{iban}")
    public ResponseEntity<ApiResponse> getByAccountIban(@PathVariable String iban, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date start_date, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date end_date, @RequestParam String search) {
        try {
            User user = ServiceHelper.getLoggedInUser();

            List<Transaction> transactions = transactionService.getAllByAccountIban(user, iban, start_date, end_date, search);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "All transactions retrieved", Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/owns/{id}")
    public ResponseEntity<ApiResponse<String>> transactionIsOwnedByUser(@PathVariable Long id) {
        try {
            User user = ServiceHelper.getLoggedInUser();

            transactionService.transactionIsOwnedByUser(user, id);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "You own this transaction"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> add(@RequestBody(required = true) TransactionRequestDTO transactionIn) {
        try {
            User user = ServiceHelper.getLoggedInUser();

            //  Retrieve the data
            Transaction transaction = transactionService.add(user, transactionIn);

            //  Return the data
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Transaction retrieved", new TransactionResponseDTO(transaction)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
