package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    List<Transaction> findAllByCreatedAtLessThanEqualAndCreatedAtGreaterThanEqualAndFromAccountIbanAndDescriptionContainingOrLabelContaining(Date startDate, Date endDate, String fromAccountIban, String description, String label);

    @Query("SELECT t FROM Transaction t LEFT JOIN t.toAccount LEFT JOIN t.fromAccount LEFT JOIN t.toAccount.user LEFT JOIN t.fromAccount.user WHERE t.fromAccount.user.id = :id OR t.toAccount.user.id = :id")
    List<Transaction> findAllByUserId(Long id);

    @Query("SELECT t FROM Transaction t LEFT JOIN t.toAccount LEFT JOIN t.fromAccount WHERE t.createdAt <= :endDate AND t.createdAt >= :startDate AND (:amountRelation is null OR :amountRelation = '' OR (:amountRelation = '<' AND t.amount < :amount) OR (:amountRelation = '>' AND t.amount > :amount) OR (:amountRelation = '=' AND t.amount = :amount)) AND (:iban = '' OR :iban is null OR (t.fromAccount IS NOT null AND t.fromAccount.iban = :iban ) OR (t.toAccount IS NOT null and t.toAccount.iban = :iban))")
    Page<Transaction> findAll(Date endDate, Date startDate, String iban, String amountRelation, Float amount, Pageable pageable);

    @Query("SELECT t FROM Transaction t LEFT JOIN t.toAccount LEFT JOIN t.fromAccount WHERE t.createdAt <= :endDate AND t.createdAt >= :startDate AND ((t.fromAccount IS NOT null AND t.fromAccount.iban = :iban ) OR (t.toAccount IS NOT null and t.toAccount.iban = :iban)) AND (t.description LIKE CONCAT('%', :search, '%') OR t.label LIKE CONCAT('%', :search, '%'))")
    List<Transaction> findAllByIban(Date endDate, Date startDate, String iban, String search);

    List<Transaction> findAllByCreatedAtBetweenAndFromAccountIban(Date startOfDay, Date endOfDay, String search);
}
