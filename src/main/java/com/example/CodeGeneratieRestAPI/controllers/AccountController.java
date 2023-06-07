package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.Hibernate.map;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    private final ModelMapper modelMapper;

    public AccountController() {
        modelMapper = new ModelMapper();

        //  Set the field matching to strict
        modelMapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        PropertyMap<Account, AccountResponseDTO> accountMap = new PropertyMap<>() {
            protected void configure() {
                map().setUserId(source.getUser().getId());
            }
        };
        modelMapper.addMappings(accountMap);
    }
    //  POST mappings
    @PostMapping
    public AccountResponseDTO add(@RequestBody(required = true) AccountRequestDTO accountRequestDTO) {
        try {
            //  Retrieve the data
            Account account = accountService.add(accountRequestDTO);

            //  Return the data
            return new AccountResponseDTO(account);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  GET mappings

    //  Get all active accounts
    @GetMapping("/active")
    public List<AccountResponseDTO> getAllActiveAccountsForLoggedInUser(@RequestParam(required = false) String search) {
        try {
            //  Retrieve the data
            List<Account> accounts = accountService.getAllActiveAccountsForLoggedInUser(search);

            //  Return the data
            return Arrays.asList(modelMapper.map(accounts, AccountResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    //  Get all active AND non-active accounts
    @GetMapping("/all")
    public List<AccountResponseDTO> getAllAccountsForLoggedInUser(@RequestParam(required = false) String search) {
        try {
            //  Retrieve the required data
            List<Account> accounts = accountService.getAllAccountsForLoggedInUser(search);

            //  Return the data
            return Arrays.asList(modelMapper.map(accounts, AccountResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping()
    public List<AccountResponseDTO> getAllAccounts(@RequestParam(required = false) String search) {
        try {
            //  Retrieve the data
            List<Account> accounts = accountService.getAllAccounts(search);

            //  Return the data
            return Arrays.asList(modelMapper.map(accounts, AccountResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/{iban}")
    public AccountResponseDTO getAccountByIban(@PathVariable(required = true) String iban) {
        try{
            //  Retrieve the data
            Account account = accountService.getAccountByIban(iban);

            //  Return the data
            return modelMapper.map(account, AccountResponseDTO.class);
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
    // PUT mappings

    @PutMapping()
    public AccountResponseDTO update(@RequestBody(required = true) AccountRequestDTO accountRequestDTO) {
        try {
            //  Retrieve the data
            Account account = accountService.update(accountRequestDTO);

            //  Return the data
            return modelMapper.map(account, AccountResponseDTO.class);
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
            // Perform the delete action
            Boolean succesOrFailed = accountService.delete(iban);

            // Return the data
            return succesOrFailed;
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }
}
