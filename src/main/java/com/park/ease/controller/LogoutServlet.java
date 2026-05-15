package com.park.ease.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LogoutServlet handles user logout by invalidating the current session
 * and redirecting to the landing page.
 * 
 * Supports both GET and POST requests to handle logout from
 * different parts of the application (links and forms).
 * 
 * URL Pattern: /LogoutServlet
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processes logout by invalidating the user session and
     * redirecting to the home page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Retrieve existing session without creating a new one
            HttpSession session = request.getSession(false);

            if (session != null) {
                // Invalidate session to clear all user data and attributes
                session.invalidate();
            }

            // Redirect to landing page after successful logout
            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    /**
     * Delegates POST logout requests to doGet handler.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}