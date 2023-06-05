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

@Component
public class DataSeeder implements ApplicationRunner {

    // Declare services here (Autowired)
    @Autowired
    TransactionService transactionService;
    @Autowired
    UserService userService;

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
    }
    private void seedData() {
        // Insert code here to seed data (Example: Default user, default bank account, etc.)

        // Add a default user
        User defaultUser = new User();
        defaultUser.setUsername("admin");
        defaultUser.setPassword(new HashedPassword("admin"));
        defaultUser.setUserType(UserType.EMPLOYEE);

        userService.add(defaultUser);

        //  Add a few default bank accounts



        //  Add a few default transactions

    }
}
