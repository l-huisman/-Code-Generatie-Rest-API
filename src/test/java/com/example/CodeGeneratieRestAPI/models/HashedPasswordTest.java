// BEGIN: 5f7d9a7d7f6a
package com.example.CodeGeneratieRestAPI.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashedPasswordTest {

    @Test
    void testValidatePassword() {
        HashedPassword hashedPassword = new HashedPassword("password");
        assertTrue(hashedPassword.validatePassword("password"));
        assertFalse(hashedPassword.validatePassword("wrongpassword"));
    }

    @Test
    void testGetPassword() {
        HashedPassword hashedPassword = new HashedPassword("password");
        String password = hashedPassword.getPassword();
        assertNotNull(password);
        assertTrue(password.contains(":"));
    }
}
// END: 5f7d9a7d7f6a