package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByFromAccountIban(String iban);

    List<Transaction> findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIban(String startDate, String endDate, String fromAccountIban);
}
