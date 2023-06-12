package com.example.CodeGeneratieRestAPI.controllers;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.exceptions.TransactionAmountNotValidException;
import com.example.CodeGeneratieRestAPI.exceptions.TransactionNotOwnedException;
import com.example.CodeGeneratieRestAPI.jwt.JwTokenProvider;
import com.example.CodeGeneratieRestAPI.models.*;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

// IntelliJ probably already loads the spring context
// Just in case, we use @ExtendWith to ensure the context is loaded.
// We use @WebMvcTest because it allows us to only test the controller
// and not load in anything else (repositories, services etc.)
@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc
class TransactionControllerTest {

    // We use a unit test for controller methods to test any custom logic we have in there
    // In this case, we're using ModelMapper and we have to check if this produces the correct results


    // We use mockMvc to simulate HTTP requests to a controller class
    @Autowired
    private MockMvc mockMvc;

    // We mock our service, because we don't want to test it here
    // Note that we have to Mock all dependencies our controller code uses if we use @WebMvcTest
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private JwTokenProvider jwTokenProvider;


    // We could also add ObjectMapper to convert objects to JSON for us

    @BeforeEach
    void setUp() {
    }

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
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void getAll() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String search = "";
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(getMockTransaction(1L, user, 60F, TransactionType.WITHDRAW, fromAccount, null));
        transactions.add(getMockTransaction(2L, user, 60F, TransactionType.WITHDRAW, fromAccount, null));

        Integer pageNumber = 0, pageSize = 10;

        Pageable pageableRequest = PageRequest.of(pageNumber, pageSize);

        Page<Transaction> pageTransactions = new PageImpl<>(transactions, pageableRequest, transactions.size());

        when(transactionService.getAll(user, startDate, endDate, "", "", 0F, pageNumber, pageSize)).thenReturn(pageTransactions);
        when(userService.getLoggedInUser()).thenReturn(user);

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(get("/transactions?start_date=" + DateFormat.format(startDate) + "&end_date=" + DateFormat.format(endDate) + "&iban=&amount_relation=&amount=&page_number=" + pageNumber + "&page_size=" + pageSize).header("Authorization", "test")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(transactions.size())))
                .andExpect(jsonPath("$.data[0].amount").value("60.0"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void getAllByUserId() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(getMockTransaction(1L, user, 60F, TransactionType.WITHDRAW, fromAccount, null));
        transactions.add(getMockTransaction(2L, user, 60F, TransactionType.WITHDRAW, fromAccount, null));

        when(transactionService.getAllByUser(user)).thenReturn(transactions);
        when(userService.getLoggedInUser()).thenReturn(user);

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(get("/transactions/user").header("Authorization", "test")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(transactions.size())))
                .andExpect(jsonPath("$.data[0].amount").value("60.0"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void getById() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        Transaction transaction = getMockTransaction(1L, user, 60F, TransactionType.WITHDRAW, fromAccount, null);

        when(transactionService.getById(user, transaction.getId())).thenReturn(transaction);
        when(userService.getLoggedInUser()).thenReturn(user);

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(get("/transactions/" + transaction.getId()).header("Authorization", "test")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value("60.0"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void getAllByAccountIban() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String search = "";
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(getMockTransaction(1L, user, 60F, TransactionType.WITHDRAW, fromAccount, null));
        transactions.add(getMockTransaction(2L, user, 60F, TransactionType.WITHDRAW, fromAccount, null));

        when(transactionService.getAllByAccountIban(user, fromAccount.getIban(), startDate, endDate, "", "", 0F, 0, 10)).thenReturn(transactions);
        when(userService.getLoggedInUser()).thenReturn(user);

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(get("/transactions/accounts/" + fromAccount.getIban() + "?start_date=" + DateFormat.format(startDate) + "&end_date=" + DateFormat.format(endDate) + "&search=").header("Authorization", "test")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(transactions.size())))
                .andExpect(jsonPath("$.data[0].amount").value("60.0"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void transactionIsOwnedByUser() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        Transaction transaction = getMockTransaction(1L, user, 60F, TransactionType.WITHDRAW, fromAccount, null);

        when(transactionService.transactionIsOwnedByUser(user, transaction.getId())).thenReturn(transaction);
        when(userService.getLoggedInUser()).thenReturn(user);

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(get("/transactions/owns/" + transaction.getId()).header("Authorization", "test")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You own this transaction"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void transactionIsNotOwnedByUser() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        User user1 = getMockUser(2L, UserType.USER, "john1");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        Transaction transaction = getMockTransaction(1L, user1, 60F, TransactionType.WITHDRAW, fromAccount, null);

        when(transactionService.transactionIsOwnedByUser(user, transaction.getId())).thenThrow(new TransactionNotOwnedException("This user does not own the specified transaction"));
        when(userService.getLoggedInUser()).thenReturn(user);

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(get("/transactions/owns/" + transaction.getId()).header("Authorization", "test")).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This user does not own the specified transaction"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void addTransaction() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        Transaction transaction = getMockTransaction(1L, user, 60F, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(transactionService.add(any(User.class), any(TransactionRequestDTO.class))).thenReturn(transaction);
        when(userService.getLoggedInUser()).thenReturn(user);

        String json = new ObjectMapper().writeValueAsString(transactionRequestDTO).replace("null", "\"\"");

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(post("/transactions").header("Authorization", "test").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value("60.0"));
    }

    @Test
    @WithMockUser(username = "Devon", password = "pwd", roles = "USER")
    void addInvalidTransaction() throws Exception {
        User user = getMockUser(1L, UserType.USER, "john");
        Account fromAccount = getMockAccount("123456", 1000F, user, false);

        Transaction transaction = getMockTransaction(1L, user, 0F, TransactionType.WITHDRAW, fromAccount, null);
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(fromAccount.getIban(), null, "WITHDRAW", transaction.getAmount(), transaction.getLabel(), transaction.getDescription());

        when(transactionService.add(any(User.class), any(TransactionRequestDTO.class))).thenThrow(new TransactionAmountNotValidException("The transaction amount can't be zero."));
        when(userService.getLoggedInUser()).thenReturn(user);

        String json = new ObjectMapper().writeValueAsString(transactionRequestDTO).replace("null", "\"\"");

        // Check if we get a 200 OK
        // And if the JSON content matches our expectations
        this.mockMvc.perform(post("/transactions").header("Authorization", "test").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The transaction amount can't be zero."));
    }

    // @Test
    // void add() throws Exception {

    //     // Arrange
    //     when(carService.add(any(Car.class))).thenReturn(new Car(2, "Mercedes", 2000, "CD4567", null));

    //     // Act & Assert
    //     this.mockMvc.perform(post("/cars")
    //                     .contentType(MediaType.APPLICATION_JSON_VALUE)
    //                     /// String literals in Java 17: enclose in """
    //                     .content("""
    //                              {
    //                                 "brand": "Mercedes",
    //                                 "weight": 2000,
    //                                 "licensePlate": "CD4567"
    //                               }
    //                             """))
    //             // But since we used any(Car.class) a simple {} should be enough
    //             .andDo(print())
    //             .andExpect(status().isCreated())
    //             .andExpect(jsonPath("$.brand").value("Mercedes"));
    // }
}