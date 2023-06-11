package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.AccountData;
import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.*;
import com.example.CodeGeneratieRestAPI.helpers.IBANGenerator;
import com.example.CodeGeneratieRestAPI.helpers.ServiceHelper;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServiceHelper serviceHelper;
    @Mock
    private IBANGenerator ibanGenerator;
    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    private Account getMockAccount(User user) {
        Account account = new Account();
        account.setIban("NL24-INHO-0288-9098-04");
        account.setUser(user);
        account.setUserId(account.getUserId());
        account.setBalance(1000.0F);
        account.setAbsoluteLimit(10F);
        account.setDailyLimit(500F);
        account.setTransactionLimit(100F);
        account.setName("Test account");
        account.setIsActive(true);
        return account;
    }

    private AccountRequestDTO getMockAccountRequestDTO() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setBalance(1000.0F);
        accountRequestDTO.setAbsoluteLimit(10F);
        accountRequestDTO.setDailyLimit(500F);
        accountRequestDTO.setTransactionLimit(100F);
        accountRequestDTO.setName("Test account");
        accountRequestDTO.setIsActive(true);
        return accountRequestDTO;
    }

    private User getMockUser(UserType userType) {
        User user = new User();
        user.setId(1L);
        user.setUserType(userType);
        user.setUsername("john");
        return user;
    }

    @Test
    public void testGetAllAccounts() {
        // Setup
        User user = getMockUser(UserType.USER);
        List<Account> accountList = new ArrayList<>();
        Account account1 = getMockAccount(user);
        Account account2 = getMockAccount(user);
        accountList.add(account1);
        accountList.add(account2);
        when(accountRepository.findAllBySearchTermAndUserId(anyString(), anyBoolean(), anyLong()))
                .thenReturn(accountList);

        // Run the test
        final List<Account> result = accountService.getAllAccounts("Test", true, user);
        // final List<Account> result =
        // accountRepository.findAllBySearchTermAndUserId("Test", true, user.getId());

        // Verify the results
        assertEquals(2, result.size());
        assertTrue(result.contains(account1));
        assertTrue(result.contains(account2));
    }

    @Test
    public void testAddAccount() {
        // Setup
        User user_USER = getMockUser(UserType.USER);
        User user_EMPLOYEE = getMockUser(UserType.EMPLOYEE);
        AccountRequestDTO accountRequestDTO_1 = getMockAccountRequestDTO();
        accountRequestDTO_1.setIban("1234567890");
        AccountRequestDTO accountRequestDTO_2 = getMockAccountRequestDTO();
        AccountRequestDTO accountRequestDTO_3 = getMockAccountRequestDTO();
        accountRequestDTO_3.setUserId(user_USER.getId());

        // Run the test and verify the exception
        AccountCreationException exception_1 = Assertions.assertThrows(AccountCreationException.class,
                () -> accountService.add(accountRequestDTO_1, user_USER));
        assertEquals("You cannot set the IBAN of a new account", exception_1.getMessage());

        AccountCreationException exception_2 = Assertions.assertThrows(AccountCreationException.class,
                () -> accountService.add(accountRequestDTO_2, user_EMPLOYEE));
        assertEquals(
                "You cannot add an account as an employee without selecting a user (if you are adding an account for yourself, select yourself as the user)",
                exception_2.getMessage());

        assertDoesNotThrow(() -> accountService.add(accountRequestDTO_3, user_USER));
    }

    @Test
    void testGetAccountByIbanWithValidUser() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);

        Optional<Account> expectedAccountOptional = Optional.of(accountToCheck);
        when(accountRepository.getAccountByIban(accountToCheck.getIban())).thenReturn(expectedAccountOptional);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);

        //Account actualAccount = accountService.getAccountByIban(account.getIban(), user1);
        AccountData actualAccount = accountService.getAccountByIban(account.getIban(), user1);

        //assertEquals(expectedAccountOptional.orElse(null), actualAccount);
        assertEquals(expectedAccountOptional.orElse(null), actualAccount.getAccount());
    }

    @Test
    void testGetAccountByIbanThrowsAccountNotAccessibleException() {
        User user1 = getMockUser(UserType.USER);
        User user2 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);

        Optional<Account> expectedAccountOptional = Optional.of(accountToCheck);
        when(accountRepository.getAccountByIban(accountToCheck.getIban())).thenReturn(expectedAccountOptional);
        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(false);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.empty());

        AccountNotAccessibleException exception = Assertions.assertThrows(AccountNotAccessibleException.class, () -> accountService.getAccountByIban(account.getIban(), user2));
        assertEquals("Account with IBAN: " + accountToCheck.getIban() + " does not belong to the logged in user", exception.getMessage());
    }

    @Test
    void testGetAccountByIbanThrowsAccountNotFoundException() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.empty());

        AccountNotFoundException exception = Assertions.assertThrows(AccountNotFoundException.class, () -> accountService.getAccountByIban("NL02-INHO-1254-1234-56", user1));
        assertEquals("Account with IBAN: NL02-INHO-1254-1234-56 does not exist", exception.getMessage());
    }

    @Test
    void testGetAllAccountsByUserIdThrowsAccountNotAccessibleException() {
        User user1 = getMockUser(UserType.USER);
        User user2 = getMockUser(UserType.USER);
        user2.setId(2L);
        List<Account> accountList = new ArrayList<>();
        Account account1 = getMockAccount(user1);
        Account account2 = getMockAccount(user1);
        accountList.add(account1);
        accountList.add(account2);
        when(accountRepository.findAllByUserId(anyLong())).thenReturn(accountList);

        // Verify the results
        AccountNotAccessibleException exception = Assertions.assertThrows(AccountNotAccessibleException.class, () -> accountService.getAllAccountsByUserId(user1.getId(), user2));
        assertEquals("You cannot access the accounts of another user", exception.getMessage());
    }

    @Test
    void testGetAllAccountsByUserId() {
        User user1 = getMockUser(UserType.USER);
        List<Account> accountList = new ArrayList<>();
        Account account1 = getMockAccount(user1);
        Account account2 = getMockAccount(user1);
        accountList.add(account1);
        accountList.add(account2);
        when(accountRepository.findAllByUserId(anyLong())).thenReturn(accountList);

        // Verify the results
        List<Account> result = accountService.getAllAccountsByUserId(user1.getId(), user1);
        assertEquals(2, result.size());
        assertTrue(result.contains(account1));
        assertTrue(result.contains(account2));
    }

    @Test
    void testGetAllAccountsByUserIdWithEmployee() {
        User user1 = getMockUser(UserType.USER);
        User user2 = getMockUser(UserType.EMPLOYEE);
        user2.setId(2L);
        List<Account> accountList = new ArrayList<>();
        Account account1 = getMockAccount(user1);
        Account account2 = getMockAccount(user1);
        accountList.add(account1);
        accountList.add(account2);
        when(accountRepository.findAllByUserId(anyLong())).thenReturn(accountList);

        // Verify the results
        List<Account> result = accountService.getAllAccountsByUserId(user1.getId(), user2);
        assertEquals(2, result.size());
        assertTrue(result.contains(account1));
        assertTrue(result.contains(account2));
    }

    @Test
    void testGetAllAccountsByUserIdAsUserFromEmployee() {
        User user1 = getMockUser(UserType.USER);
        User user2 = getMockUser(UserType.EMPLOYEE);
        user2.setId(2L);
        List<Account> accountList = new ArrayList<>();
        Account account1 = getMockAccount(user1);
        Account account2 = getMockAccount(user1);
        accountList.add(account1);
        accountList.add(account2);
        when(accountRepository.findAllByUserId(anyLong())).thenReturn(accountList);

        // Verify the results
        AccountNotAccessibleException exception = Assertions.assertThrows(AccountNotAccessibleException.class, () -> accountService.getAllAccountsByUserId(user2.getId(), user1));
        assertEquals("You cannot access the accounts of another user", exception.getMessage());
    }

    @Test
    void testUpdate() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();
        accountRequestDTO.setIban(accountToCheck.getIban());
        accountRequestDTO.setBalance(null);
        accountRequestDTO.setName("Changed name");

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.of(accountToCheck));

        Account accountToUpdate = accountService.update(accountRequestDTO, user1);
        assertEquals(accountToCheck.getBalance(), accountToUpdate.getBalance());
    }

    @Test
    void testUpdateAccountUpdateException() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();
        accountRequestDTO.setIban(accountToCheck.getIban());
        accountRequestDTO.setBalance(2000.0f);

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.of(accountToCheck));

        AccountUpdateException exception = Assertions.assertThrows(AccountUpdateException.class, () -> accountService.update(accountRequestDTO, user1));
        assertEquals("You cannot update the balance of an account", exception.getMessage());
    }

    @Test
    void testUpdateAccountNotFoundException() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();
        accountRequestDTO.setIban(accountToCheck.getIban());
        accountRequestDTO.setBalance(null);
        accountRequestDTO.setName("Changed name");

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.empty());


        AccountNotFoundException exception = Assertions.assertThrows(AccountNotFoundException.class, () -> accountService.update(accountRequestDTO, user1));
        assertEquals("Account with IBAN: " + accountToCheck.getIban() + " does not exist", exception.getMessage());
    }

    @Test
    void testUpdateAccountNotAccessibleException() {
        User user1 = getMockUser(UserType.USER);
        User user2 = getMockUser(UserType.USER);
        user2.setId(2L);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();
        accountRequestDTO.setIban(accountToCheck.getIban());
        accountRequestDTO.setBalance(null);
        accountRequestDTO.setName("Changed name");

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(false);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.of(accountToCheck));

        AccountNotAccessibleException exception = Assertions.assertThrows(AccountNotAccessibleException.class, () -> accountService.update(accountRequestDTO, user2));
        assertEquals("Account with IBAN: " + accountRequestDTO.getIban() + " does not belong to the logged in user", exception.getMessage());
    }

    @Test
    void testDelete() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.of(accountToCheck));

        assertEquals("Account with IBAN: " + accountToCheck.getIban() + " has been set to inactive", accountService.delete(account.getIban(), user1));
    }

    @Test
    void testDeleteThrowsAccountCannotBeDeletedException() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);
        accountToCheck.setIsActive(false);

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.of(accountToCheck));

        AccountCannotBeDeletedException exception = Assertions.assertThrows(AccountCannotBeDeletedException.class, () -> accountService.delete(account.getIban(), user1));
        assertEquals("Account with IBAN: " + accountToCheck.getIban() + " is already inactive", exception.getMessage());
    }

    @Test
    void testDeleteThrowsAccountNotFoundException() {
        User user1 = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(true);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.empty());

        AccountNotFoundException exception = Assertions.assertThrows(AccountNotFoundException.class, () -> accountService.delete(account.getIban(), user1));
        assertEquals("Account with IBAN: " + account.getIban() + " does not exist", exception.getMessage());
    }

    @Test
    void testDeleteThrowsAccountNotAccessibleException() {
        User user1 = getMockUser(UserType.USER);
        User user2 = getMockUser(UserType.USER);
        user2.setId(2L);
        AccountRequestDTO account = getMockAccountRequestDTO();

        when(ibanGenerator.getUniqueIban()).thenReturn("NL01-INHO-0000-0000-05");

        Account accountToCheck = accountService.add(account, user1);

        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user1.getId())).thenReturn(false);
        when(serviceHelper.checkIfObjectExistsByIdentifier(any(), any())).thenReturn(true);
        when(accountRepository.getAccountByIban(account.getIban())).thenReturn(Optional.of(accountToCheck));

        AccountNotAccessibleException exception = Assertions.assertThrows(AccountNotAccessibleException.class, () -> accountService.delete(account.getIban(), user2));
        assertEquals("Account with IBAN: " + account.getIban() + " does not belong to the logged in user", exception.getMessage());
    }
}