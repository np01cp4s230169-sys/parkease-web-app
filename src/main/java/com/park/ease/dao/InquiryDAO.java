package com.park.ease.dao;

import java.util.List;

import com.park.ease.model.Inquiry;

/**
 * InquiryDAO defines the data access contract for contact form inquiry operations.
 * Implemented by InquiryDAOImpl using JDBC and MySQL.
 */
public interface InquiryDAO {

    /** Saves a new inquiry submitted through the contact form. */
    boolean saveInquiry(Inquiry inquiry);

    /** Retrieves all submitted inquiries for admin review on the dashboard. */
    List<Inquiry> getAllInquiries();
}