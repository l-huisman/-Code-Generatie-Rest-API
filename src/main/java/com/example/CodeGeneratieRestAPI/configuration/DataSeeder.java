package com.example.CodeGeneratieRestAPI.configuration;

import com.example.CodeGeneratieRestAPI.dtos.TransactionRequestDTO;
import com.example.CodeGeneratieRestAPI.dtos.UserRequestDTO;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
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
    UserRepository userRepository;

    @Autowired
    AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Create a default user
        UserRequestDTO defaultEmployee = new UserRequestDTO("Luke", "Huisman", "admin", "admin",
                "684651@student.inholland.nl", UserType.EMPLOYEE);
        userService.add(defaultEmployee);

        // Create a default customer user
        UserRequestDTO defaultCustomer = new UserRequestDTO("Dewi", "Cabret", "Dewi", "Dewi",
                "647824@student.inholland.nl", UserType.USER);
        userService.add(defaultCustomer);

        // Create another default customer user
        UserRequestDTO defaultCustomer2 = new UserRequestDTO("Devon", "van Wichen", "Devon", "Devon",
                "650122@student.inholland.nl", UserType.USER);
        userService.add(defaultCustomer2);

        // Create another default customer user
        UserRequestDTO defaultCustomer3 = new UserRequestDTO("Mark", "de Haan", "Mark", "Mark",
                "Mark.deHaan@inholland.nl", UserType.USER);
        userService.add(defaultCustomer3);

        userRepository.findAll().forEach(user -> {
            if (user.getUserType() == UserType.EMPLOYEE)
                return;
            // Create default accounts
            Account account = new Account();
            account.setIban(null);
            account.setUser(user);
            account.setUserId(user.getId());
            account.setName(user.getFirstName() + " " + user.getLastName() + " $avings account");
            account.setDailyLimit(100f);
            account.setTransactionLimit(100f);
            account.setAbsoluteLimit(-50f);
            account.setBalance(1000f);
            account.setIsSavings(random.nextBoolean());
            account.setCreatedAt(new Date());
            account.setIsActive(true);
            accountService.addSeededAccount(account);

            TransactionRequestDTO transaction = new TransactionRequestDTO();
            transaction.setTransactionType("DEPOSIT");
            transaction.setToAccountIban(account.getIban());
            transaction.setAmount(180f);
            transaction.setLabel("Initial deposit");
            transaction.setDescription("Initial deposit");

            TransactionRequestDTO transaction2 = new TransactionRequestDTO();
            transaction2.setTransactionType("WITHDRAW");
            transaction2.setFromAccountIban(account.getIban());
            transaction2.setAmount(40f);
            transaction2.setLabel("Initial withdraw");
            transaction2.setDescription("Initial withdraw");

            transactionService.add(user, transaction);
            transactionService.add(user, transaction2);
        });

                // Create another default customer user
        UserRequestDTO defaultCustomer4 = new UserRequestDTO("John", "Doe", "Johny", "hereIs!",
                "John.Doe@inholland.nl", UserType.USER);
        userService.add(defaultCustomer4);
    }
}
