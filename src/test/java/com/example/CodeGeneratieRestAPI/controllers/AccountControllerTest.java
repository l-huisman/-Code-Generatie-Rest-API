package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.TransactionAmountNotValidException;
import com.example.CodeGeneratieRestAPI.exceptions.TransactionNotOwnedException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import com.example.CodeGeneratieRestAPI.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        this.mockMvc.perform(post("/accounts").header("Authorization", "test").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.iban").value("NL01-INHO-0000-0000-44"));
    }


}
