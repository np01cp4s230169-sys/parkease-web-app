package com.park.ease.controller;

import java.io.IOException;

import com.park.ease.model.User;
import com.park.ease.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
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
 *   - Remember Me cookie stores email for 7 days for user convenience
 *
 * URL Pattern: /LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Cookie name used to store the remembered email address. */
    private static final String REMEMBER_ME_COOKIE = "rememberedEmail";

    /** Cookie expiry duration — 7 days expressed in seconds. */
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    // Service layer dependency for user authentication
    private UserService userService = new UserService();

    /**
     * Displays the login page.
     * Reads the Remember Me cookie and pre-fills the email field if present.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* Read Remember Me cookie and pass email to JSP for pre-filling */
        String rememberedEmail = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REMEMBER_ME_COOKIE.equals(cookie.getName())) {
                    rememberedEmail = cookie.getValue();
                    break;
                }
            }
        }

        request.setAttribute("rememberedEmail", rememberedEmail);
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    /**
     * Processes login form submission.
     * Validates credentials, handles Remember Me cookie, creates session,
     * and redirects based on user role.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String email    = request.getParameter("email");
            String password = request.getParameter("password");
            String rememberMe = request.getParameter("rememberMe");

            /* Trim email but not password (passwords may contain intentional spaces) */
            if (email != null) email = email.trim();

            /* Validate both fields are provided */
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Email and password are required.");
                request.setAttribute("rememberedEmail", email != null ? email : "");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            /* Authenticate user credentials against database */
            User user = userService.authenticateUser(email, password);

            if (user != null) {

                /* Check if account has been approved by admin */
                if ("approved".equalsIgnoreCase(user.getStatus())) {

                    /* Invalidate old session to prevent session fixation attacks */
                    HttpSession oldSession = request.getSession(false);
                    if (oldSession != null) oldSession.invalidate();

                    /* Create new session and store authenticated user */
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    session.setAttribute("role", user.getRole());
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes inactivity timeout

                    /* Handle Remember Me cookie based on checkbox selection */
                    if ("on".equals(rememberMe)) {
                        /* Store email in browser cookie for 7 days */
                        Cookie emailCookie = new Cookie(REMEMBER_ME_COOKIE, user.getEmail());
                        emailCookie.setMaxAge(COOKIE_MAX_AGE);
                        emailCookie.setPath("/");
                        emailCookie.setHttpOnly(true); // block JavaScript access for security
                        response.addCookie(emailCookie);
                    } else {
                        /* Remember Me not selected — delete existing cookie if present */
                        Cookie deleteCookie = new Cookie(REMEMBER_ME_COOKIE, "");
                        deleteCookie.setMaxAge(0); // age 0 instructs browser to delete
                        deleteCookie.setPath("/");
                        response.addCookie(deleteCookie);
                    }

                    /* Redirect to appropriate portal based on role */
                    if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/UserServlet?action=dashboard");
                    }

                } else {
                    /* Account exists but is pending, rejected, or suspended */
                    request.setAttribute("error", "Account is " + user.getStatus() + ". Please contact Admin.");
                    request.setAttribute("rememberedEmail", email);
                    request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                }

            } else {
                /* Credentials did not match any account */
                request.setAttribute("error", "Invalid email or password.");
                request.setAttribute("rememberedEmail", email);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred during login. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}