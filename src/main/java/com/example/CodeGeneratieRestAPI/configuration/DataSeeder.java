package com.example.CodeGeneratieRestAPI.configuration;

import com.example.CodeGeneratieRestAPI.dtos.AccountRequestDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.HashedPassword;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.services.AccountService;
import com.example.CodeGeneratieRestAPI.services.TransactionService;
import com.example.CodeGeneratieRestAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DataSeeder implements ApplicationRunner {

    // Declare services here (Autowired)
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserService userService;

    @Autowired
    AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Insert code here to seed data (Example: Default user, default bank account, etc.)
        // Transaction transaction1 = new Transaction(1);
        // transactionService.add(transaction1);

        // Create a default user
        User defaultUser = new User();
        defaultUser.setFirst_name("Luke");
        defaultUser.setLast_name("Huisman");
        defaultUser.setEmail("684651@student.inholland.nl");
        defaultUser.setUsername("admin");
        defaultUser.setPassword(new HashedPassword("admin"));
        defaultUser.setUserType(UserType.EMPLOYEE);
        userService.add(defaultUser);

        //  Add a few default bank accounts
        addAccounts(defaultUser);


        //  Add a few default transactions

    }
    private void addAccounts(User user){
        AccountRequestDTO account1 = new AccountRequestDTO();
        account1.setIban("NL01INHO0000000001");
        account1.setUserId(user.getId());
        account1.setAccountName("Luke's account");
        account1.setDailyLimit(1000f);
        account1.setTransactionLimit(500f);
        account1.setAbsoluteLimit(100f);
        account1.setBalance(5000f);
        account1.setIsSavings(false);
        account1.setIsActive(true);
        accountService.add(account1);

        AccountRequestDTO account2 = new AccountRequestDTO();
        account2.setIban("NL02INHO0000000002");
        account2.setUserId(user.getId());
        account2.setAccountName("Luke's second account");
        account2.setDailyLimit(2000f);
        account2.setTransactionLimit(1000f);
        account2.setAbsoluteLimit(200f);
        account2.setBalance(8000f);
        account2.setIsSavings(false);
        account2.setIsActive(true);
        accountService.add(account2);

        AccountRequestDTO account3 = new AccountRequestDTO();
        account3.setIban("NL03INHO0000000003");
        account3.setUserId(user.getId());
        account3.setAccountName("Luke's third account");
        account3.setDailyLimit(1500f);
        account3.setTransactionLimit(800f);
        account3.setAbsoluteLimit(150f);
        account3.setBalance(6000f);
        account3.setIsSavings(false);
        account3.setIsActive(true);
        accountService.add(account3);

    }
}
