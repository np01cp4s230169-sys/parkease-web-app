package com.park.ease.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.dao.InquiryDAO;
import com.park.ease.model.Inquiry;
import com.park.ease.util.DBConnectionUtil;

/**
 * InquiryDAOImpl provides JDBC-based implementation of the InquiryDAO interface.
 * Handles all database operations for inquiry records in the inquiries table.
 */
public class InquiryDAOImpl implements InquiryDAO {

    /**
     * Inserts a new inquiry record submitted through the contact form.
     */
    @Override
    public boolean saveInquiry(Inquiry inquiry) {
        String sql = "INSERT INTO inquiries (name, email, message) VALUES (?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inquiry.getName());
            ps.setString(2, inquiry.getEmail());
            ps.setString(3, inquiry.getMessage());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all submitted inquiries ordered by submission time (newest first).
     * Used by admin to review contact form submissions on the dashboard.
     */
    @Override
    public List<Inquiry> getAllInquiries() {
        List<Inquiry> list = new ArrayList<>();
        String sql = "SELECT id, name, email, message, submitted_at FROM inquiries ORDER BY submitted_at DESC";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Inquiry inquiry = new Inquiry();
                inquiry.setId(rs.getInt("id"));
                inquiry.setName(rs.getString("name"));
                inquiry.setEmail(rs.getString("email"));
                inquiry.setMessage(rs.getString("message"));
                inquiry.setSubmittedAt(rs.getTimestamp("submitted_at"));
                list.add(inquiry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}