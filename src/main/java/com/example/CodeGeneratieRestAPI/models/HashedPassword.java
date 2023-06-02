package com.example.CodeGeneratieRestAPI.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

@Data

@AllArgsConstructor
public class HashedPassword {

    private final byte[] hash;
    private final byte[] salt;

    public HashedPassword(String password) {
        SecureRandom random = new SecureRandom();
        salt = new byte[16];
        random.nextBytes(salt);
        this.hash = hashPasswordWithSalt(password, salt);
    }


    public boolean validatePassword(String password) {
        byte[] hashedPassword = hashPasswordWithSalt(password, salt);
        return Arrays.equals(this.hash, hashedPassword);
    }

    private byte[] hashPasswordWithSalt(String password, byte[] salt) {
        byte[] hashedPassword;
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hashedPassword = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return hashedPassword;
    }
}
