package com.park.ease.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {
    
    // Generates a random 16-byte salt
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Hashes password using SHA-256 and the provided salt
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verifies entered password against stored hash
    public static boolean verifyPassword(String inputPassword, String storedSalt, String storedHash) {
        if (inputPassword == null || storedSalt == null || storedHash == null) {
            return false;
        }

        String hashedInput = hashPassword(inputPassword, storedSalt);
        return storedHash.equals(hashedInput);
    }
}