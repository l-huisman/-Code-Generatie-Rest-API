package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByIban(String iban);

    Boolean existsByIban(String iban);

    Float getBalanceByIban(String iban);

    Float getAllActiveAccountsBalanceByUserId(Long userId);

    Float getAllAccountsBalanceByUserId(Long userId);


}
