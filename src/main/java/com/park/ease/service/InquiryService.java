package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.InquiryDAO;
import com.park.ease.daoimpl.InquiryDAOImpl;
import com.park.ease.model.Inquiry;

/**
 * InquiryService handles business logic for contact form submissions
 * in the ParkEase system.
 * 
 * Validates inquiry data before saving and provides retrieval
 * of all submitted inquiries for admin review on the dashboard.
 */
public class InquiryService {

    // DAO dependency for inquiry data operations
    private final InquiryDAO inquiryDAO = new InquiryDAOImpl();

    /**
     * Processes a contact form submission by validating fields
     * and saving the inquiry to the database.
     * 
     * @param name    full name of the person submitting the inquiry
     * @param email   email address for admin to respond to
     * @param message the inquiry or support message content
     * @return true if inquiry was saved successfully, false if validation fails
     */
    public boolean submitContactForm(String name, String email, String message) {
        // Validate all required fields are provided
        if (name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || message == null || message.trim().isEmpty()) {
            return false;
        }

        // Basic email format validation
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        Inquiry inquiry = new Inquiry(name.trim(), email.trim(), message.trim());
        return inquiryDAO.saveInquiry(inquiry);
    }

    /**
     * Retrieves all submitted inquiries for admin review.
     * 
     * @return list of all Inquiry objects submitted through the contact form
     */
    public List<Inquiry> viewAllInquiries() {
        return inquiryDAO.getAllInquiries();
    }
}