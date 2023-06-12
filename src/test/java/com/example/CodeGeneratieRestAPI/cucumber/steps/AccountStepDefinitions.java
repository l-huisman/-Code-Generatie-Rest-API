package com.example.CodeGeneratieRestAPI.cucumber.steps;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

public class AccountStepDefinitions extends BaseStepDefinitions {
    private final HttpHeaders httpHeaders = new HttpHeaders();

    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper mapper;
    private String token;
    private Account account, anotherAccount;
    private User user, anotherUser;
    private TransactionRequestDTO transactionDTO, anotherTransactionDTO;
    private Transaction transaction, anotherTransaction;
    private RuntimeException exception;




    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedIn(String username, String password) throws Throwable {
        httpHeaders.add("Content-Type", "application/json");
        response = restTemplate
                .exchange("/" + "login",
                        HttpMethod.POST,
                        new HttpEntity<>("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}", httpHeaders), // null because OPTIONS does not have a body
                        String.class);

        token = JsonPath.read(response.getBody(), "$.token");
        httpHeaders.add("Authorization", "Bearer " + token);
    }
    @Given("The endpoint for {string} is available for method {string}")
    public void theEndPointsForIsAvailableForMethod(String endpoint, String method) throws Throwable{
        response = restTemplate
                .exchange("/" + endpoint,
                        HttpMethod.OPTIONS,
                        new HttpEntity<>(null, httpHeaders), // null because OPTIONS does not have a body
                        String.class);

        List<String> options = Arrays.stream(response.getHeaders()
                        .get("Allow")
                        .get(0)// The first element is all allowed methods separated by comma
                        .split(","))
                .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));
    }
    @When("I retrieve all accounts")
    public void iRetrieveAllAccounts(){
        response = restTemplate.exchange(restTemplate.getRootUri() + "/accounts", HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class);
    }
    @Then("I should receive all accounts")
    public void iShouldReceiveAllAccounts(){
        int actual = JsonPath.read(response.getBody(), "$.data.size()");
        Assertions.assertEquals(9, actual);
    }
    @When("I retrieve account with IBAN {string}")
    public void iRetrieveAccountWithIBAN(String iban){
        response = restTemplate.exchange(restTemplate.getRootUri() + "/accounts/" + iban, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class);
    }
    @Then("I should receive account with IBAN {string}")
    public void iShouldReceiveAccountWithIBAN(String iban){
        String actual = JsonPath.read(response.getBody(), "$.data.account.iban");
        Assertions.assertEquals(iban, actual);
    }
}
