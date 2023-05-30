package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.TransactionType;
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

    public Transaction add(Transaction transaction) {
        Account fromAccount = accountRepository.findByIban(transaction.getFromAccountIban());

        if (fromAccount.getBalance() + fromAccount.getAbsoluteLimit() < transaction.getAmount()) {
            throw new RuntimeException("This account does not have enough balance to complete this transaction.");
        }

        //Check negative amount
        if (transaction.getAmount() < 0) {
            throw new RuntimeException("The transaction amount can not be negative.");
        }

        //Check if the user owns this account or is an admin

        //Check if the account is a savings account and if the transaction is a deposit
        if (fromAccount.getIsSavings() && transaction.getTransactionType() != TransactionType.WITHDRAW) {
            throw new RuntimeException("A savings account can not be used for withdraws.");
        }

        //Check if the transaction is a transfer and if there is a toAccountId
        if (transaction.getTransactionType() == TransactionType.TRANSFER && transaction.getToAccountIban() == null) {
            throw new RuntimeException("A transfer transaction requires a toAccountId.");
        }

        //Check if the transaction amount didn't exceed the transaction limit
        if (fromAccount.getTransactionLimit() < transaction.getAmount()) {
            throw new RuntimeException("The daily limit for this account has been exceeded.");
        }

        //Check if the transaction amount didn't exceed the total limit
//        if (fromAccount.getDaily_limit() < transaction.getAmount()) {
//            throw new RuntimeException("This account does not have enough balance to complete this transaction.");
//        }


        Transaction out = new Transaction();

        return transactionRepository.save(out);
    }

    public Transaction getById(long id) {
        return transactionRepository.findById(id).get();
    }

    public List<Transaction> getAllByAccountIban(String iban) {
        return transactionRepository.findAllByFromAccountIban(iban);
    }
}