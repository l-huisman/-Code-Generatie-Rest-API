package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountData;
import com.example.CodeGeneratieRestAPI.dtos.AccountLimitsLeft;
import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.AccountResponseDTO;
import com.example.CodeGeneratieRestAPI.exceptions.AccountCreationException;
import com.example.CodeGeneratieRestAPI.exceptions.AccountNotFoundException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import com.example.CodeGeneratieRestAPI.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc
public class AccountControllerTest {
    //  We use MockMvc to simulate HTTP requests to our controller
    @Autowired
    private MockMvc mockMvc;

    //  We mock our services because we're not testing them here
    //  Note that we have to Mock all dependencies of our controller
    @MockBean
    private AccountService accountService;
    @MockBean
    private JwTokenProvider jwTokenProvider;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private TransactionRepository transactionRepository;

    private User getMockUser(Long id, UserType userType, String username) {
        User user = new User();
        user.setId(id);
        user.setUserType(userType);
        user.setUsername(username);
        user.setPassword(new HashedPassword("john"));
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
        account.setIsActive(true);
        account.setIsSavings(isSavings);
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

    private Transaction getMockTransaction(Long id, User user, Float amount, TransactionType transactionType, Account fromAccount, Account toAccount) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setAmount(amount);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionType(transactionType);
        transaction.setLabel("");
        transaction.setDescription("");
        return transaction;
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void addAccount() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Piet");
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.add(any(AccountRequestDTO.class), any(User.class))).thenReturn(getMockAccount("NL01-INHO-0000-0000-44", accountRequestDTO.getBalance(), user, false));

        String json = new ObjectMapper().writeValueAsString(accountRequestDTO).replace("null", "\"\"");

        //  Check if we get a 201 OK
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(post("/accounts").header("Authorization", "test").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.iban").value("NL01-INHO-0000-0000-44"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void addInvalidAccount() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Sjon");
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.add(any(AccountRequestDTO.class), any(User.class))).thenThrow(new AccountCreationException("You cannot set the IBAN of a new account"));

        String json = new ObjectMapper().writeValueAsString(accountRequestDTO).replace("null", "\"\"");

        //  Check if we get a 400 BAD REQUEST
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(post("/accounts").header("Authorization", "test").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You cannot set the IBAN of a new account"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void getAllAccounts() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Gerrit");

        List<Account> accounts = new ArrayList<>();
        accounts.add(getMockAccount("NL01-INHO-0000-0000-44", 1000F, user, false));
        accounts.add(getMockAccount("NL01-INHO-0000-0000-45", 1000F, user, false));

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.getAllAccounts("", null, user)).thenReturn(accounts);

        //  Check if we get a 200 OK
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(get("/accounts?search=&active=")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].iban").value("NL01-INHO-0000-0000-44"))
                .andExpect(jsonPath("$.data[1].iban").value("NL01-INHO-0000-0000-45"))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void getAllAccountsByUserId() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Gerrit");

        List<Account> accounts = new ArrayList<>();
        accounts.add(getMockAccount("NL01-INHO-0000-0000-44", 1000F, user, false));
        accounts.add(getMockAccount("NL01-INHO-0000-0000-45", 1000F, user, false));

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.getAllAccountsByUserId(user.getId(), user)).thenReturn(accounts);

        //  Check if we get a 200 OK
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(get("/accounts/user/1")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].iban").value("NL01-INHO-0000-0000-44"))
                .andExpect(jsonPath("$.data[1].iban").value("NL01-INHO-0000-0000-45"))
                .andExpect(jsonPath("$.data", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void getAllAccountsByUserIdThrowsException() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Gerrit");

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.getAllAccountsByUserId(2L, user)).thenThrow(new AccountNotFoundException("No accounts found"));

        //  Check if we get a 404 NOT FOUND
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(get("/accounts/user/2")).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No accounts found"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void getAccountByIban() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Gerrit");
        Account account = getMockAccount("NL01-INHO-0000-0000-44", 1000F, user, false);

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.getAccountByIban(account.getIban(), user)).thenReturn(new AccountData(new AccountResponseDTO(account), new AccountLimitsLeft()));

        //  Check if we get a 200 OK
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(get("/accounts/NL01-INHO-0000-0000-44")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.account.iban").value("NL01-INHO-0000-0000-44"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void getAccountByIbanThrowsException() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Gerrit");

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.getAccountByIban("NL01-INHO-0000-0000-44", user)).thenThrow(new AccountNotFoundException("No account found"));

        //  Check if we get a 404 NOT FOUND
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(get("/accounts/NL01-INHO-0000-0000-44")).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No account found"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void updateAccount() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Patrick");

        Account account = getMockAccount("NL01-INHO-0000-0000-44", 1000F, user, false);
        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();
        accountRequestDTO.setIban("NL01-INHO-0000-0000-44");

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.update(accountRequestDTO, user)).thenReturn(account);

        // Convert accountRequestDTO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(accountRequestDTO);

        // Perform the PUT request with the request body
        this.mockMvc.perform(put("/accounts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.iban").value("NL01-INHO-0000-0000-44"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void updateAccountThrowsException() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Patrick");

        AccountRequestDTO accountRequestDTO = getMockAccountRequestDTO();
        accountRequestDTO.setIban("NL01-INHO-0000-0000-44");

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.update(accountRequestDTO, user)).thenThrow(new AccountNotFoundException("No account found"));

        // Convert accountRequestDTO to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(accountRequestDTO);

        // Perform the PUT request with the request body
        this.mockMvc.perform(put("/accounts").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No account found"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void deleteAccount() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Patrick");

        Account account = getMockAccount("NL01-INHO-0000-0000-44", 1000F, user, false);

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.delete(account.getIban(), user)).thenReturn("Account with IBAN: " + account.getIban() + " has been set to inactive");

        //  Check if we get a 200 OK
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(delete("/accounts/NL01-INHO-0000-0000-44").with(csrf())).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account with IBAN: " + account.getIban() + " has been set to inactive"));
    }

    @Test
    @WithMockUser(username = "Dewi", password = "Dewi123", roles = "USER")
    void deleteAccountThrowsException() throws Exception {
        User user = getMockUser(1L, UserType.USER, "Patrick");

        when(userService.getLoggedInUser()).thenReturn(user);
        when(accountService.delete("NL01-INHO-0000-0000-44", user)).thenThrow(new AccountNotFoundException("No account found"));

        //  Check if we get a 404 NOT FOUND
        //  And if the JSON content matches our expected object
        this.mockMvc.perform(delete("/accounts/NL01-INHO-0000-0000-44").with(csrf())).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No account found"));
    }
}
