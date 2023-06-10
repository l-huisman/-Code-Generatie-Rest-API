package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
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

    private User getMockUser(UserType userType) {
        User user = new User();
        user.setId(1L);
        user.setUserType(userType);
        user.setUsername("john");
        return user;
    }
    // @Test
    // public void testGetAllAccounts() {
    // // Setup
    // List<Account> accountList = new ArrayList<>();
    // accountList.add(getMockAccount(getMockUser(UserType.USER)));
    // accountList.add(getMockAccount(getMockUser(UserType.USER)));
    // when(accountRepository.findAll()).thenReturn(accountList);

    // // Run the test
    // final List<Account> result = accountService.getAllAccounts("", true);

    // // Verify the results
    // assertEquals(2, result.size());
    // }
}