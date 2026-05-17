package com.park.ease.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.dao.UserDAO;
import com.park.ease.model.User;
import com.park.ease.util.DBConnectionUtil;
import com.park.ease.util.EncryptionUtil;

/**
 * UserDAOImpl provides JDBC-based implementation of the UserDAO interface.
 * Handles all database operations for user records in the users table.
 *
 * Uses PreparedStatements to prevent SQL injection attacks.
 * Passwords are verified using EncryptionUtil during login.
 * Profile images are stored as Base64-encoded strings in the profile_pic column.
 */
public class UserDAOImpl implements UserDAO {

    /**
     * Inserts a new user record into the users table.
     * Stores hashed password, salt, and optional Base64 profile image.
     */
    @Override
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, phone, email, password_hash, salt, role, status, profile_pic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getSalt());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getStatus());
            ps.setString(8, user.getProfilePic()); // Base64 image or null if not uploaded
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticates a user by retrieving their record by email and
     * verifying the entered password against the stored hash and salt.
     */
    @Override
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedSalt = rs.getString("salt");
                    String storedHash = rs.getString("password_hash");
                    String inputHash = EncryptionUtil.hashPassword(password, storedSalt);

                    // Verify hashed input matches stored hash
                    if (storedHash != null && storedHash.equals(inputHash)) {
                        return mapResultSetToUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a phone number already exists in the users table.
     * Used during registration to prevent duplicate accounts.
     */
    @Override
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if an email address already exists in the users table.
     * Used during registration to prevent duplicate accounts.
     */
    @Override
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all user records from the database.
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection con = DBConnectionUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Updates the status of a user account and records the update timestamp.
     */
    @Override
    public boolean updateUserStatus(int userId, String status) {
        String sql = "UPDATE users SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a specific user record by their unique ID.
     */
    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all users with pending status excluding admin accounts.
     * Used by admin to review and approve or reject new registrations.
     */
    @Override
    public List<User> getPendingUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE LOWER(status) = 'pending' AND LOWER(role) != 'admin'";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates the password hash and salt for a user account.
     * Called after verifying the current password in the service layer.
     */
    @Override
    public boolean updatePassword(int userId, String passwordHash, String salt) {
        String sql = "UPDATE users SET password_hash = ?, salt = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setString(2, salt);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates profile information (name, email, phone) for a user account.
     */
    @Override
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the Base64-encoded profile picture for a user account.
     * Called when a user uploads a new profile image from their profile page.
     */
    @Override
    public boolean updateProfilePic(int userId, String base64Image) {
        String sql = "UPDATE users SET profile_pic = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, base64Image);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if an email is used by any user other than the specified user.
     * Used during profile updates to prevent email conflicts.
     */
    @Override
    public boolean isEmailExistsForOtherUser(String email, int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id <> ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a phone number is used by any user other than the specified user.
     * Used during profile updates to prevent phone number conflicts.
     */
    @Override
    public boolean isPhoneExistsForOtherUser(String phone, int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ? AND user_id <> ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Maps a database ResultSet row to a User model object.
     * Reused across all methods that retrieve user records.
     * Includes profile_pic column mapping for Base64 image data.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setName(rs.getString("name"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setSalt(rs.getString("salt"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        u.setProfilePic(rs.getString("profile_pic")); // may be null for existing users
        u.setCreatedAt(rs.getTimestamp("created_at"));
        u.setUpdatedAt(rs.getTimestamp("updated_at"));
        return u;
    }
}
