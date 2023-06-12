package com.example.CodeGeneratieRestAPI.cucumber.steps;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
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
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

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


    @Given("I am logged in as {string} with password {string} to do some account stuff")
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

    @Given("The endpoint for {string} is available for method {string} to do some account stuff")
    public void theEndPointsForIsAvailableForMethod(String endpoint, String method) throws Throwable {
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
    public void iRetrieveAllAccounts() {
        response = restTemplate.exchange(restTemplate.getRootUri() + "/accounts", HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class);
    }

    @Then("I should receive all accounts")
    public void iShouldReceiveAllAccounts() {
        int actual = JsonPath.read(response.getBody(), "$.data.size()");
        Assertions.assertEquals(9, actual);
    }

    @When("I retrieve account with IBAN {string}")
    public void iRetrieveAccountWithIBAN(String iban) {
        response = restTemplate.exchange(restTemplate.getRootUri() + "/accounts/" + iban, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class);
    }

    @Then("I should receive account with IBAN {string}")
    public void iShouldReceiveAccountWithIBAN(String iban) {
        String actual = JsonPath.read(response.getBody(), "$.data.account.iban");
        Assertions.assertEquals(iban, actual);
    }
    @When("I create a new account")
    public void iCreateANewAccount(){
        AccountRequestDTO account = new AccountRequestDTO();
        account.setUserId(1L);
        account.setName("Account name");
        account.setDailyLimit(1000F);
        account.setTransactionLimit(1000F);
        account.setAbsoluteLimit(1000F);
        account.setIsSavings(false);
        account.setIsActive(true);
        response = restTemplate.exchange(restTemplate.getRootUri() + "/accounts", HttpMethod.POST, new HttpEntity<>(account, httpHeaders), String.class);
    }
    @Then("I should receive the new account")
    public void iShouldReceiveTheNewAccount(){
        String actual = JsonPath.read(response.getBody(), "$.data.name");
        Assertions.assertEquals("Account name", actual);
    }
    @When("I create a new account with IBAN {string}")
    public void iCreateANewAccountWithIBAN(String iban){
        AccountRequestDTO account = new AccountRequestDTO();
        account.setIban(iban);
        account.setUserId(1L);
        account.setName("Account name");
        account.setDailyLimit(1000F);
        account.setTransactionLimit(1000F);
        account.setAbsoluteLimit(1000F);
        account.setIsSavings(false);
        account.setIsActive(true);
        response = restTemplate.exchange(restTemplate.getRootUri() + "/accounts", HttpMethod.POST, new HttpEntity<>(account, httpHeaders), String.class);
    }
    @Then("I should receive an error")
    public void iShouldReceiveAnError(){
        String actual = JsonPath.read(response.getBody(), "$.error");
        Assertions.assertEquals("Account already exists", actual);
    }
}
