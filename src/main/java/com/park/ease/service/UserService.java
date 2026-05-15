package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.UserDAO;
import com.park.ease.daoimpl.UserDAOImpl;
import com.park.ease.model.User;
import com.park.ease.util.EncryptionUtil;

/**
 * UserService handles all user-related business logic in the ParkEase system.
 * Manages user registration, authentication, profile updates, password changes,
 * and admin approval of user accounts.
 * 
 * Passwords are hashed using EncryptionUtil before being stored in the database.
 * Plain text passwords are never passed to the DAO layer.
 */
public class UserService {

    // DAO dependency for user data operations
    private UserDAO userDAO = new UserDAOImpl();

    /**
     * Registers a new user by generating a salt, hashing the password,
     * and saving the user record with pending status.
     * 
     * @param user          the user object with name, email, phone, role, status
     * @param plainPassword the plain text password entered during registration
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUser(User user, String plainPassword) {
        if (user == null || plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }

        // Generate salt and hash password before storing
        String salt = EncryptionUtil.generateSalt();
        String hashedPassword = EncryptionUtil.hashPassword(plainPassword, salt);
        user.setSalt(salt);
        user.setPasswordHash(hashedPassword);

        // Apply default role and status if not already set
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getStatus() == null) {
            user.setStatus("pending");
        }

        return userDAO.registerUser(user);
    }

    /**
     * Authenticates a user by email and password.
     * Returns the user object if credentials are valid, null otherwise.
     * 
     * @param email    the email address entered at login
     * @param password the plain text password entered at login
     * @return authenticated User object or null if credentials are invalid
     */
    public User authenticateUser(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        email = email.trim();

        if (email.isEmpty() || password.isEmpty()) {
            return null;
        }

        return userDAO.login(email, password);
    }

    /**
     * Checks if an email address is already registered in the system.
     * Used during registration to prevent duplicate accounts.
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailExists(String email) {
        return userDAO.isEmailExists(email);
    }

    /**
     * Checks if a phone number is already registered in the system.
     * Used during registration to prevent duplicate accounts.
     * 
     * @param phone the phone number to check
     * @return true if phone exists, false otherwise
     */
    public boolean isPhoneExists(String phone) {
        return userDAO.isPhoneExists(phone);
    }

    /**
     * Retrieves all users with pending registration status.
     * Used by admin to review and approve or reject new accounts.
     * 
     * @return list of users awaiting admin approval
     */
    public List<User> getPendingUsers() {
        return userDAO.getPendingUsers();
    }

    /**
     * Updates the status of a user account (approved or rejected).
     * Called by admin during user approval management.
     * 
     * @param userId the ID of the user to update
     * @param status the new status: approved or rejected
     * @return true if update was successful, false otherwise
     */
    public boolean changeUserStatus(int userId, String status) {
        return userDAO.updateUserStatus(userId, status);
    }

    /**
     * Retrieves a user record by their unique ID.
     * 
     * @param userId the ID of the user to retrieve
     * @return User object or null if not found
     */
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    /**
     * Changes a user's password after verifying the current password.
     * Generates a new salt and hashes the new password before saving.
     * 
     * @param userId          the ID of the user changing password
     * @param currentPassword the current plain text password for verification
     * @param newPassword     the new plain text password to set
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        User user = userDAO.getUserById(userId);

        if (user == null || currentPassword == null || newPassword == null
                || currentPassword.isEmpty() || newPassword.isEmpty()) {
            return false;
        }

        // Verify current password matches stored hash before allowing change
        if (!EncryptionUtil.verifyPassword(currentPassword, user.getSalt(), user.getPasswordHash())) {
            return false;
        }

        // Generate new salt and hash for the new password
        String newSalt = EncryptionUtil.generateSalt();
        String newHashedPassword = EncryptionUtil.hashPassword(newPassword, newSalt);
        return userDAO.updatePassword(userId, newHashedPassword, newSalt);
    }

    /**
     * Updates user profile information (name, email, phone).
     * 
     * @param user the user object with updated details
     * @return true if update was successful, false otherwise
     */
    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }

    /**
     * Checks if an email is used by any user other than the specified user.
     * Used during profile updates to prevent email conflicts.
     * 
     * @param email  the email address to check
     * @param userId the current user's ID to exclude from the check
     * @return true if email exists for another user, false otherwise
     */
    public boolean isEmailExistsForOtherUser(String email, int userId) {
        return userDAO.isEmailExistsForOtherUser(email, userId);
    }

    /**
     * Checks if a phone number is used by any user other than the specified user.
     * Used during profile updates to prevent phone number conflicts.
     * 
     * @param phone  the phone number to check
     * @param userId the current user's ID to exclude from the check
     * @return true if phone exists for another user, false otherwise
     */
    public boolean isPhoneExistsForOtherUser(String phone, int userId) {
        return userDAO.isPhoneExistsForOtherUser(phone, userId);
    }
}