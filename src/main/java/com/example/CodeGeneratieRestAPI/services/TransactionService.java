package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<Transaction> getAll() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction add(TransactionRequestDTO transaction) {
        Account fromAccount = accountRepository.findById(transaction.getFromAccountId()).get();

        if (fromAccount.getBalance() < transaction.getAmount()) {
            throw new RuntimeException("This account does not have enough balance to complete this transaction.");
        }

        //Check if the user owns this account or is an admin

        //Check if the account is a savings account and if the transaction is not a deposit

        //Check if the transaction is a transfer and if there is a toAccountId

        //Check if the transaction amount didn't exceed the transaction limit

        //Check if the transaction amount didn't exceed the total limit

        Transaction out = new Transaction();

        return transactionRepository.save(out);
    }

    public Transaction getById(long id) {
        return transactionRepository.findById(id).get();
    }

    public Transaction getByAccountId(long accountId) {
        return transactionRepository.findByFromAccountId(accountId);
    }
}