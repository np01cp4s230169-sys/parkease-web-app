package com.park.ease.controller;

import com.park.ease.service.InquiryService; // Clean, non-split package reference
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/pages")
public class PageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Straightforward single-class instantiation
    private final InquiryService inquiryService = new InquiryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("about".equals(action)) {
            request.getRequestDispatcher("/about.jsp").forward(request, response);
        } else if ("contact".equals(action)) {
            request.getRequestDispatcher("/contact.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String message = request.getParameter("message");
        
        boolean isSaved = inquiryService.submitContactForm(name, email, message);
        
        if (isSaved) {
            request.setAttribute("successMessage", "Inquiry sent successfully! Our support team will contact you shortly.");
        } else {
            request.setAttribute("errorMessage", "Operation failed. Please check form parameters and try again.");
        }
        
        request.getRequestDispatcher("/contact.jsp").forward(request, response);
    }
}
