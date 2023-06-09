package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.AccountCreationException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotAccessibleException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotFoundException;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
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
import java.util.List;

import static com.example.CodeGeneratieRestAPI.helpers.IBANGenerator.getUniqueIban;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public Account add(AccountRequestDTO accountRequestDTO) {
        try {
            User currentLoggedInUser = ServiceHelper.getLoggedInUser();

            //  Check if the accountRequestDTO is valid
            //this.checkIfAccountRequestDTOIsValid(accountRequestDTO, currentLoggedInUser);

            //  Check if the IBAN has not been set yet
            if (accountRequestDTO.getIban() != null) {
                throw new AccountCreationException("You cannot set the IBAN of a new account");
            }

            //  If the user is an employee, check if the user id is set
            //  It is possible for an employee to add an account for itself, but it is also possible for an employee to add an account for another user
            //  Hence why we check if the user id is set if the user is an employee
            if (currentLoggedInUser.getUserType().equals("EMPLOYEE") && accountRequestDTO.getUserId() == null) {
                throw new AccountCreationException("You cannot add an account as an employee without selecting a user (if you are adding an account for yourself, select yourself as the user)");
            } else {
                //  If the user is not an employee, set the user id to the id of the current logged in user
                accountRequestDTO.setUserId(currentLoggedInUser.getId());
            }

            //  Get the user
            User user = userRepository.findById(accountRequestDTO.getUserId()).orElse(null);

            //  Set the userId on the account
            accountRequestDTO.setUserId(currentLoggedInUser.getId());

            //  Generate a new unique IBAN
            String iban = getUniqueIban();

            //  Set the IBAN of the accountRequestDTO
            accountRequestDTO.setIban(iban);

            //  Create new account object and save it to the database
            Account newAccount = new Account(accountRequestDTO, user);
            newAccount.setCreatedAt(getCurrentDate());

            accountRepository.save(newAccount);

            return newAccount;
        } catch (Exception e) {
            throw e;
        }

    }

    private Date getCurrentDate() {
        //TODO: Make the ZoneId configurable
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        return Date.from(LocalDateTime.now(zone).atZone(zone).toInstant());
    }

    private void checkIfAccountRequestDTOIsValid(AccountRequestDTO accountRequestDTO) {
        if (accountRequestDTO == null) {
            throw new IllegalArgumentException("AccountRequest object is null");
        }
    }

    private Boolean checkIfAccountBelongsToUser(String iban, User loggedInUser) {
        if (loggedInUser.getUserType().equals("EMPLOYEE")) {
            return true;
        }
        return accountRepository.checkIfAccountBelongsToUser(iban, loggedInUser.getId());
    }
    private Account checkAndGetAccount(String iban){
        // Check if the iban is valid
        if (!ServiceHelper.checkIfObjectExistsByIdentifier(iban, new Account())) {
            throw new AccountNotFoundException("Account with IBAN: " + iban + " does not exist");
        }

        // Check if the account belongs to the user or if the user is an employee
        User currentLoggedInUser = ServiceHelper.getLoggedInUser();
        if (!accountRepository.checkIfAccountBelongsToUser(iban, currentLoggedInUser.getId()) && !currentLoggedInUser.getUserType().getAuthority().equals("EMPLOYEE")) {
            throw new AccountNotAccessibleException("Account with IBAN " + iban + " does not belong to you!");
        }

        return accountRepository.getAccountByIban(iban).orElseThrow(() -> new AccountNotFoundException("Account with IBAN: " + iban + " does not exist"));
    }

    public List<Account> getAllActiveAccountsForLoggedInUser(String accountName) {
        // Get the current logged in user
        User currentLoggedInUser = ServiceHelper.getLoggedInUser();

        // Get the balance of all accounts of the user and return it
        List<Account> allActiveAccountsBalance = accountRepository.findAllByNameContainingAndUser_Id(accountName, currentLoggedInUser.getId());

        //  Return the accounts
        return allActiveAccountsBalance;
    }

    public List<Account> getAllAccounts(String search, boolean active) {
        // Get the current logged in user
        User currentLoggedInUser = ServiceHelper.getLoggedInUser();

        // Check if the user is an employee
        if (currentLoggedInUser.getUserType().getAuthority().equals("EMPLOYEE")) {
            //  Get all accounts
            return accountRepository.findAllBySearchTerm(search, active);
        }
        else {
            //  Get all accounts of the user
            return accountRepository.findAllBySearchTermAndUserId(search, active, currentLoggedInUser.getId());
        }
    }
    public Account getAccountByIban(String iban) {
        // Get the current logged in user
        User currentLoggedInUser = ServiceHelper.getLoggedInUser();

        //  Check account and get the account
        Account account = this.checkAndGetAccount(iban);

        //  Return the account
        return account;
    }

    public Account update(AccountRequestDTO account) {
        // Get the current logged in user
        User loggedInUser = ServiceHelper.getLoggedInUser();

        // Check if the accountRequestDTO is valid
        this.checkIfAccountRequestDTOIsValid(account);

        // Check if the account exists and get the account
        Account accountToUpdate = checkAndGetAccount(account.getIban());

        // Update the account
        Account updatedAccount = getUpdatedAccount(account, accountToUpdate);

        // Save the account
        accountRepository.save(updatedAccount);

        // Create a response object and return it
        return updatedAccount;
    }

    private Account getUpdatedAccount(AccountRequestDTO accountWithNewValues, Account accountToUpdate) {
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

    public String delete(String iban) {
        // Get the current logged-in user
        User loggedInUser = ServiceHelper.getLoggedInUser();

        // Check account and get the account
        Account account = this.checkAndGetAccount(iban);

        // Check if the account is active
        if (!account.getIsActive()) {
            throw new IllegalArgumentException("Account with IBAN: " + iban + " is already inactive");
        }

        // Delete the account
        account.setIsActive(false);
        accountRepository.save(account);

        return "Account with IBAN: " + iban + " has been set to inactive";
    }

    public void addSeededAccount(Account account) {
        account.setIban(getUniqueIban());
        accountRepository.save(account);
    }

//    public List<AccountResponseDTO> getAllAccounts() {
//        List<Account> accounts = accountRepository.findAll();
//        List<AccountResponseDTO> accountsresponse = new ArrayList<>();
//        for (Account account : accounts) {
//            accountsresponse.add(new AccountResponseDTO(account));
//        }
//        return accountsresponse;
//    }
}
