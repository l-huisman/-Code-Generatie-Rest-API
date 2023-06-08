package com.example.CodeGeneratieRestAPI.repositories;

import com.example.CodeGeneratieRestAPI.models.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {
    Account findByIban(String iban);

    @Query("SELECT a FROM Account a WHERE a.iban = :iban")
    Optional<Account> getAccountByIban(String iban);
    List<Account> findAll();

    Boolean existsByIban(String iban);

    //Float getBalanceByIban(String iban);

    //List<Account> getAllActiveAccountsByUserId(Long userId);
    //List<Account> getAllAccountsByUserId(Long userId);
    List<Account> findAllByNameContainingAndUser_Id(String accountName, Long userId);

    //List<Account> findAllByNameContaining(String accountName);
    List<Account> findAllByUserUsernameContainingOrNameContaining(Optional<String> userUsername, Optional<String> accountName);
    //List<Account> findAllByUserUsernameContainingOrUserFirst_nameContainingOrUserLast_nameContainingOrNameContainingOrIbanContaining(String search);

//     @Query("SELECT a FROM Account a WHERE (a.isActive = :isActive OR :isActive IS NULL) AND (a.user.username ILIKE %:search% " +
//             "OR a.user.firstName ILIKE %:search% " +
//             "OR a.user.lastName ILIKE %:search% " +
//             "OR a.name ILIKE %:search% " +
//             "OR a.iban ILIKE %:search%)")
//     List<Account> findAllBySearchTerm(String search, Boolean isActive);

//    @Query("SELECT a FROM Account a WHERE (:isActive IS NULL OR a.isActive = :isActive) AND " +
//            "(COALESCE(:search, '') = '' OR a.user.username ILIKE %:search% " +
//            "OR a.user.firstName ILIKE %:search% " +
//            "OR a.user.lastName ILIKE %:search% " +
//            "OR a.name ILIKE %:search% " +
//            "OR a.iban ILIKE %:search%)")
    @Query("SELECT a FROM Account a WHERE (:isActive IS NULL OR a.isActive = :isActive) " +
            "AND (a.user.username ILIKE CONCAT('%', :search, '%') " +
            "OR a.user.firstName ILIKE CONCAT('%', :search, '%') " +
            "OR a.user.lastName ILIKE CONCAT('%', :search, '%') " +
            "OR a.name ILIKE CONCAT('%', :search, '%') " +
            "OR a.iban ILIKE CONCAT('%', :search, '%'))")
List<Account> findAllBySearchTerm(String search, Boolean isActive);

    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND (:isActive IS NULL OR a.isActive = :isActive) AND (a.user.username ILIKE %:search% " +
            "OR a.user.firstName ILIKE %:search% " +
            "OR a.user.lastName ILIKE %:search% " +
            "OR a.name ILIKE %:search% " +
            "OR a.iban ILIKE %:search%)")
    List<Account> findAllBySearchTermAndUserId(String search, Boolean isActive, Long userId);


    List<Account> findByUserUsernameContainingIgnoreCaseOrUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrNameContainingIgnoreCaseOrIbanContainingIgnoreCaseAndIsActive(String username, String firstName, String lastName, String name, String iban, Boolean isActive);


    //List<Account> findAllByUser_Id(Long userId);
    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.iban = :iban AND a.user.id = :userId")
    boolean checkIfAccountBelongsToUser(@Param("iban") String iban, @Param("userId") Long userId);
}
