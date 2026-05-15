package com.park.ease.controller;

import java.io.IOException;
import java.util.List;

import com.park.ease.model.User;
import com.park.ease.model.Vehicle;
import com.park.ease.service.VehicleService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ProfileServlet handles displaying the user profile page.
 * Retrieves the logged-in user's vehicle list and forwards
 * to the profile view for display.
 * 
 * Note: Profile update, password change, and vehicle addition
 * are handled by UserServlet to keep responsibilities separated.
 * 
 * URL Pattern: /ProfileServlet
 */
@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependency - avoids direct DAO access from controller
    private VehicleService vehicleService = new VehicleService();

    /**
     * Displays the user profile page with their registered vehicles.
     * Validates user session before loading profile data.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;

            // Redirect to login if user session is not active
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/LoginServlet");
                return;
            }

            // Retrieve all vehicles registered by the current user
            List<Vehicle> userVehicles = vehicleService.getUserVehicles(user.getUserId());

            // Set vehicle list as request attribute for profile view
            request.setAttribute("vehicleList", userVehicles);

            // Forward to profile view inside WEB-INF (secure, not directly accessible)
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading your profile.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Delegates POST requests to doGet handler.
     * Profile is a read-only view - all updates handled by UserServlet.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}