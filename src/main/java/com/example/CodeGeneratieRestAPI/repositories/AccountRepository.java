package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByIban(String iban);

    List<Account> findAll();

    Boolean existsByIban(String iban);

    Float getBalanceByIban(String iban);

    Float getAllActiveAccountsBalanceByUserId(Long userId);

    Float getAllAccountsBalanceByUserId(Long userId);

    @Query("SELECT a FROM Account a WHERE a.user.username LIKE %:search% " +
            "OR a.user.first_name LIKE %:search% " +
            "OR a.user.last_name LIKE %:search% " +
            "OR a.name LIKE %:search% " +
            "OR a.iban LIKE %:search%")
    List<Account> findAllBySearchTerm(String search);
    Account findAccountByIban(String iban);
    //List<Account> findAllByUser_Id(Long userId);
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.iban = :iban AND a.user.id = :userId")
    boolean checkIfAccountBelongsToUser(@Param("iban") String iban, @Param("userId") Long userId);
}
