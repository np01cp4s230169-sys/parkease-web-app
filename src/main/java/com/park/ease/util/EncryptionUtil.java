package com.park.ease.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * EncryptionUtil provides password hashing and verification utilities
 * for secure user authentication in the ParkEase system.
 * 
 * Uses SHA-256 hashing algorithm with a random salt to protect
 * user passwords. Passwords are never stored in plain text.
 * 
 * Usage flow:
 *   1. Registration: generateSalt() then hashPassword(password, salt)
 *   2. Login: verifyPassword(inputPassword, storedSalt, storedHash)
 */
public class EncryptionUtil {

    /**
     * Generates a cryptographically secure random 16-byte salt.
     * Salt is Base64 encoded for safe storage in the database.
     * 
     * @return Base64-encoded salt string
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password using SHA-256 combined with the provided salt.
     * Returns null if either parameter is null.
     * 
     * @param password the plain text password to hash
     * @param salt     the Base64-encoded salt string
     * @return Base64-encoded hashed password, or null if input is invalid
     */
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
            throw new RuntimeException("Error hashing password: SHA-256 algorithm not available.", e);
        }
    }

    /**
     * Verifies a plain text password against a stored salt and hash.
     * Hashes the input with the stored salt and compares to the stored hash.
     * Returns false if any parameter is null.
     * 
     * @param inputPassword the plain text password entered by the user
     * @param storedSalt    the Base64-encoded salt stored in the database
     * @param storedHash    the Base64-encoded hash stored in the database
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String inputPassword, String storedSalt, String storedHash) {
        if (inputPassword == null || storedSalt == null || storedHash == null) {
            return false;
        }

        String hashedInput = hashPassword(inputPassword, storedSalt);
        return storedHash.equals(hashedInput);
    }
}