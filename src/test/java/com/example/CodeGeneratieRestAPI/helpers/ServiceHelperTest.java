package com.example.CodeGeneratieRestAPI.helpers;

import com.example.CodeGeneratieRestAPI.exceptions.UserNotFoundException;
import com.example.CodeGeneratieRestAPI.models.Account;
import com.example.CodeGeneratieRestAPI.models.Transaction;
import com.example.CodeGeneratieRestAPI.models.User;
import com.example.CodeGeneratieRestAPI.repositories.AccountRepository;
import com.example.CodeGeneratieRestAPI.repositories.TransactionRepository;
import com.example.CodeGeneratieRestAPI.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;

class ServiceHelperTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ServiceHelper serviceHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceHelper = new ServiceHelper(accountRepository, userRepository, transactionRepository);
    }

    @Test
    void testCheckIfObjectExistsByIdentifier() {
        // Test case for Account object
        String iban = "NL01INHO0000000001";
        when(accountRepository.existsByIban(iban)).thenReturn(true);
        boolean result = serviceHelper.checkIfObjectExistsByIdentifier(iban, new Account());
        Assertions.assertTrue(result);

        // Test case for User object
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        result = serviceHelper.checkIfObjectExistsByIdentifier(userId, new User());
        Assertions.assertTrue(result);

        // Test case for Transaction object
        Long transactionId = 1L;
        when(transactionRepository.existsById(transactionId)).thenReturn(true);
        result = serviceHelper.checkIfObjectExistsByIdentifier(transactionId, new Transaction());
        Assertions.assertTrue(result);

        // Test case for invalid object type
        Object invalidObject = new Object();
        Assertions.assertThrows(IllegalArgumentException.class, () -> serviceHelper.checkIfObjectExistsByIdentifier(iban, invalidObject));
    }

    @Test
    void testGetLoggedInUser() {
        // Set up mock authentication and user details
        String username = "testuser";
        String password = "testpassword";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password, Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Set up mock user repository
        User user = new User();
        user.setUsername(username);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        // Test the getLoggedInUser method
        User loggedInUser = serviceHelper.getLoggedInUser();
        Assertions.assertEquals(user, loggedInUser);

        // Test case for user not found
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());
        Assertions.assertThrows(UserNotFoundException.class, () -> serviceHelper.getLoggedInUser());
    }
}