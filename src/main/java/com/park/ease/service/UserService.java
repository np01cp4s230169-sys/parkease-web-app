package com.park.ease.service;

import com.park.ease.dao.UserDAO;
import com.park.ease.daoimpl.UserDAOImpl;
import com.park.ease.model.User;
import com.park.ease.util.EncryptionUtil;
import java.util.List;

public class UserService {

    private UserDAO userDAO = new UserDAOImpl();

    public boolean registerUser(User user, String plainPassword) {
        if (user == null || plainPassword == null || plainPassword.isEmpty()) {
            return false;
        }

        String salt = EncryptionUtil.generateSalt();
        String hashedPassword = EncryptionUtil.hashPassword(plainPassword, salt);

        user.setSalt(salt);
        user.setPasswordHash(hashedPassword);

        if (user.getRole() == null) {
            user.setRole("USER");
        }

        if (user.getStatus() == null) {
            user.setStatus("pending");
        }

        return userDAO.registerUser(user);
    }

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

    public boolean isEmailExists(String email) {
        return userDAO.isEmailExists(email);
    }

    public boolean isPhoneExists(String phone) {
        return userDAO.isPhoneExists(phone);
    }

    /* 
     * CRITICAL BUG FIX LINK: 
     * Changed from userDAO.getAllUsers() to target userDAO.getPendingUsers().
     * This isolates pending accounts and clears them from the view upon status update.
     */
    public List<User> getPendingUsers() {
        return userDAO.getPendingUsers();
    }

    public boolean changeUserStatus(int userId, String status) {
        return userDAO.updateUserStatus(userId, status);
    }

    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        User user = userDAO.getUserById(userId);

        if (user == null || currentPassword == null || newPassword == null
                || currentPassword.isEmpty() || newPassword.isEmpty()) {
            return false;
        }

        if (!EncryptionUtil.verifyPassword(currentPassword, user.getSalt(), user.getPasswordHash())) {
            return false;
        }

        String newSalt = EncryptionUtil.generateSalt();
        String newHashedPassword = EncryptionUtil.hashPassword(newPassword, newSalt);

        return userDAO.updatePassword(userId, newHashedPassword, newSalt);
    }

    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }

    public boolean isEmailExistsForOtherUser(String email, int userId) {
        return userDAO.isEmailExistsForOtherUser(email, userId);
    }

    public boolean isPhoneExistsForOtherUser(String phone, int userId) {
        return userDAO.isPhoneExistsForOtherUser(phone, userId);
    }
}
