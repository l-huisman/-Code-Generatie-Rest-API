package com.example.CodeGeneratieRestAPI.services;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.TransactionNotFoundException;
import com.example.CodeGeneratieRestAPI.exceptions.TransactionNotOwnedException;
import com.example.CodeGeneratieRestAPI.helpers.IBANGenerator;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.CodeGeneratieRestAPI.exceptions.EmployeeOnlyException;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private User getMockUser(UserType userType) {
        User user = new User();
        user.setId(1L);
        user.setUserType(userType);
        user.setUsername("john");
        return user;
    }

    private Account getMockAccount(User user) {
        Account account = new Account();
        account.setIban("1234567890");
        account.setUser(user);
        account.setBalance(1000.0F);
        account.setAbsoluteLimit(10F);
        account.setDailyLimit(500F);
        account.setTransactionLimit(100F);
        return account;
    }

    private Transaction getMockTransaction(User user, TransactionType transactionType, Account fromAccount, Account toAccount) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(60F);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionType(transactionType);
        return transaction;
    }

    @Test
    public void testAddDeposit() {
        User user = getMockUser(UserType.USER);
        Account toAccount = getMockAccount(user);
        Transaction transaction = getMockTransaction(user, TransactionType.DEPOSIT, null, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, toAccount.getIban(), "DEPOSIT", transaction.getAmount() , transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertDoesNotThrow(() -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddWithdraw() {
        User user = getMockUser(UserType.USER);
        Account fromAccount = getMockAccount(user);
        Transaction transaction = getMockTransaction(user, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount() , transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertDoesNotThrow(() -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testGetAllByUser() {
        User user = new User();
        user.setId(1L);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        transactions.add(new Transaction());

        when(transactionRepository.findAllByUserId(user.getId())).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllByUser(user);

        Assertions.assertEquals(transactions.size(), result.size());
    }

    @Test
    public void testGetAll() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.EMPLOYEE);

        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        String search = "test";

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        transactions.add(new Transaction());

        when(transactionRepository.findAll(endDate, startDate, search)).thenReturn(transactions);

        List<Transaction> result = transactionService.getAll(user, startDate, endDate, search);

        Assertions.assertEquals(transactions.size(), result.size());
    }

    @Test
    public void testGetAllNonEmployee() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.USER);

        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        String search = "test";

        Assertions.assertThrows(EmployeeOnlyException.class, () -> {
            transactionService.getAll(user, startDate, endDate, search);
        });
    }


    @Test
    public void testGetById() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.EMPLOYEE);
        user.setUsername("john");

        Account account = new Account();
        account.setIban("1234567890");
        account.setUser(user);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0F);
        transaction.setFromAccount(account);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Assertions.assertDoesNotThrow(() -> transactionService.getById(user, transaction.getId()));
    }

    @Test
    public void testTransactionIsOwnedByUser() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.USER);
        user.setUsername("john");

        Account account = new Account();
        account.setIban("1234567890");
        account.setUser(user);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0F);
        transaction.setFromAccount(account);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Assertions.assertDoesNotThrow(() -> transactionService.transactionIsOwnedByUser(user, transaction.getId()));
    }

    @Test
    public void testTransactionIsNotOwnedByUser() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.USER);
        user.setUsername("john");

        User user1 = new User();
        user1.setId(2L);
        user1.setUserType(UserType.USER);
        user1.setUsername("john1");

        Account account = new Account();
        account.setIban("1234567890");
        account.setUser(user);

        Account account1 = new Account();
        account1.setIban("1234567891");
        account1.setUser(user1);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(100.0F);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setFromAccount(account1);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Assertions.assertThrows(TransactionNotOwnedException.class, () -> transactionService.transactionIsOwnedByUser(user, transaction.getId()));
    }

    @Test
    public void testTransactionDoesNotExist() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.USER);
        user.setUsername("john");
        Long id = 1L;

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(TransactionNotFoundException.class, () -> transactionService.transactionIsOwnedByUser(user, id));
    }

    @Test
    public void testGetAllByAccountIban() {
        User user = new User();
        user.setId(1L);
        user.setUserType(UserType.USER);
        user.setUsername("john");

        String iban = "1234567890";
        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        String search = "test";
        List<Account> accounts = new ArrayList<>();
        Account account = new Account();
        account.setIban(iban);
        account.setUser(user);
        accounts.add(account);
        user.setAccounts(accounts);
        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(100.0F);
        transaction1.setFromAccount(account);

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(120.0F);
        transaction2.setFromAccount(account);
        transaction2.setLabel("test");

        transactions.add(transaction1);
        transactions.add(transaction2);

        when(transactionRepository.findAllByIban(endDate, startDate, iban, search)).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllByAccountIban(user, iban, startDate, endDate, search);

        Assertions.assertEquals(transactions, result);
    }

    @Test
    public void testGetTodaysAccumulatedTransactionAmount() {
        try {
            Method GetTodaysAccumulatedTransactionAmountMethod = TransactionService.class.getDeclaredMethod("getTodaysAccumulatedTransactionAmount", String.class);
            GetTodaysAccumulatedTransactionAmountMethod.setAccessible(true);

            String iban = "1234567890";

            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = LocalDateTime.of(today, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(today, LocalTime.MAX);

            Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

            Account account = new Account();
            account.setIban(iban);
            List<Transaction> transactions = new ArrayList<>();
            Transaction transaction = new Transaction();
            transaction.setAmount(100.0F);
            transaction.setFromAccount(account);
            transactions.add(transaction);
            transactions.add(transaction);

            when(transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startDate, endDate, iban)).thenReturn(transactions);

            Double result = (Double) GetTodaysAccumulatedTransactionAmountMethod.invoke(transactionService, iban);

            Assertions.assertEquals(200.0, result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}