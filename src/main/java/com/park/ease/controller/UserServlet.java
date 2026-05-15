package com.park.ease.controller;

import java.io.IOException;
import java.util.List;

import com.park.ease.model.User;
import com.park.ease.model.Vehicle;
import com.park.ease.service.UserService;
import com.park.ease.service.VehicleService;
import com.park.ease.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * UserServlet handles all user-related operations in the ParkEase system.
 * Manages user registration, profile management, vehicle registration,
 * password changes, and user dashboard access.
 * 
 * Includes duplicate account checks using email and phone number
 * to ensure unique user registration as per system requirements.
 * 
 * URL Pattern: /UserServlet
 * Actions (GET): register, dashboard, profile
 * Actions (POST): doRegister, addVehicle, changePassword, updateProfile
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependencies
    private UserService userService = new UserService();
    private VehicleService vehicleService = new VehicleService();

    /**
     * Handles GET requests - routes to registration, dashboard, or profile pages.
     * Validates user session before accessing protected pages.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User currentUser = null;

        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }

        try {
            if ("register".equals(action)) {
                // Display the registration form - publicly accessible
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            }
            else if ("dashboard".equals(action)) {
                if (currentUser != null) {
                    request.getRequestDispatcher("/WEB-INF/views/user_dashboard.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/LoginServlet");
                }
            }
            else if ("profile".equals(action)) {
                if (currentUser != null) {
                    // Load user's registered vehicles for profile page display
                    List<Vehicle> vehicleList = vehicleService.getUserVehicles(currentUser.getUserId());
                    request.setAttribute("vehicleList", vehicleList);
                    request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/LoginServlet");
                }
            }
            else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests - routes to appropriate handler based on action.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("doRegister".equals(action)) {
                handleRegistration(request, response);
            }
            else if ("addVehicle".equals(action)) {
                handleAddVehicle(request, response);
            }
            else if ("changePassword".equals(action)) {
                handleChangePassword(request, response);
            }
            else if ("updateProfile".equals(action)) {
                handleUpdateProfile(request, response);
            }
            else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // ==================== REGISTRATION ====================

    /**
     * Handles new user registration with full validation and duplicate checks.
     * Validates name format (no numbers), email format, and uniqueness
     * of both email and phone number before creating the account.
     * New accounts are set to pending status awaiting admin approval.
     */
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        // Trim inputs to handle accidental whitespace
        if (name != null) name = name.trim();
        if (email != null) email = email.trim();
        if (phone != null) phone = phone.trim();

        // 1. Validate all required fields are provided
        if (!ValidationUtil.isNotEmpty(name, email, phone, password)) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 2. Validate name contains only letters and spaces (no numbers allowed)
        if (!name.matches("[a-zA-Z ]+")) {
            request.setAttribute("error", "Full name must contain letters only. Numbers are not allowed.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 3. Validate email format is correct
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("error", "Invalid email format. Please enter a valid email address.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 4. Validate phone contains only digits and is proper length
        if (!phone.matches("\\d{10,15}")) {
            request.setAttribute("error", "Phone number must contain 10 to 15 digits only.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 5. Duplicate check: Email must not already be registered
        if (userService.isEmailExists(email)) {
            request.setAttribute("error", "This email address is already registered. Please use a different email.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 6. Duplicate check: Phone number must not already be registered
        if (userService.isPhoneExists(phone)) {
            request.setAttribute("error", "This phone number is already registered. Please use a different number.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // Build new user object with pending status for admin approval
        User newUser = new User();
        newUser.setName(name);
        newUser.setPhone(phone);
        newUser.setEmail(email);
        newUser.setRole("USER");
        newUser.setStatus("pending");

        // Register the user and redirect to login with success message
        if (userService.registerUser(newUser, password)) {
            request.setAttribute("success", "Registration successful! Your account is pending admin approval.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    // ==================== VEHICLE MANAGEMENT ====================

    /**
     * Handles adding a new vehicle to the user's account.
     * Validates all required vehicle fields before saving.
     */
    private void handleAddVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Redirect to login if session is not active
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String regNo = request.getParameter("regNo");
        String type = request.getParameter("type");
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String color = request.getParameter("color");

        // Validate required vehicle fields
        if (regNo == null || regNo.trim().isEmpty()
                || type == null || type.trim().isEmpty()
                || make == null || make.trim().isEmpty()
                || model == null || model.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Registration number, type, make, and model are required.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Build vehicle object with user association
        Vehicle v = new Vehicle();
        v.setUserId(user.getUserId());
        v.setRegistrationNumber(regNo.trim());
        v.setVehicleType(type.trim());
        v.setMake(make.trim());
        v.setModel(model.trim());
        v.setColor(color != null ? color.trim() : "");

        if (vehicleService.addVehicle(v)) {
            session.setAttribute("successMsg", "Vehicle '" + regNo.trim() + "' added successfully.");
        } else {
            session.setAttribute("errorMsg", "Failed to add vehicle. Registration number may already exist.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }

    // ==================== PASSWORD MANAGEMENT ====================

    /**
     * Handles user password change request.
     * Validates current password, new password, and confirmation match
     * before updating the password in the database.
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Redirect to login if session is not active
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate all password fields are provided
        if (!ValidationUtil.isNotEmpty(currentPassword, newPassword, confirmPassword)) {
            session.setAttribute("errorMsg", "All password fields are required.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Validate new password and confirmation match
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("errorMsg", "New password and confirm password do not match.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Validate new password is different from current
        if (newPassword.equals(currentPassword)) {
            session.setAttribute("errorMsg", "New password must be different from your current password.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (userService.changePassword(user.getUserId(), currentPassword, newPassword)) {
            session.setAttribute("successMsg", "Password changed successfully.");
        } else {
            session.setAttribute("errorMsg", "Current password is incorrect. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }

    // ==================== PROFILE MANAGEMENT ====================

    /**
     * Handles updating user profile information.
     * Validates name, email format, and uniqueness of email and phone
     * for other users before applying the update.
     * Refreshes the session with updated user data after successful update.
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Redirect to login if session is not active
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        // Trim inputs to handle accidental whitespace
        if (name != null) name = name.trim();
        if (email != null) email = email.trim();
        if (phone != null) phone = phone.trim();

        // Validate all required fields are provided
        if (!ValidationUtil.isNotEmpty(name, email, phone)) {
            session.setAttribute("errorMsg", "Name, email, and phone are required.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Validate name contains only letters and spaces
        if (!name.matches("[a-zA-Z ]+")) {
            session.setAttribute("errorMsg", "Full name must contain letters only. Numbers are not allowed.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            session.setAttribute("errorMsg", "Invalid email format. Please enter a valid email address.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Check email is not already used by another account
        if (userService.isEmailExistsForOtherUser(email, currentUser.getUserId())) {
            session.setAttribute("errorMsg", "This email address is already used by another account.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        // Check phone is not already used by another account
        if (userService.isPhoneExistsForOtherUser(phone, currentUser.getUserId())) {
            session.setAttribute("errorMsg", "This phone number is already used by another account.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        User updatedUser = new User();
        updatedUser.setUserId(currentUser.getUserId());
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);

        if (userService.updateProfile(updatedUser)) {
            // Refresh session with updated user data
            User freshUser = userService.getUserById(currentUser.getUserId());
            session.setAttribute("user", freshUser);
            session.setAttribute("role", freshUser.getRole());
            session.setAttribute("successMsg", "Profile updated successfully.");
        } else {
            session.setAttribute("errorMsg", "Failed to update profile. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }
}