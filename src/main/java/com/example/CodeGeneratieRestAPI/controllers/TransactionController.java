package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionResponseDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public List<TransactionResponseDTO> getAll(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date start_date, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date end_date, @RequestParam String search) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();

            List<Transaction> transactions = transactionService.getAll(start_date, end_date, search, username);

            return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/user")
    public List<TransactionResponseDTO> getAllByUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();

            List<Transaction> transactions = transactionService.getAllByUserId(username);

            return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/{id}")
    public TransactionResponseDTO getById(@PathVariable long id) {
        try {
            //  Retrieve the data
            Transaction transaction = transactionService.getById(id, "admin");

            //  Return the data
            return modelMapper.map(transaction, TransactionResponseDTO.class);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/accounts/{iban}")
    public List<TransactionResponseDTO> getByAccountIban(@PathVariable String iban, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date start_date, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date end_date, @RequestParam String search) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();

            System.out.println(iban);

            List<Transaction> transactions = transactionService.getAllByAccountIban(iban, start_date, end_date, search, username);

            return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @PostMapping
    public TransactionResponseDTO add(@RequestBody(required = true) TransactionRequestDTO transactionIn) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();

            System.out.println(transactionIn.getAmount());
            //  Retrieve the data
            Transaction transaction = transactionService.add(transactionIn, username);

            //  Return the data
            return modelMapper.map(transaction, TransactionResponseDTO.class);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e.getMessage());
            return null;
        }
    }
}
