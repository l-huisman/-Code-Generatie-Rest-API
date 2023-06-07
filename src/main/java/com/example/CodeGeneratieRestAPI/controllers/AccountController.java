package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    // TODO: Check why no data is returned when calling the endpoints

    @Autowired
    private AccountService accountService;

    //  POST mappings
    @PostMapping
    public AccountResponseDTO add(@RequestBody AccountRequestDTO account) {
        try {
            // Retrieve and return the data
            return accountService.add(account);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  GET mappings

    //  Get the balance of all active accounts combined
    @GetMapping("/active")
    public Float getAllActiveAccountsBalanceForLoggedInUser() {
        try {
            // Retrieve the balance of all active accounts combined and return it
            return accountService.getAllActiveAccountsBalanceForLoggedInUser();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  Get the balance of all active AND non-active accounts combined
    @GetMapping("/all")
    public Float getAllAccountsBalanceForLoggedInUser() {
        try {
            // Retrieve the balance of all accounts combined and return it
            return accountService.getAllAccountsBalanceForLoggedInUser();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/balance")
    public Float getBalanceByIban(@RequestBody String iban) {
        try {
            // Retrieve the balance of an account by its iban and return it
            return accountService.getBalanceByIban(iban);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/{iban}")
    public AccountResponseDTO getAccountByAccountId(@PathVariable String iban) {
        try {
            // Retrieve an account by its iban and return it
            return new AccountResponseDTO(accountService.getByIban(iban));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
    // PUT mappings

    @PutMapping()
    public AccountResponseDTO update(@RequestBody AccountRequestDTO account) {
        try {
            // Update the account and return the updated account
            return accountService.update(account);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
    //  DELETE mappings

    //  This is a SOFT delete, HARD deletes are NOT allowed
    @DeleteMapping()
    public Boolean delete(@RequestBody(required = true) String iban) {
        try {
            // Delete the account
            return accountService.delete(iban);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping
    public List<AccountResponseDTO> getAllAccounts() {
        try {
            // Retrieve a list of all accounts
            return accountService.getAllAccounts();
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
}
