package com.example.CodeGeneratieRestAPI.models;

import com.example.CodeGeneratieRestAPI.exceptions.PasswordValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashedPasswordTest {

    private HashedPassword hashedPassword;

    @BeforeEach
    public void setUp() {
        hashedPassword = new HashedPassword("password");
    }

    @Test
    public void testValidatePassword() {
        Assertions.assertTrue(hashedPassword.validatePassword("password"));
        Assertions.assertThrows(PasswordValidationException.class, () -> hashedPassword.validatePassword("wrongPassword"));
    }

    @Test
    public void testGetPassword() {
        String password = hashedPassword.getPassword();
        Assertions.assertNotNull(password);
        Assertions.assertNotEquals("password", password);
    }
}