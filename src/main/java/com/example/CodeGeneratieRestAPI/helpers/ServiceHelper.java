package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceHelper {
    private static AccountRepository accountRepository;
    private static UserRepository userRepository;
    private static TransactionRepository transactionRepository;

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

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        ServiceHelper.accountRepository = accountRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        ServiceHelper.userRepository = userRepository;
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        ServiceHelper.transactionRepository = transactionRepository;
    }
}
