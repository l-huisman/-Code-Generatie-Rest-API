package com.example.CodeGeneratieRestAPI.cucumber.steps;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.TransactionType;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TransactionStepDefinitions extends BaseStepDefinitions {
    private final HttpHeaders httpHeaders = new HttpHeaders();
    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;

    @Mock
    private TransactionRepository transactionRepository;
        
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper mapper;

    private Account account;
    private User user;
    private Transaction transaction;
    private RuntimeException exception;

    @Given("The endpoint for {string} is available for method {string}")
    public void theEndpointForIsAvailableForMethod(String endpoint, String method) throws Throwable {
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

    @When("^I retrieve all transactions$")
    public void iRetrieveAllTransactions() {
        response = restTemplate.exchange(restTemplate.getRootUri() + "/transactions", HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), String.class);
    }

    @Then("^I should receive all transactions$")
    public void iShouldReceiveAllTransactions() {
        int actual = JsonPath.read(response.getBody(), "$.size()");
        Assertions.assertEquals(1, actual);
    }

    @Given("a user with username {string}")
    public void aUserWithUsername(String username) {
        user = new User();
        user.setUsername(username);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
    }

    @Given("an account with IBAN {string} and balance {float}")
    public void anAccountWithIbanAndBalance(String iban, float balance) {
        account = new Account();
        account.setIban(iban);
        account.setBalance(balance);
        when(accountRepository.findByIban(iban)).thenReturn(account);
    }

    @Given("a savings account with IBAN {string} and balance {float}")
    public void aSavingsAccountWithIbanAndBalance(String iban, float balance) {
        account = new Account();
        account.setIban(iban);
        account.setBalance(balance);
        account.setIsSavings(true);
        when(accountRepository.findByIban(iban)).thenReturn(account);
    }

    @Given("an account with IBAN {string} and balance {float} and transaction limit {float}")
    public void anAccountWithIbanAndBalanceAndTransactionLimit(String iban, float balance, float transactionLimit) {
        account = new Account();
        account.setIban(iban);
        account.setBalance(balance);
        account.setTransactionLimit(transactionLimit);
        when(accountRepository.findByIban(iban)).thenReturn(account);
    }

    @Given("an account with IBAN {string} and balance {float} and daily limit {float}")
    public void anAccountWithIbanAndBalanceAndDailyLimit(String iban, float balance, float dailyLimit) {
        account = new Account();
        account.setIban(iban);
        account.setBalance(balance);
        account.setDailyLimit(dailyLimit);
        when(accountRepository.findByIban(iban)).thenReturn(account);
    }

    @When("a transaction of {float} is added to the account")
    public void aTransactionIsAddedToTheAccount(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a deposit transaction of {float} is added to the account")
    public void aDepositTransactionIsAddedToTheAccount(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a withdraw transaction of {float} is added to the account")
    public void aWithdrawTransactionIsAddedToTheAccount(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a transfer transaction of {float} is added to the account without a toAccountId")
    public void aTransferTransactionIsAddedToTheAccountWithoutAToAccountId(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.TRANSFER);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("another transaction of {float} is added to the account")
    public void anotherTransactionIsAddedToTheAccount(float amount) {
        Transaction anotherTransaction = new Transaction();
        anotherTransaction.setAmount(amount);
        anotherTransaction.setFromAccount(account);
        anotherTransaction.setTransactionType(TransactionType.WITHDRAW);
        when(transactionRepository.save(anotherTransaction)).thenReturn(anotherTransaction);
        try {
            transactionService.add(anotherTransaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @Then("the transaction is saved successfully")
    public void theTransactionIsSavedSuccessfully() {
        verify(transactionRepository).save(transaction);
        assertNull(exception);
    }

    @Then("a RuntimeException is thrown with message {string}")
    public void aRuntimeExceptionIsThrownWithMessage(String message) {
        verify(transactionRepository, never()).save(transaction);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}
