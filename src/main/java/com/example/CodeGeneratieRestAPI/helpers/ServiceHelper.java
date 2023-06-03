package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceHelper {
    @Autowired
    private static AccountRepository accountRepository;
    @Autowired
    private static UserRepository userRepository;
    @Autowired
    private static TransactionRepository transactionRepository;

    public static <T, U> boolean checkIfObjectExistsByIdentifier(T identifier, U objectDataType) {
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
}
