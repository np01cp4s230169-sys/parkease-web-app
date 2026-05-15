package com.park.ease.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Inquiry model class representing a contact form submission in the ParkEase system.
 * Maps to the 'inquiries' table in the database.
 * 
 * Inquiries are submitted through the public Contact page and are visible
 * to the admin on the dashboard for follow-up support.
 * 
 * Implements Serializable to allow safe storage and transfer of inquiry objects.
 */
public class Inquiry implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;                  // Primary key - unique inquiry identifier
    private String name;             // Full name of the person submitting the inquiry
    private String email;            // Email address for admin to respond to
    private String message;          // The inquiry or support message content
    private Timestamp submittedAt;   // Timestamp when the inquiry was submitted

    /** Default constructor required for object instantiation. */
    public Inquiry() {}

    /**
     * Parameterized constructor for convenient inquiry creation in the service layer.
     * 
     * @param name    full name of the inquiry sender
     * @param email   email address of the inquiry sender
     * @param message content of the inquiry or support request
     */
    public Inquiry(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }
}