package com.park.ease.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.park.ease.model.User;
import com.park.ease.model.Vehicle;
import com.park.ease.service.BookingService;
import com.park.ease.service.SlotService;
import com.park.ease.service.UserService;
import com.park.ease.service.VehicleService;
import com.park.ease.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * UserServlet handles all user-related operations in the ParkEase system.
 * Manages user registration (with optional profile image upload), profile
 * management, vehicle registration, password changes, and dashboard access.
 *
 * The @MultipartConfig annotation enables file upload support for profile
 * image upload during registration. Images are converted to Base64 strings
 * and stored directly in the database, requiring no server-side file storage.
 *
 * URL Pattern: /UserServlet
 * Actions (GET):  register, dashboard, profile
 * Actions (POST): doRegister, addVehicle, changePassword, updateProfile
 */
@WebServlet("/UserServlet")
@MultipartConfig(
    maxFileSize    = 2 * 1024 * 1024,  // max single file size: 2 MB
    maxRequestSize = 5 * 1024 * 1024   // max total request size: 5 MB
)
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependencies
    private UserService userService = new UserService();
    private VehicleService vehicleService = new VehicleService();
    private SlotService slotService = new SlotService();
    private BookingService bookingService = new BookingService();

    /**
     * Handles GET requests — routes to registration, dashboard, or profile pages.
     * Validates user session before accessing protected pages.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        try {
            if ("register".equals(action) || action == null) {
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);

            } else if ("dashboard".equals(action)) {
                if (currentUser != null) {
                    // Load real dashboard statistics for the user
                    Map<String, Integer> slotStats = slotService.getDashboardStats();
                    int availableSlots = slotStats.getOrDefault("availableSlots", 0);

                    List<Vehicle> userVehicles = vehicleService.getUserVehicles(currentUser.getUserId());
                    int activeBookings = 0;
                    double totalSpent = 0.0;
                    for (Vehicle v : userVehicles) {
                        List<com.park.ease.model.ParkingSession> sessions =
                            bookingService.getUserBookingHistory(v.getVehicleId());
                        for (com.park.ease.model.ParkingSession s : sessions) {
                            if ("active".equalsIgnoreCase(s.getStatus())) activeBookings++;
                            if ("completed".equalsIgnoreCase(s.getStatus())) totalSpent += s.getTotalCharges();
                        }
                    }

                    request.setAttribute("activeBookings", activeBookings);
                    request.setAttribute("availableSlots", availableSlots);
                    request.setAttribute("totalSpent", String.format("%.2f", totalSpent));
                    request.getRequestDispatcher("/WEB-INF/views/user_dashboard.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/LoginServlet");
                }

            } else if ("profile".equals(action)) {
                if (currentUser != null) {
                    List<Vehicle> vehicleList = vehicleService.getUserVehicles(currentUser.getUserId());
                    request.setAttribute("vehicleList", vehicleList);
                    request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/LoginServlet");
                }

            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests — routes to the correct action handler.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            if ("doRegister".equals(action)) {
                handleRegistration(request, response);

            } else if ("addVehicle".equals(action)) {
                handleAddVehicle(request, response);

            } else if ("changePassword".equals(action)) {
                handleChangePassword(request, response);

            } else if ("updateProfile".equals(action)) {
                handleUpdateProfile(request, response);

            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // ==================== REGISTRATION ====================

    /**
     * Handles new user registration.
     * Validates all input fields, checks for duplicate email and phone,
     * processes optional profile image upload by converting to Base64,
     * and creates the account with pending status for admin approval.
     */
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name     = request.getParameter("name");
        String email    = request.getParameter("email");
        String phone    = request.getParameter("phone");
        String password = request.getParameter("password");

        // Trim inputs to handle accidental whitespace
        if (name != null)  name  = name.trim();
        if (email != null) email = email.trim();
        if (phone != null) phone = phone.trim();

        // 1. Validate all required fields are provided
        if (!ValidationUtil.isNotEmpty(name, email, phone, password)) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 2. Name must contain letters and spaces only
        if (!name.matches("[a-zA-Z ]+")) {
            request.setAttribute("error", "Full name must contain letters only. Numbers are not allowed.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 3. Validate email format
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("error", "Invalid email format. Please enter a valid email address.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 4. Validate phone contains only digits and is correct length
        if (!phone.matches("\\d{10,15}")) {
            request.setAttribute("error", "Phone number must contain 10 to 15 digits only.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 5. Check email is not already registered
        if (userService.isEmailExists(email)) {
            request.setAttribute("error", "This email address is already registered. Please use a different email.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 6. Check phone is not already registered
        if (userService.isPhoneExists(phone)) {
            request.setAttribute("error", "This phone number is already registered. Please use a different number.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 7. Process optional profile image upload
        String base64Image = null;
        try {
            Part profilePicPart = request.getPart("profilePic");
            if (profilePicPart != null && profilePicPart.getSize() > 0) {
                String contentType = profilePicPart.getContentType();
                // Only accept image files
                if (contentType != null && contentType.startsWith("image/")) {
                    InputStream inputStream = profilePicPart.getInputStream();
                    byte[] imageBytes = inputStream.readAllBytes();
                    base64Image = Base64.getEncoder().encodeToString(imageBytes);
                } else {
                    request.setAttribute("error", "Profile image must be a valid image file (jpg, png, gif).");
                    request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            // If file part is missing or empty, continue without profile pic
            e.printStackTrace();
        }

        // Build new user object with pending status for admin approval
        User newUser = new User();
        newUser.setName(name);
        newUser.setPhone(phone);
        newUser.setEmail(email);
        newUser.setRole("USER");
        newUser.setStatus("pending");
        newUser.setProfilePic(base64Image); // null if no image uploaded

        // Register the user and redirect to login with success message
        if (userService.registerUser(newUser, password)) {
            request.setAttribute("success", "Registration successful! Your account is pending admin approval.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    // ==================== VEHICLE MANAGEMENT ====================

    /**
     * Handles adding a new vehicle to the user's account.
     * Validates registration number uniqueness before saving.
     */
    private void handleAddVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String regNo = request.getParameter("regNo");
        String type  = request.getParameter("type");
        String make  = request.getParameter("make");
        String model = request.getParameter("model");
        String color = request.getParameter("color");

        if (regNo != null) regNo = regNo.trim().toUpperCase();

        if (regNo == null || regNo.isEmpty() || type == null || type.isEmpty()) {
            session.setAttribute("errorMsg", "Registration number and vehicle type are required.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setUserId(currentUser.getUserId());
        vehicle.setRegistrationNumber(regNo);
        vehicle.setVehicleType(type);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setColor(color);

        if (vehicleService.addVehicle(vehicle)) {
            session.setAttribute("successMsg", "Vehicle '" + regNo + "' added successfully.");
        } else {
            session.setAttribute("errorMsg", "Failed to add vehicle. Registration number may already exist.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }

    // ==================== PASSWORD CHANGE ====================

    /**
     * Handles password change for logged-in users.
     * Verifies current password before applying the new one.
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String currentPassword  = request.getParameter("currentPassword");
        String newPassword      = request.getParameter("newPassword");
        String confirmPassword  = request.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null || confirmPassword == null
                || currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            session.setAttribute("errorMsg", "All password fields are required.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("errorMsg", "New password and confirm password do not match.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (userService.changePassword(currentUser.getUserId(), currentPassword, newPassword)) {
            session.setAttribute("successMsg", "Password changed successfully.");
        } else {
            session.setAttribute("errorMsg", "Current password is incorrect.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }

    // ==================== PROFILE UPDATE ====================

    /**
     * Handles profile information updates (name, email, phone).
     * Checks for conflicts with other accounts before saving.
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String name  = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (name != null)  name  = name.trim();
        if (email != null) email = email.trim();
        if (phone != null) phone = phone.trim();

        if (name == null || email == null || phone == null
                || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            session.setAttribute("errorMsg", "Name, email, and phone are required.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            session.setAttribute("errorMsg", "Name must contain letters only.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            session.setAttribute("errorMsg", "Invalid email format.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (userService.isEmailExistsForOtherUser(email, currentUser.getUserId())) {
            session.setAttribute("errorMsg", "Email is already used by another account.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        if (userService.isPhoneExistsForOtherUser(phone, currentUser.getUserId())) {
            session.setAttribute("errorMsg", "Phone number is already used by another account.");
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
            return;
        }

        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);

        if (userService.updateProfile(currentUser)) {
            session.setAttribute("user", currentUser); // update session with new details
            session.setAttribute("successMsg", "Profile updated successfully.");
        } else {
            session.setAttribute("errorMsg", "Profile update failed. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }
}