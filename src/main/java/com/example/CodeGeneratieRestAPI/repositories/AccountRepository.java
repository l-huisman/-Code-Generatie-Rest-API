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

    Boolean existsByIban(String iban);

    //Float getBalanceByIban(String iban);

    //List<Account> getAllActiveAccountsByUserId(Long userId);
    //List<Account> getAllAccountsByUserId(Long userId);
    List<Account> findAllByNameContainingAndUser_Id(String accountName, Long userId);
    //List<Account> findAllByNameContaining(String accountName);
    List<Account> findAllByUserUsernameContainingOrNameContaining(String userUsername, String accountName);
    //List<Account> findAllByUser_Id(Long userId);
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.iban = :iban AND a.user.id = :userId")
    boolean checkIfAccountBelongsToUser(@Param("iban") String iban, @Param("userId") Long userId);
}
