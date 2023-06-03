package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    //  POST mappings
    @PostMapping
    public AccountResponseDTO add(@RequestBody AccountRequestDTO account) {
        try {
            //  Retrieve the data
            var data = accountService.add(account);

            //  Return the data
            return data;
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
            //  Retrieve the data
            var data = accountService.getAllActiveAccountsBalanceForLoggedInUser();

            //  Return the data
            return data;
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
            //  Retrieve the required data
            var data = accountService.getAllAccountsBalanceForLoggedInUser();

            //  Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping()
    public Float getBalanceByIban(@RequestBody String iban) {
        try {
            //  Retrieve the data
            var data = accountService.getBalanceByIban(iban);

            //  Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
    @GetMapping("/{iban}")
    public AccountResponseDTO getAccountByAccountId(@PathVariable String iban) {
        try{
            //  Retrieve the data
            var data = new AccountResponseDTO(accountService.getByIban(iban));

            //  Return the data
            return data;
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
            //  Retrieve the data
            var data = accountService.update(account);

            //  Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
    //  DELETE mappings

    //  This is a SOFT delete, HARD deletes are NOT allowed
    @DeleteMapping()
    public Boolean delete(@RequestBody String iban) {

        try {
            // Perform the delete action
            var data = accountService.delete(iban);

            // Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
}
