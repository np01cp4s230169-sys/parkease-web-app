package com.park.ease.controller;

import java.io.IOException;

import com.park.ease.service.InquiryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * PageServlet handles public-facing static pages and contact form submissions.
 * Manages navigation to the About and Contact pages, and processes
 * inquiry form submissions from the contact page.
 * 
 * URL Pattern: /pages
 * Actions (GET): about, contact
 * Actions (POST): contact form submission
 */
@WebServlet("/pages")
public class PageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependency for contact form inquiry handling
    private final InquiryService inquiryService = new InquiryService();

    /**
     * Handles GET requests - routes to About or Contact page.
     * Both pages are publicly accessible without authentication.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("about".equals(action)) {
                // Forward to About page - publicly accessible
                request.getRequestDispatcher("/about.jsp").forward(request, response);
            } else if ("contact".equals(action)) {
                // Forward to Contact page - publicly accessible
                request.getRequestDispatcher("/contact.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred while loading the page.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests - processes contact form inquiry submissions.
     * Validates name, email, and message fields before saving the inquiry.
     * Forwards back to contact page with appropriate success or error message.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String message = request.getParameter("message");

            // Trim inputs to remove accidental whitespace
            if (name != null) name = name.trim();
            if (email != null) email = email.trim();
            if (message != null) message = message.trim();

            // Validate all required fields are provided
            if (name == null || name.isEmpty()
                    || email == null || email.isEmpty()
                    || message == null || message.isEmpty()) {
                request.setAttribute("errorMessage", "All fields are required. Please fill in name, email, and message.");
                request.getRequestDispatcher("/contact.jsp").forward(request, response);
                return;
            }

            // Validate email format before saving inquiry
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                request.setAttribute("errorMessage", "Invalid email format. Please enter a valid email address.");
                request.getRequestDispatcher("/contact.jsp").forward(request, response);
                return;
            }

            // Submit inquiry through service layer
            boolean isSaved = inquiryService.submitContactForm(name, email, message);

            if (isSaved) {
                request.setAttribute("successMessage", "Inquiry sent successfully! Our support team will contact you shortly.");
            } else {
                request.setAttribute("errorMessage", "Failed to submit inquiry. Please try again later.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        }

        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }
}