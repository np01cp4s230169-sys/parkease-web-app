package com.park.ease.controller;

import java.io.IOException;

import com.park.ease.model.User;
import com.park.ease.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LoginServlet handles user authentication for the ParkEase system.
 * Validates credentials, checks account approval status, manages session
 * creation, and redirects users based on their role (ADMIN or USER).
 * 
 * Security features:
 *   - Old session invalidation before creating new session (prevents session fixation)
 *   - Role-based redirection after successful login
 *   - Account status verification (only approved users can login)
 *   - Session timeout set to 30 minutes of inactivity
 * 
 * URL Pattern: /LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependency for user authentication
    private UserService userService = new UserService();

    /**
     * Displays the login page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    /**
     * Processes login form submission.
     * Validates email and password, authenticates against database,
     * checks account approval status, and redirects based on user role.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Trim email but not password (passwords may contain spaces intentionally)
            if (email != null) {
                email = email.trim();
            }

            // Validate both fields are provided
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Email and password are required.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            // Authenticate user credentials against database
            User user = userService.authenticateUser(email, password);

            if (user != null) {
                // Check if account has been approved by admin
                if ("approved".equalsIgnoreCase(user.getStatus())) {

                    // Invalidate old session to prevent session fixation attacks
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }

                    // Create new session and store user details
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("role", user.getRole());
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes timeout

                    // Redirect based on user role
                    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/UserServlet?action=dashboard");
                    }
                } else {
                    // Account exists but not approved (pending or rejected)
                    request.setAttribute("error", "Account is " + user.getStatus() + ". Please contact Admin.");
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                }
            } else {
                // Invalid credentials - email or password does not match
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            // Log unexpected errors and forward to error page
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
