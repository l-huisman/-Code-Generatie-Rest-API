package com.example.CodeGeneratieRestAPI.models;

import lombok.Data;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

@Data
public class HashedPassword {
    private final byte[] hash;
    private final byte[] salt;

    public HashedPassword(byte[] hashedPassword, byte[] salt) {
        this.hash = hashedPassword;
        this.salt = salt;
    }

    public HashedPassword(String password) {
        //generate salt
        SecureRandom random = new SecureRandom();
        salt = new byte[16];
        random.nextBytes(salt);

        this.hash = hashPasswordWithSalt(password, salt);
    }

    public HashedPassword() {
        SecureRandom random = new SecureRandom();
        salt = new byte[16];
        random.nextBytes(salt);
        this.hash = hashPasswordWithSalt("password", salt);
    }

    public boolean validatePassword(String password) {
        byte[] newHash = hashPasswordWithSalt(password, salt);
        return Arrays.equals(this.hash, newHash);
    }

    private byte[] hashPasswordWithSalt(String password, byte[] salt) {
        byte[] newHash;
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            newHash = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return newHash;
    }

    public String getPassword() {
        return Base64.getEncoder().encodeToString(this.hash);
    }
}

