package com.example.CodeGeneratieRestAPI.configuration;

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
import java.util.Random;


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

        // Create a default user
        User defaultUser = new User();
        defaultUser.setFirst_name("Luke");
        defaultUser.setLast_name("Huisman");
        defaultUser.setEmail("684651@student.inholland.nl");
        defaultUser.setUsername("admin");
        defaultUser.setPassword(new HashedPassword("admin"));
        defaultUser.setUserType(UserType.EMPLOYEE);
        userService.add(defaultUser);

        // Create a default customer user
        User defaultCustomer = new User();
        defaultCustomer.setFirst_name("Dewi");
        defaultCustomer.setLast_name("Cabret");
        defaultCustomer.setEmail("647824@student.inholland.nl");
        defaultCustomer.setUsername("Dewi");
        defaultCustomer.setPassword(new HashedPassword("Dewi"));
        defaultCustomer.setUserType(UserType.USER);
        userService.add(defaultCustomer);

        // Create another default customer user
        User defaultCustomer2 = new User();
        defaultCustomer2.setFirst_name("Devon");
        defaultCustomer2.setLast_name("van Wichen");
        defaultCustomer2.setEmail("650122@student.inholland.nl");
        defaultCustomer2.setUsername("Devon");
        defaultCustomer2.setPassword(new HashedPassword("Devon"));
        defaultCustomer2.setUserType(UserType.USER);
        userService.add(defaultCustomer2);

        // Create another default customer user
        User defaultCustomer3 = new User();
        defaultCustomer3.setFirst_name("Mark");
        defaultCustomer3.setLast_name("de Haan");
        defaultCustomer3.setEmail("Mark.deHaan@inholland.nl");
        defaultCustomer3.setUsername("Mark");
        defaultCustomer3.setPassword(new HashedPassword("Mark"));
        defaultCustomer3.setUserType(UserType.USER);
        userService.add(defaultCustomer3);

        userService.getAll().forEach(user -> {
            Random random = new Random();
            // Create default accounts
            Account account = new Account();
            account.setIban(null);
            account.setUser(user);
            account.setUserId(user.getId());
            account.setName(user.getFirst_name() + " " + user.getLast_name() + "$avings account");
            account.setDailyLimit(100f);
            account.setTransactionLimit(100f);
            account.setAbsoluteLimit(-50f);
            account.setBalance(1000f);
            account.setIsSavings(random.nextBoolean());
            account.setCreatedAt(new Date());
            account.setIsActive(true);
            accountService.addSeededAccount(account);
        });
    }
}
