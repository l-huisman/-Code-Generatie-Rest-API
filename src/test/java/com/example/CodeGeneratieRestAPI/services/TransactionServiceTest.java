package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.*;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

import static org.mockito.Mockito.when;

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

    private User getMockUser(Long id, UserType userType, String username) {
        User user = new User();
        user.setId(id);
        user.setUserType(userType);
        user.setUsername(username);
        return user;
    }

    private Account getMockAccount(String iban, Float balance, User user, Boolean isSavings) {
        Account account = new Account();
        account.setIban(iban);
        account.setUser(user);
        account.setBalance(balance);
        account.setAbsoluteLimit(10F);
        account.setDailyLimit(200F);
        account.setTransactionLimit(100F);
        account.setIsSavings(isSavings);
        return account;
    }

    private Transaction getMockTransaction(User user, Float amount, TransactionType transactionType, Account fromAccount, Account toAccount) {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(amount);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionType(transactionType);
        return transaction;
    }

    @Test
    public void testAddWrongTransactionType() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account toAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 60F, null, null, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, toAccount.getIban(), "", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionTypeNotValidException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddDeposit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account toAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 60F, TransactionType.DEPOSIT, null, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, toAccount.getIban(), "DEPOSIT", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertDoesNotThrow(() -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddDepositAmountZero() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account toAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 0F, TransactionType.DEPOSIT, null, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, toAccount.getIban(), "DEPOSIT", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionAmountNotValidException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddDepositAmountNegative() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account toAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, -10F, TransactionType.DEPOSIT, null, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, toAccount.getIban(), "DEPOSIT", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionAmountNotValidException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddDepositMissingToAccount() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account toAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 60F, TransactionType.DEPOSIT, null, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, null, "DEPOSIT", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionAccountNotValidException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddDepositNotYourAccount() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "doe");
        Account toAccount = getMockAccount("123456", 1000F, user1, false);
        Transaction transaction = getMockTransaction(user1, 60F, TransactionType.DEPOSIT, null, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, toAccount.getIban(), "DEPOSIT", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(AccountNotOwnedException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddWithdraw() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 60F, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertDoesNotThrow(() -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddWithdrawMissingFromAccount() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 60F, TransactionType.WITHDRAW, null, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(null, null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionAccountNotValidException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddWithdrawExceedTransactionLimit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 200F, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionExceededTransactionLimitException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddWithdrawExceedAbsoluteLimit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 100F, user, false);
        Transaction transaction = getMockTransaction(user, 950F, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionExceededAbsoluteLimitException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddWithdrawExceedDailyLimit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Transaction transaction = getMockTransaction(user, 90F, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        transactions.add(getMockTransaction(user, 90F, TransactionType.WITHDRAW, fromAccount, null));
        transactions.add(getMockTransaction(user, 90F, TransactionType.WITHDRAW, fromAccount, null));

        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startOfDay, endOfDay, fromAccount.getIban())).thenReturn(transactions);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionExceededDailyLimitException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferFromNotOwn() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "doe");
        Account fromAccount = getMockAccount("123456", 1000F, user1, false);
        Account toAccount = getMockAccount("123457", 1000F, user, false);

        Transaction transaction = getMockTransaction(user, 60F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(AccountNotOwnedException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferFromOwnSavingsToPayment() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, true);
        Account toAccount = getMockAccount("123457", 1000F, user, false);

        Transaction transaction = getMockTransaction(user, 60F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertDoesNotThrow(() -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferFromOwnPaymentToSavings() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Account toAccount = getMockAccount("123457", 1000F, user, true);

        Transaction transaction = getMockTransaction(user, 60F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertDoesNotThrow(() -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferFromPaymentToSavings() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "doe");

        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Account toAccount = getMockAccount("123457", 1000F, user1, true);

        Transaction transaction = getMockTransaction(user, 60F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionTransferSavingsException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferFromSavingsToPayment() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "doe");

        Account fromAccount = getMockAccount("123456", 1000F, user, true);
        Account toAccount = getMockAccount("123457", 1000F, user1, false);

        Transaction transaction = getMockTransaction(user, 60F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionTransferSavingsException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferExceedingTransactionLimit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Account toAccount = getMockAccount("123457", 1000F, user, false);

        Transaction transaction = getMockTransaction(user, 110F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionExceededTransactionLimitException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferExceedingAbsoluteLimit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 60F, user, false);
        Account toAccount = getMockAccount("123457", 60F, user, false);

        Transaction transaction = getMockTransaction(user, 90F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionExceededAbsoluteLimitException.class, () -> transactionService.add(user, transactionRequestDTO));
    }

    @Test
    public void testAddTransferExceedingDailyLimit() {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);
        Account toAccount = getMockAccount("123457", 1000F, user, false);

        Transaction transaction = getMockTransaction(user, 90F, TransactionType.TRANSFER, fromAccount, toAccount);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), toAccount.getIban(), "TRANSFER", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(getMockTransaction(user, 90F, TransactionType.WITHDRAW, fromAccount, toAccount));
        transactions.add(getMockTransaction(user, 90F, TransactionType.WITHDRAW, fromAccount, toAccount));
        transactions.add(getMockTransaction(user, 90F, TransactionType.WITHDRAW, fromAccount, toAccount));

        LocalDate today = LocalDate.now();
        Date startOfDay = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        when(accountRepository.findByIban(fromAccount.getIban())).thenReturn(fromAccount);
        when(accountRepository.findByIban(toAccount.getIban())).thenReturn(toAccount);
        when(transactionRepository.findAllByCreatedAtBetweenAndFromAccountIban(startOfDay, endOfDay, fromAccount.getIban())).thenReturn(transactions);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Assertions.assertThrows(TransactionExceededDailyLimitException.class, () -> transactionService.add(user, transactionRequestDTO));
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

        Pageable pageableRequest = PageRequest.of(0, 10);

        Page<Transaction> pageTransactions = new PageImpl<>(transactions, pageableRequest, transactions.size());

        when(transactionRepository.findAll(endDate, startDate, "", "", 0F, pageableRequest)).thenReturn(pageTransactions);

        Page<Transaction> result = transactionService.getAll(user, startDate, endDate, "", "", 0F, 0, 10);

        Assertions.assertEquals(transactions.size(), result.getContent().size());
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
            transactionService.getAll(user, startDate, endDate, "", "", 0F, 0, 10);
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
    public void testTransactionToAccountIsNotOwnedByUser() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "john1");

        Account toAccount = getMockAccount("1234567890", 1000F, user1, false);

        Transaction transaction = getMockTransaction(user1, 90F, TransactionType.DEPOSIT, null, toAccount);

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Assertions.assertThrows(TransactionNotOwnedException.class, () -> transactionService.transactionIsOwnedByUser(user, transaction.getId()));
    }

    @Test
    public void testTransactionFromAccountIsNotOwnedByUser() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "john1");

        Account toAccount = getMockAccount("1234567890", 1000F, user1, false);

        Transaction transaction = getMockTransaction(user1, 90F, TransactionType.WITHDRAW, toAccount, null);

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
        User user = getMockUser(1L, UserType.USER, "john");

        String iban = "1234567890";
        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<Account> accounts = new ArrayList<>();
        Account account = getMockAccount(iban, 1000F, user, false);
        accounts.add(account);
        user.setAccounts(accounts);

        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction = getMockTransaction(user, 100F, TransactionType.DEPOSIT, null, account);
        transaction.setLabel("test");
        transactions.add(transaction);
        transactions.add(getMockTransaction(user, 100F, TransactionType.DEPOSIT, null, account));

        Pageable pageableRequest = PageRequest.of(0, 10);

        when(transactionRepository.findAllByIban(endDate, startDate, iban, "", "", 0F, pageableRequest)).thenReturn(transactions);


        List<Transaction> result = transactionService.getAllByAccountIban(user, iban, startDate, endDate, "", "", 0F, 0, 10);

        Assertions.assertEquals(transactions, result);
    }

    @Test
    public void testGetAllByAccountIbanNotOwns() {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "john1");

        String iban = "1234567890";
        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        List<Account> accounts = new ArrayList<>();
        Account account = getMockAccount(iban, 1000F, user1, false);
        accounts.add(account);
        user1.setAccounts(accounts);

        List<Transaction> transactions = new ArrayList<>();

        Transaction transaction = getMockTransaction(user1, 100F, TransactionType.DEPOSIT, null, account);
        transaction.setLabel("test");
        transactions.add(transaction);
        transactions.add(getMockTransaction(user1, 100F, TransactionType.DEPOSIT, null, account));

        Pageable pageableRequest = PageRequest.of(0, 10);

        when(transactionRepository.findAllByIban(endDate, startDate, iban, "", "", 0F, pageableRequest)).thenReturn(transactions);

        Assertions.assertThrows(AccountNotOwnedException.class, () -> transactionService.getAllByAccountIban(user, iban, startDate, endDate, "", "", 0F, 0, 10));
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