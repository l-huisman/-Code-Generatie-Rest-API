package com.example.CodeGeneratieRestAPI.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashedPasswordTest {

    @Test
    public void testValidatePassword() {
        HashedPassword hashedPassword = new HashedPassword("password");
        assertTrue(hashedPassword.validatePassword("password"));
        assertFalse(hashedPassword.validatePassword("wrongpassword"));
    }

    @Test
    public void testGetPassword() {
        HashedPassword hashedPassword = new HashedPassword("password");
        String password = hashedPassword.getPassword();
        String[] parts = password.split(":");
        byte[] hash = java.util.Base64.getDecoder().decode(parts[0]);
        byte[] salt = java.util.Base64.getDecoder().decode(parts[1]);
        HashedPassword newHashedPassword = new HashedPassword(hash, salt);
        assertTrue(newHashedPassword.validatePassword("password"));
    }
}