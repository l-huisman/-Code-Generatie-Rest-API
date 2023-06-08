package com.example.CodeGeneratieRestAPI.cucumber.steps;

import com.example.CodeGeneratieRestAPI.models.*;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionStepDefinitions extends BaseStepDefinitions {
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

    private Account account, anotherAccount;
    private User user, anotherUser;
    private Transaction transaction, anotherTransaction;
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

    @Given("a user with username {string} and type {string}")
    public void aUserWithUsername(String username, String type) {
        user = new User();
        user.setUsername(username);
        user.setUserType(UserType.valueOf(type));
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            userRepository.save(user);
        }
    }

    @Given("another user with username {string} and type {string}")
    public void anotherUserWithUsername(String username, String type) {
        anotherUser = new User();
        anotherUser.setUsername(username);
        anotherUser.setUserType(UserType.valueOf(type));
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) {
            anotherUser = optionalUser.get();
        } else {
            userRepository.save(anotherUser);
        }
    }

    @Given("{string} has a {string} account with IBAN {string} and balance {float} and transaction limit {float} and daily limit {float} and absolute limit {float}")
    public void aAccountWithIbanAndBalance(String username, String isSavings, String iban, float balance, float transactionLimit, float dailyLimit, float absoluteLimit) {
        account = accountRepository.findByIban(iban);

        if (account == null) {
            Optional<User> optionalUser = userRepository.findUserByUsername(username);
            optionalUser.ifPresent(value -> user = value);
            account = new Account();
            account.setIban(iban);
            account.setUser(user);
            account.setBalance(balance);
            account.setTransactionLimit(transactionLimit);
            account.setDailyLimit(dailyLimit);
            account.setAbsoluteLimit(absoluteLimit);
            account.setIsSavings(isSavings.equals("savings"));
            accountRepository.save(account);
        }
    }

    @Given("another user {string} has a {string} account with IBAN {string} and balance {float} and transaction limit {float} and daily limit {float} and absolute limit {float}")
    public void anotherUserAccountWithIbanAndBalance(String username, String isSavings, String iban, float balance, float transactionLimit, float dailyLimit, float absoluteLimit) {
        anotherAccount = accountRepository.findByIban(iban);

        if (anotherAccount == null) {
            Optional<User> optionalUser = userRepository.findUserByUsername(username);
            optionalUser.ifPresent(value -> anotherUser = value);
            anotherAccount = new Account();
            anotherAccount.setIban(iban);
            anotherAccount.setUser(anotherUser);
            anotherAccount.setBalance(balance);
            anotherAccount.setTransactionLimit(transactionLimit);
            anotherAccount.setDailyLimit(dailyLimit);
            anotherAccount.setAbsoluteLimit(absoluteLimit);
            anotherAccount.setIsSavings(isSavings.equals("savings"));
            accountRepository.save(anotherAccount);
        }
    }

    @Given("a {string} account with IBAN {string} and balance {float} and transaction limit {float} and daily limit {float} and absolute limit {float}")
    public void anAccountWithIbanAndBalanceAndDailyLimit(String isSavings, String iban, float balance, float transactionLimit, float dailyLimit, float absoluteLimit) {
        account = accountRepository.findByIban(iban);
        if (account == null) {
            account = new Account();
            account.setIban(iban);
            account.setBalance(balance);
            account.setTransactionLimit(transactionLimit);
            account.setDailyLimit(dailyLimit);
            account.setAbsoluteLimit(absoluteLimit);
            account.setIsSavings(isSavings.equals("savings"));
            accountRepository.save(account);
        }
    }

    @When("a withdraw transaction of {float} is added to the account")
    public void aWithdrawTransactionIsAddedToTheAccount(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        try {
            transaction = transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a deposit transaction of {float} is added to the account")
    public void aDepositTransactionIsAddedToTheAccount(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setToAccount(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        try {
            transaction = transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            exception = e;
        }
    }

    @When("a transfer transaction of {float} to IBAN {string} is added to the account")
    public void aTransferTransactionToIBANIsAddedToTheAccount(float amount, String toAccountIban) {
        anotherAccount = accountRepository.findByIban(toAccountIban);

        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setToAccount(anotherAccount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        //print transaction
        try {
            transaction = transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a deposit transaction of {float} to IBAN {string} is added to the account")
    public void aDepositTransactionToIBANIsAddedToTheAccount(float amount, String toAccountIban) {
        account = accountRepository.findByIban(toAccountIban);

        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setToAccount(account);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        //print transaction
        try {
            transaction = transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a withdraw transaction of {float} from IBAN {string} is added to the account")
    public void aWithdrawTransactionToIBANIsAddedToTheAccount(float amount, String fromAccountIban) {
        account = accountRepository.findByIban(fromAccountIban);

        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        //print transaction
        try {
            transaction = transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a transfer transaction of {float} from IBAN {string} to IBAN {string} is added to the account")
    public void aTransferTransactionToIBANIsAddedToTheAccount(float amount, String fromAccountIban, String toAccountIban) {
        account = accountRepository.findByIban(toAccountIban);
        Account fromAccount = accountRepository.findByIban(fromAccountIban);

        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(account);
        transaction.setTransactionType(TransactionType.TRANSFER);
        //print transaction
        try {
            transaction = transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a deposit transaction of {float} is added to the account without a toAccountIban")
    public void aDepositTransactionIsAddedToTheAccountWithoutAToAccountIban(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a transfer transaction of {float} is added to the account without a toAccountIban")
    public void aTransferTransactionIsAddedToTheAccountWithoutAToAccountIban(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setFromAccount(account);
        transaction.setTransactionType(TransactionType.TRANSFER);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("a transfer transaction of {float} is added to the account without a fromAccountIban")
    public void aTransferTransactionIsAddedToTheAccountWithoutAFromAccountIban(float amount) {
        transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setToAccount(account);
        transaction.setTransactionType(TransactionType.TRANSFER);
        try {
            transactionService.add(transaction, user.getUsername());
        } catch (RuntimeException e) {
            exception = e;
        }
    }

    @When("another deposit transaction of {float} is added to the account")
    public void anotherDepositTransactionIsAddedToTheAccount(float amount) {
        anotherTransaction = new Transaction();
        anotherTransaction.setAmount(amount);
        anotherTransaction.setToAccount(account);
        anotherTransaction.setTransactionType(TransactionType.DEPOSIT);
        try {
            transactionService.add(anotherTransaction, user.getUsername());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            exception = e;
        }
    }

    @When("another withdraw transaction of {float} is added to the account")
    public void anotherWithdrawTransactionIsAddedToTheAccount(float amount) {
        anotherTransaction = new Transaction();
        anotherTransaction.setAmount(amount);
        anotherTransaction.setFromAccount(account);
        anotherTransaction.setTransactionType(TransactionType.WITHDRAW);
        try {
            transactionService.add(anotherTransaction, user.getUsername());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            exception = e;
        }
    }

    @When("another transfer transaction of {float} to IBAN {string} is added to the account")
    public void anotherTransferTransactionIsAddedToTheAccount(float amount, String toAccountIban) {
        anotherAccount = accountRepository.findByIban(toAccountIban);

        anotherTransaction = new Transaction();
        anotherTransaction.setAmount(amount);
        anotherTransaction.setFromAccount(account);
        anotherTransaction.setToAccount(anotherAccount);
        anotherTransaction.setTransactionType(TransactionType.TRANSFER);
        try {
            transactionService.add(anotherTransaction, user.getUsername());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            exception = e;
        }
    }

    @Then("the transaction is saved successfully")
    public void theTransactionIsSavedSuccessfully() {
        Transaction savedTransaction = transactionRepository.findById(transaction.getId()).orElse(null);
        assertNotNull(savedTransaction);
        assertEquals(transaction.getAmount(), savedTransaction.getAmount());
        //ssertEquals(transaction.getFromAccount(), savedTransaction.getFromAccount());
        assertEquals(transaction.getTransactionType(), savedTransaction.getTransactionType());
        assertNull(exception);
    }

    @Then("a RuntimeException is thrown with message {string}")
    public void aRuntimeExceptionIsThrownWithMessage(String message) {
        Transaction savedTransaction = transaction.getId() != null ? transactionRepository.findById(transaction.getId()).orElse(null) : null;
        assertNull(savedTransaction);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
    @Then("a other RuntimeException is thrown with message {string}")
    public void aOtherRuntimeExceptionIsThrownWithMessage(String message) {
        Transaction savedTransaction = anotherTransaction.getId() != null ? transactionRepository.findById(anotherTransaction.getId()).orElse(null) : null;
        assertNull(savedTransaction);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}