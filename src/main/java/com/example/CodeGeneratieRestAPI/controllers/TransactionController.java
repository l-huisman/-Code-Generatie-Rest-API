package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionResponseDTO;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<TransactionResponseDTO> getAll(@RequestParam Date startDate, @RequestParam Date endDate, @RequestParam String fromAccountIban, @RequestParam String search) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        List<Transaction> transactions = transactionService.getAll(startDate, endDate, fromAccountIban, search, search, username);

        return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
    }

    @GetMapping("/{id}")
    public Transaction getById(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        return transactionService.getById(id, username);
    }

    @GetMapping("/accounts/{iban}")
    public List<TransactionResponseDTO> getByAccountIban(@PathVariable String iban, @RequestParam Date startDate, @RequestParam Date endDate, @RequestParam String search) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        List<Transaction> transactions = transactionService.getAllByAccountIban(iban, startDate, endDate, search, search, username);

        return Arrays.asList(modelMapper.map(transactions, TransactionResponseDTO[].class));
    }

    @PostMapping
    public Transaction add(@RequestBody TransactionRequestDTO transactionIn) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        
        Transaction transaction = modelMapper.map(transactionIn, Transaction.class);

        return transactionService.add(transaction, username);
    }
}
