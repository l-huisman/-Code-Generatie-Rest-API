package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

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

    //  Get the balance of all active accounts combined
    @GetMapping
    public Float getAllActiveAccountsBalanceByUserId(@RequestBody Long userId) {
        try {
            //  Retrieve the data
            var data = accountService.getAllActiveAccountsBalanceByUserId(userId);

            //  Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  Get the balance of all active AND non-active accounts combined
    @GetMapping
    public Float getAllAccountsBalanceByUserId(@RequestBody Long userId) {
        try {
            //  Retrieve the required data
            var data = accountService.getAllAccountsBalanceByUserId(userId);

            //  Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping
    public Float getBalance(@RequestBody String iban) {
        try {
            //  Retrieve the data
            var data = accountService.getBalance(iban);

            //  Return the data
            return data;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
}
