package com.park.ease.service;

import com.park.ease.dao.InquiryDAO;
import com.park.ease.daoimpl.InquiryDAOImpl; // CRITICAL FIX: Imports the implementation class
import com.park.ease.model.Inquiry;
import java.util.List; // CRITICAL FIX: Added to support the data collection list

public class InquiryService {
    
    // Direct link to your Data Access Layer implementation
    private final InquiryDAO inquiryDAO = new InquiryDAOImpl();

    public boolean submitContactForm(String name, String email, String message) {
        // Enforce strict business data boundaries
        if (name == null || name.trim().isEmpty() || 
            email == null || email.trim().isEmpty() || 
            message == null || message.trim().isEmpty()) {
            return false;
        }
        
        // Basic syntax matching validation check
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }
        
        Inquiry inquiry = new Inquiry(name.trim(), email.trim(), message.trim());
        return inquiryDAO.saveInquiry(inquiry);
    }

    // LAYER BRIDGING METHOD: FETCHES ALL SUBMITTED CUSTOMER SUPPORT RECORD ARRAYS
    public List<Inquiry> viewAllInquiries() {
        return inquiryDAO.getAllInquiries();
    }
}
