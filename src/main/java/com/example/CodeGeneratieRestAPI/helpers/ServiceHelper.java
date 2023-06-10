package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.exceptions.AccountNotFoundException;
import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ServiceHelper {
    private static AccountRepository accountRepository;
    private static UserRepository userRepository;
    private static TransactionRepository transactionRepository;

    //  This method checks if an object exists by its identifier (IBAN, user id, transaction id) and its data type (Account, User, Transaction)
    public static <T> boolean checkIfObjectExistsByIdentifier(T identifier, Object objectDataType) {
        switch (objectDataType.getClass().getSimpleName()) {
            case "Account":
                return accountRepository.existsByIban((String) identifier);
            case "User":
                return userRepository.existsById((Long) identifier);
            case "Transaction":
                return transactionRepository.existsById((Long) identifier);
            default:
                throw new IllegalArgumentException("Object type is not valid");
        }
    }

    //  This method gets the logged-in user
//    public User getLoggedInUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        return userRepository.findUserByUsername(userDetails.getUsername()).orElseThrow(() -> new UserNotFoundException("User with username: " + userDetails.getUsername() + " does not exist"));
//    }
}