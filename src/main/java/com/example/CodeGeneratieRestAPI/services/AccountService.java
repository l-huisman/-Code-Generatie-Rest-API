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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.example.CodeGeneratieRestAPI.helpers.IBANGenerator.getUniqueIban;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public AccountResponseDTO add(AccountRequestDTO accountRequestDTO) {
        try{
            User currentLoggedInUser = getLoggedInUser();

            //  Check if the accountRequestDTO is valid
            this.checkIfAccountRequestDTOIsValid(accountRequestDTO, currentLoggedInUser);

            //  Check if the IBAN has not been set yet
            if (accountRequestDTO.getIban() != null) {
                throw new IllegalArgumentException("You cannot set the IBAN of a new account");
            }

            //  Generate a new unique IBAN
            String iban = getUniqueIban();

            //  Set the IBAN of the accountRequestDTO
            accountRequestDTO.setIban(iban);

            //  Create new account object and save it to the database
            Account newAccount = new Account(accountRequestDTO);
            newAccount.setCreatedAt(getCurrentDate());

            accountRepository.save(newAccount);

            //  Create a response object and return it
            AccountResponseDTO response = new AccountResponseDTO(newAccount);

            return response;
        } catch (Exception e) {
            throw e;
        }

    }

    private Date getCurrentDate() {
        //TODO: Make the ZoneId configurable
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        return Date.from(LocalDateTime.now(zone).atZone(zone).toInstant());
    }

    private void checkIfAccountRequestDTOIsValid(AccountRequestDTO accountRequestDTO, User loggedInUser) {
        if (accountRequestDTO == null) {
            throw new IllegalArgumentException("AccountRequest object is null");
        }
        if (!accountRequestDTO.getUserId().equals(loggedInUser.getId()) && accountRequestDTO.getUserId() != null) {
            throw new IllegalArgumentException("The id of the owner of the account you are trying to add/edit does not match the id of the authenticated user");
        }

    }
    private User getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        User user = userRepository.findUserByUsername(username).orElse(null);
        if (user == null)
            throw new EntityNotFoundException("User with username '" + username + "' does not exist");
        return user;
    }
    private Boolean checkIfAccountBelongsToUser(String iban, User loggedInUser) {
        if (loggedInUser.getUserType().equals("EMPLOYEE")) {
            return true;
        }
        return accountRepository.checkIfAccountBelongsToUser(iban, loggedInUser.getId());
    }
//    public Float retrieveBalance(String iban) {
//        User currentLoggedInUser = getLoggedInUser();
//
//        // Check if the iban is valid
//        if (ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
//            throw new EntityNotFoundException("Account with IBAN " + iban + " does not exist");
//        }
//
//        //  Check if the account belongs to the user
//        if (!checkIfAccountBelongsToUser(iban, currentLoggedInUser)) {
//            throw new IllegalArgumentException("The account with IBAN " + iban + " does not belong to the user with id " + currentLoggedInUser.getId());
//        }
//
//        // Get the balance of the account
//        Float balance = accountRepository.getBalanceByIban(iban);
//
//        return balance != null ? balance : 0;
//    }

    public Float getAllActiveAccountsBalanceForLoggedInUser() {
        // Get the current logged in user
        User currentLoggedInUser = getLoggedInUser();

        // Get the balance of all accounts of the user and return it
        Float allActiveAccountsBalance = accountRepository.getAllActiveAccountsBalanceByUserId(currentLoggedInUser.getId());

        //  If the user has no accounts, return 0
        return allActiveAccountsBalance != null ? allActiveAccountsBalance : 0;
    }

    public Float getAllAccountsBalanceForLoggedInUser() {
        // Get the current logged in user
        User currentLoggedInUser = getLoggedInUser();

        // Get the balance of all accounts of the user and return it
        Float balance = accountRepository.getAllAccountsBalanceByUserId(currentLoggedInUser.getId());

        // If the user has no balance, return 0
        return balance != null ? balance : 0.0f;
    }

    public Float getBalanceByIban(String iban) {
        // Get the current logged in user
        User currentLoggedInUser = getLoggedInUser();

        // Check if the iban is valid
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
            throw new EntityNotFoundException("Account with IBAN " + iban + " does not exist");
        }
        // Check if the account belongs to the user or if the user is an employee
        if (!accountRepository.checkIfAccountBelongsToUser(iban, currentLoggedInUser.getId())) {
            throw new IllegalArgumentException("Account with IBAN " + iban + " does not belong to user with id " + currentLoggedInUser.getId());
        }

        Float balance = accountRepository.getBalanceByIban(iban);
        return balance != null ? balance : 0;
    }
    // Update the balance of an account
    public AccountResponseDTO updateBalance(String iban, Float amount){
        // Check if the iban is valid
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
            throw new EntityNotFoundException("Account with IBAN: " + iban + " does not exist");
        }
        // Check if the amount is valid
        if (amount == null) {
            throw new IllegalArgumentException("Amount is null");
        }
        // Get the account
        Account account = accountRepository.findByIban(iban);
        // Update the balance
        account.updateBalance(amount);
        // Save the account
        accountRepository.save(account);
        // Create a response object and return it
        return new AccountResponseDTO(account);

    }
    public Account getByIban(String iban) {
        // Get the current logged in user
        User currentLoggedInUser = getLoggedInUser();

        // Check if the iban is valid
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
            throw new EntityNotFoundException("Account with IBAN: " + iban + " does not exist");
        }

        // Check if the account belongs to the user or if the user is an employee
        if (!accountRepository.checkIfAccountBelongsToUser(iban, currentLoggedInUser.getId())) {
            throw new IllegalArgumentException("Account with IBAN " + iban + " does not belong to user with id " + currentLoggedInUser.getId());
        }

        //  Get the account details by iban and return it
        return accountRepository.findByIban(iban);
    }
    public AccountResponseDTO update(AccountRequestDTO account){
        // Get the current logged in user
        User loggedInUser = getLoggedInUser();

        // Check if the accountRequestDTO is valid
        this.checkIfAccountRequestDTOIsValid(account, loggedInUser);

        // Check if the account exists
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(account.getIban(), Account.class)) {
            throw new EntityNotFoundException("Account with IBAN: " + account.getIban() + " does not exist");
        }

        // Check if the account belongs to the user
        if (!checkIfAccountBelongsToUser(account.getIban(), loggedInUser)) {
            throw new IllegalArgumentException("The account with IBAN " + account.getIban() + " does not belong to the user with id " + loggedInUser.getId());
        }

        // Get the account to update
        Account accountToUpdate = accountRepository.findByIban(account.getIban());

        // Update the account
        Account updatedAccount = accountToUpdate.update(new Account(account));

        // Save the account
        accountRepository.save(updatedAccount);

        // Create a response object and return it
        return new AccountResponseDTO(updatedAccount);
    }
    private Account getUpdatedAccount(AccountRequestDTO accountWithNewValues, Account accountToUpdate){
        // Loop through all the fields of the accountWithNewValues object
        // If the field is not null and the value is different from the one in the accountToUpdate object
        // Set the new value to the accountToUpdate object
        for (Field field : accountWithNewValues.getClass().getDeclaredFields()) {
            // Skip the iban field (it cannot be updated
            if (field.getName().equals("iban"))
                continue;

            // Set the field to accessible
            field.setAccessible(true);
            try {
                Object newValue = field.get(accountWithNewValues);
                Object oldValue = field.get(accountToUpdate);

                if (newValue != null && !newValue.equals(oldValue)) {
                    field.set(accountToUpdate, newValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        // Return the updated account
        return accountToUpdate;
    }
    public Boolean delete(String iban){
        // Get the current logged in user
        User loggedInUser = getLoggedInUser();

        // Check if the account exists
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(iban, Account.class)) {
            throw new EntityNotFoundException("Account with IBAN: " + iban + " does not exist");
        }

        // Check if the account belongs to the user
        if (!checkIfAccountBelongsToUser(iban, loggedInUser)) {
            throw new IllegalArgumentException("The account with IBAN " + iban + " does not belong to the user with id " + loggedInUser.getId());
        }

        // Get the account
        Account accountToDelete = accountRepository.findByIban(iban);

        // Delete the account
        accountToDelete.setIsActive(false);
        accountRepository.save(accountToDelete);

        return true;
    }
}
