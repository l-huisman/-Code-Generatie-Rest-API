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

    @PostMapping
    public AccountResponseDTO add(@RequestBody AccountRequestDTO account) {
        try {
            return accountService.add(account);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  Get the balance of all active accounts combined
    @GetMapping("/active")
    public Float getAllActiveAccountsBalanceByUserId(@RequestBody Long userId) {
        try {
            return accountService.getAllActiveAccountsBalanceByUserId(userId);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  Get the balance of all active AND non-active accounts combined
    @GetMapping("/all")
    public Float getAllAccountsBalanceByUserId(@RequestBody Long userId) {
        try {
            return accountService.getAllAccountsBalanceByUserId(userId);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/balance")
    public Float getBalance(@RequestBody String iban) {
        try {
            return accountService.getBalance(iban);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
}
