package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    // Gets an account by iban
    Account findByIban(String iban);

    // Gets all accounts of a user
    Account findAllByUserId(Long userId);

    //  Gets the balance of an account
    Float getBalance(String iban);

    //  Gets the balance of all (active) accounts of a user
    Float getAllActiveAccountsBalanceByUserId(Long userId);

    //  Gets the balance of all accounts of a user (including non-active accounts)
    Float getAllAccountsBalanceByUserId(Long userId);

    Boolean checkIfIbanIsValid(String iban);
}
