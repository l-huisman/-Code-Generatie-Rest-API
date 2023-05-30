package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.helpers.ServiceHelper;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public AccountResponseDTO add(AccountRequestDTO accountRequestDTO) {
        // Check if the accountRequestDTO is valid
        if (!checkIfAccountRequestDTOIsValid(accountRequestDTO)) {
            throw new IllegalArgumentException("AccountRequest object is not valid");
        }
        //  Create new account object and save it to the database
        Account newAccount = new Account(accountRequestDTO);
        newAccount.setCreatedAt(getCurrentDateTimeInString());

        accountRepository.save(newAccount);

        // Create a response object and return it
        AccountResponseDTO response = new AccountResponseDTO(newAccount);

        return response;
    }

    private String getCurrentDateTimeInString() {
        //TODO: Make the ZoneId configurable
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        return LocalDateTime.now(zone).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Boolean checkIfAccountRequestDTOIsValid(AccountRequestDTO accountRequestDTO) {
        if (accountRequestDTO == null) {
            throw new IllegalArgumentException("AccountRequest object is null");
        }
        if (accountRequestDTO.getIban() != null) {
            throw new IllegalArgumentException("You cannot set the IBAN of a new account");
        }

        return true;
    }

    public Float retrieveBalance(String iban) {
        // Check if the iban is valid
//        if (!accountRepository.checkIfIbanIsValid(iban)) {
//            throw new IllegalArgumentException("IBAN is not valid");
//        }
        if (ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
            throw new EntityNotFoundException("Account with IBAN " + iban + " does not exist");
        }
        // Get the balance of the account
        Float balance = accountRepository.getBalance(iban);

        return balance != null ? balance : 0;
    }

    public Float getAllActiveAccountsBalanceByUserId(Long userId) {
        // Check if the userId is valid
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User ID is not valid");
        }
        // Get the balance of all accounts of the user and return it
        Float allActiveAccountsBalance = accountRepository.getAllActiveAccountsBalanceByUserId(userId);

        //  If the user has no accounts, return 0
        return allActiveAccountsBalance != null ? allActiveAccountsBalance : 0;
    }

    public Float getAllAccountsBalanceByUserId(Long userId) {
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(userId, User.class)) {
            throw new EntityNotFoundException("User with id " + userId + " does not exist");
        }
        Float balance = accountRepository.getAllAccountsBalanceByUserId(userId);
        return balance != null ? balance : 0;
    }

    public Float getBalance(String iban) {
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
            throw new EntityNotFoundException("Account with IBAN " + iban + " does not exist");
        }
        Float balance = accountRepository.getBalance(iban);
        return balance != null ? balance : 0;
    }
    //  This is a private (generic) method that checks if an object exists by its identifier


}
