package com.park.ease.model;

import java.sql.Timestamp;

/**
 * User model class representing a registered user in the ParkEase system.
 * Maps to the 'users' table in the database.
 * 
 * Roles: ADMIN (manages system), USER (books parking slots)
 * Status: pending (awaiting approval), approved (can login), rejected (denied)
 * 
 * Passwords are never stored in plain text - only the hash and salt
 * are stored, processed by EncryptionUtil.
 */
public class User {

    private int userId;           // Primary key - unique user identifier
    private String name;          // Full name - letters and spaces only
    private String phone;         // Unique phone number - 10 to 15 digits
    private String email;         // Unique email address used for login
    private String passwordHash;  // SHA-256 hashed password
    private String salt;          // Random salt used during password hashing
    private String role;          // User role: ADMIN or USER
    private String status;        // Account status: pending, approved, or rejected
    private Timestamp createdAt;  // Timestamp when account was created
    private Timestamp updatedAt;  // Timestamp when account was last updated

    /** Default constructor required for object instantiation. */
    public User() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}