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

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    private VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User currentUser = null;

        if (session != null) {
            currentUser = (User) session.getAttribute("user");
        }

        if ("register".equals(action)) {
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

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
    }

    /**
     * UPDATED: Added duplicate checks and input trimming
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

        // 1. Validation: Empty fields check
        if (!ValidationUtil.isNotEmpty(name, email, phone, password)) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 2. Validation: Email format check
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("error", "Invalid email format.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 3. Duplicate Check: Email existence
        if (userService.isEmailExists(email)) {
            request.setAttribute("error", "Email already registered.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // 4. Duplicate Check: Phone number existence
        if (userService.isPhoneExists(phone)) {
            request.setAttribute("error", "Phone number already registered.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setPhone(phone);
        newUser.setEmail(email);
        newUser.setRole("USER");
        newUser.setStatus("pending");

        // 5. Final Step: Register the user
        if (userService.registerUser(newUser, password)) {
            request.setAttribute("success", "Success! Wait for Admin approval.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    private void handleAddVehicle(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ... (remaining methods stay the same)
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user != null) {
            Vehicle v = new Vehicle();
            v.setUserId(user.getUserId());
            v.setRegistrationNumber(request.getParameter("regNo"));
            v.setVehicleType(request.getParameter("type"));
            v.setMake(request.getParameter("make"));
            v.setModel(request.getParameter("model"));
            v.setColor(request.getParameter("color"));

            if (vehicleService.addVehicle(v)) {
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&msg=vehicle_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=failed");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!ValidationUtil.isNotEmpty(currentPassword, newPassword, confirmPassword)) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=empty_password_fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=password_mismatch");
            return;
        }

        if (userService.changePassword(user.getUserId(), currentPassword, newPassword)) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&msg=password_changed");
        } else {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=invalid_current_password");
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (name != null) name = name.trim();
        if (email != null) email = email.trim();
        if (phone != null) phone = phone.trim();

        if (!ValidationUtil.isNotEmpty(name, email, phone)) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=empty_profile_fields");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=invalid_email");
            return;
        }

        if (userService.isEmailExistsForOtherUser(email, currentUser.getUserId())) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=email_exists");
            return;
        }

        if (userService.isPhoneExistsForOtherUser(phone, currentUser.getUserId())) {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=phone_exists");
            return;
        }

        User updatedUser = new User();
        updatedUser.setUserId(currentUser.getUserId());
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);

        if (userService.updateProfile(updatedUser)) {
            User freshUser = userService.getUserById(currentUser.getUserId());
            session.setAttribute("user", freshUser);
            session.setAttribute("role", freshUser.getRole());
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&msg=profile_updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile&error=profile_update_failed");
        }
    }
}