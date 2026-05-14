package com.park.ease.dao;

import com.park.ease.model.User;
import java.util.List;

public interface UserDAO {
    boolean registerUser(User user);
    User login(String email, String password);
    List<User> getAllUsers();
    boolean updateUserStatus(int userId, String status);
    User getUserById(int userId);
    boolean isPhoneExists(String phone);
    boolean isEmailExists(String email);
    List<User> getPendingUsers();
    boolean updatePassword(int userId, String passwordHash, String salt);

    // Added for profile update
    boolean updateProfile(User user);
    boolean isEmailExistsForOtherUser(String email, int userId);
    boolean isPhoneExistsForOtherUser(String phone, int userId);
}