package com.example.CodeGeneratieRestAPI.configuration;

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

//    @Autowired
//    UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Insert code here to seed data (Example: Default user, default bank account, etc.)
//        Transaction transaction1 = new Transaction(1);
//        transactionService.add(transaction1);
    }
}
