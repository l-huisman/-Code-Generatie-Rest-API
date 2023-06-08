package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.ApiResponse;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final ModelMapper modelMapper;
    // Create an object mapper to map the JSON to a POJO
    // POJO stands for Plain Old Java Object. It has no restrictions and can be used in any Java project.
    // It is a Java object that is not bound by any restriction other than those forced by the Java Language Specification.
    // In short, a POJO is an object that encapsulates data.
    private final ObjectMapper objectMapper;
    @Autowired
    private AccountService accountService;

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

        objectMapper = new ObjectMapper();
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
//    @GetMapping("/active")
//    public List<AccountResponseDTO> getAllActiveAccountsForLoggedInUser(@RequestParam(required = false) String search) {
//        try {
//            //  Retrieve the data
//            List<Account> accounts = accountService.getAllActiveAccountsForLoggedInUser(search);
//
//            //  Return the data
//            return Arrays.asList(modelMapper.map(accounts, AccountResponseDTO[].class));
//        } catch (Exception e) {
//            //TODO: handle exception
//            System.out.println(e);
//            return null;
//        }
//    }

    //  Get all active AND non-active accounts
//    @GetMapping("/all")
//    public List<AccountResponseDTO> getAllAccountsForLoggedInUser(@RequestParam(required = false) String search) {
//        try {
//            //  Retrieve the required data
//            List<Account> accounts = accountService.getAllAccountsForLoggedInUser(search);
//
//            //  Return the data
//            return Arrays.asList(modelMapper.map(accounts, AccountResponseDTO[].class));
//        } catch (Exception e) {
//            //TODO: handle exception
//            System.out.println(e);
//            return null;
//        }
//    }

    @GetMapping()
    public List<AccountResponseDTO> getAllAccounts(@RequestParam(required = false) String search, @RequestParam(required = false) boolean active) {
        try {
            //  Retrieve the data
            List<Account> accounts = accountService.getAllAccounts(search, active);

            //  Return the data

            return Arrays.asList(modelMapper.map(accounts, AccountResponseDTO[].class));
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return null;
        }
    }

    @GetMapping("/{iban}")
    public ResponseEntity<AccountResponseDTO> getAccountByIban(@PathVariable(required = true) String iban) {
        //  Retrieve the data
        AccountResponseDTO account = modelMapper.map(accountService.getAccountByIban(iban), AccountResponseDTO.class);

        //  Return the data
        return ResponseEntity.status(HttpStatus.OK).body(account);

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
    public ResponseEntity<ApiResponse<String>> delete(@RequestBody(required = true) String ibanProvided) {
        try {
            //  Convert the JSON to a POJO, so we can use the iban and perform the delete action
            String responseBody = accountService.delete(objectMapper.readTree(ibanProvided).get("iban").asText());

            // Return a response entity with the response body and the status code
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, responseBody));

        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage()));
        }
    }
}
