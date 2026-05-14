package com.park.ease.daoimpl;

import com.park.ease.dao.InquiryDAO; // CRITICAL FIX: Imports the interface contract
import com.park.ease.model.Inquiry;
import com.park.ease.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // CRITICAL FIX: Added for data stream navigation
import java.sql.SQLException;
import java.util.ArrayList; // CRITICAL FIX: Added to support data row collections
import java.util.List; // CRITICAL FIX: Added to match contract interface properties

public class InquiryDAOImpl implements InquiryDAO {
    
    @Override
    public boolean saveInquiry(Inquiry inquiry) {
        String sql = "INSERT INTO inquiries (name, email, message) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, inquiry.getName());
            ps.setString(2, inquiry.getEmail());
            ps.setString(3, inquiry.getMessage());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace(); 
            return false;
        }
    }

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
