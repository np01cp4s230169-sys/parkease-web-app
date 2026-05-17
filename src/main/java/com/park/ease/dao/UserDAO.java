package com.park.ease.dao;

import java.util.List;

import com.park.ease.model.User;

/**
 * UserDAO defines the data access contract for user-related database operations.
 * Implemented by UserDAOImpl using JDBC and MySQL.
 */
public interface UserDAO {

    /** Registers a new user record in the database. */
    boolean registerUser(User user);

    /** Authenticates a user by email and password. Returns User if valid, null otherwise. */
    User login(String email, String password);

    /** Retrieves all user records from the database. */
    List<User> getAllUsers();

    /** Updates the status of a user account (approved or rejected). */
    boolean updateUserStatus(int userId, String status);

    /** Retrieves a specific user by their unique ID. */
    User getUserById(int userId);

    /** Checks if a phone number is already registered. */
    boolean isPhoneExists(String phone);

    /** Checks if an email address is already registered. */
    boolean isEmailExists(String email);

    /** Retrieves all users with pending registration status. */
    List<User> getPendingUsers();

    /** Updates the password hash and salt for a user account. */
    boolean updatePassword(int userId, String passwordHash, String salt);

    /** Updates profile information (name, email, phone) for a user. */
    boolean updateProfile(User user);

    /** Checks if an email is used by any user other than the specified user. */
    boolean isEmailExistsForOtherUser(String email, int userId);

    /** Checks if a phone number is used by any user other than the specified user. */
    boolean isPhoneExistsForOtherUser(String phone, int userId);

    /** Updates the Base64-encoded profile picture for a user account. */
    boolean updateProfilePic(int userId, String base64Image);
}
