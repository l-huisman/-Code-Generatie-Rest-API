package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionResponseDTO;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
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
    public List<TransactionResponseDTO> getAll() {
        List<Transaction> transactions = transactionService.getAll();

        return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/accounts/{iban}")
    public List<TransactionResponseDTO> getByAccountIban(@PathVariable String iban) {
        List<Transaction> transactions = transactionService.getAllByAccountIban(iban);

        return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
    }

    @PostMapping
    public Transaction add(@RequestBody TransactionRequestDTO transactionIn) {
        Transaction transaction = modelMapper.map(transactionIn, Transaction.class);

        return transactionService.add(transaction);
    }
}
