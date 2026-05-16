package com.park.ease.controller;

import java.io.IOException;

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
 * VehicleServlet handles vehicle registration for logged-in users.
 * Allows users to add new vehicles to their account which are then
 * used when booking parking slots.
 * 
 * Note: Vehicle listing and deletion are handled through UserServlet
 * and the profile page to keep responsibilities separated.
 * 
 * URL Pattern: /VehicleServlet
 */
@WebServlet("/VehicleServlet")
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependency - controller must not access DAO directly
    private VehicleService vehicleService = new VehicleService();

    /**
     * Handles GET requests - redirects to user profile page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }

    /**
     * Handles POST requests - processes new vehicle registration.
     * Validates all required vehicle fields and user session
     * before saving the vehicle record to the database.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Validate user session is active before processing
            HttpSession session = request.getSession(false);
            User user = (session != null) ? (User) session.getAttribute("user") : null;

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/LoginServlet");
                return;
            }

            // Retrieve and trim vehicle form parameters
            String regNum = request.getParameter("registrationNumber");
            String type = request.getParameter("vehicleType");
            String make = request.getParameter("make");
            String model = request.getParameter("model");
            String color = request.getParameter("color");

            if (regNum != null) regNum = regNum.trim();
            if (type != null) type = type.trim();
            if (make != null) make = make.trim();
            if (model != null) model = model.trim();
            if (color != null) color = color.trim();

            // Validate required fields are not empty
            if (regNum == null || regNum.isEmpty()
                    || type == null || type.isEmpty()
                    || make == null || make.isEmpty()
                    || model == null || model.isEmpty()) {
                session.setAttribute("errorMsg", "Registration number, type, make, and model are required.");
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
                return;
            }

            // Build vehicle object associated with the current user
            Vehicle vehicle = new Vehicle();
            vehicle.setUserId(user.getUserId());
            vehicle.setRegistrationNumber(regNum);
            vehicle.setVehicleType(type);
            vehicle.setMake(make);
            vehicle.setModel(model);
            vehicle.setColor(color != null ? color : "");

            // Save vehicle through service layer
            boolean isSaved = vehicleService.addVehicle(vehicle);

            if (isSaved) {
                session.setAttribute("successMsg", "Vehicle '" + regNum + "' registered successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to register vehicle. Registration number may already exist.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "An unexpected error occurred while registering vehicle.");
        }

        response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
    }
}