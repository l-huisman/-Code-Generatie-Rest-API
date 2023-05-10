package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAll() {
        return (List<Transaction>) transactionRepository.findAll();
    }

    public Transaction add(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction update(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction getById(long id) {
        return transactionRepository.findById(id).get();
    }

    public void delete(long id) {
        transactionRepository.deleteById(id);
    }
}