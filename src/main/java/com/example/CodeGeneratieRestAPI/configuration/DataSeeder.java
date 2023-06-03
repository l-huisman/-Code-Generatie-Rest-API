package com.example.CodeGeneratieRestAPI.configuration;

import com.example.CodeGeneratieRestAPI.models.HashedPassword;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.models.UserType;
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
//        Transaction transaction1 = new Transaction(1);
//        transactionService.add(transaction1);

        // Add a default user
        User defaultUser = new User();
        defaultUser.setUsername("admin");
        defaultUser.setPassword(new HashedPassword("admin"));
        defaultUser.setUserType(UserType.EMPLOYEE);

        userService.add(defaultUser);
    }
}
