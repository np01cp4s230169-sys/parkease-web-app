package com.park.ease.dao;

import java.util.List; // CRITICAL FIX: Imports the List contract type
import com.park.ease.model.Inquiry;

public interface InquiryDAO {
    boolean saveInquiry(Inquiry inquiry);
    
    // CONTRACT DEFINITION FOR FETCHING ALL INQUIRIES
    List<Inquiry> getAllInquiries();
}
