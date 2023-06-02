package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    // Gets an account by iban
    Account findByIban(String iban);

    // Gets all accounts of a user
    Account findAllByUserId(Long userId);

    //  Gets the balance of an account
    @Query("SELECT a.balance FROM Account a WHERE a.name = :name")
    Float getBalance(@Param("name") String name);

    //  Gets the balance of all (active) accounts of a user
    Float getAllActiveAccountsBalanceByUserId(Long userId);

    //  Gets the balance of all accounts of a user (including non-active accounts)
    Float getAllAccountsBalanceByUserId(Long userId);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.iban = :iban")
    boolean checkIban(@Param("iban") String iban);
}
