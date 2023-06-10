package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.AccountCreationException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNoDataChangedException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotAccessibleException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotFoundException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountUpdateException;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.helpers.ServiceHelper;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServiceHelper serviceHelper;
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
        when(accountRepository.findAllBySearchTermAndUserId(anyString(), anyBoolean(), anyLong())).thenReturn(accountList);

        // Run the test
        final List<Account> result = accountService.getAllAccounts("Test", true, user);
        //final List<Account> result = accountRepository.findAllBySearchTermAndUserId("Test", true, user.getId());

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
        AccountCreationException exception_1 = Assertions.assertThrows(AccountCreationException.class, () -> accountService.add(accountRequestDTO_1, user_USER));
        assertEquals("You cannot set the IBAN of a new account", exception_1.getMessage());

        AccountCreationException exception_2 = Assertions.assertThrows(AccountCreationException.class, () -> accountService.add(accountRequestDTO_2, user_EMPLOYEE));
        assertEquals("You cannot add an account as an employee without selecting a user (if you are adding an account for yourself, select yourself as the user)", exception_2.getMessage());

        assertDoesNotThrow(() -> accountService.add(accountRequestDTO_3, user_USER));
    }

    @Test
    void testGetAccountByIban() {
        User user = getMockUser(UserType.USER);
        AccountRequestDTO account = getMockAccountRequestDTO();

        Account accountToCheck = accountService.add(account, user);

        Optional<Account> expectedAccountOptional = Optional.of(accountToCheck);
        when(accountRepository.getAccountByIban(accountToCheck.getIban())).thenReturn(expectedAccountOptional);
        when(accountRepository.checkIfAccountBelongsToUser(accountToCheck.getIban(), user.getId())).thenReturn(true);
        Account actualAccount = accountService.getAccountByIban(account.getIban(), user);

        assertEquals(expectedAccountOptional.orElse(null), actualAccount);
    }


}