package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAll() {
        return transactionService.getAll();
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable long id) {
        return transactionService.getById(id);
    }

    @GetMapping("/accounts/{accountId}")
    public Transaction getByAccountId(@PathVariable long accountId) {
        return transactionService.getByAccountId(accountId);
    }

    @PostMapping
    public Transaction add(@RequestBody TransactionRequestDTO transaction) {
        return transactionService.add(transaction);
    }
}
