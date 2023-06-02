package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByFromAccountIban(String iban);

    List<Transaction> findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanAndDescriptionContainingOrLabelContaining(Date startDate, Date endDate, String fromAccountIban, String description, String label);
        
    List<Transaction> findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanOrToAccountIbanAndDescriptionContainingOrLabelContaining(Date startDate, Date endDate, String fromAccountIban, String toAccountIban, String description, String label);

    List<Transaction> findAllByCreatedAtBetweenAndFromAccountIban(Date startOfDay, Date endOfDay, String fromAccountIban);
}
