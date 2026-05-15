package com.park.ease.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import com.park.ease.dao.VehicleDAO;
import com.park.ease.daoimpl.VehicleDAOImpl;
import com.park.ease.model.User;
import com.park.ease.model.Vehicle;

@WebServlet("/VehicleServlet")
public class VehicleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private VehicleDAO vehicleDAO = new VehicleDAOImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Typically redirects to the profile or garage page
        response.sendRedirect("profile.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Get the current session and logged-in user
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 2. Safety check: If no user is in session, go to login
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // 3. Extract form data (Match these names to your JSP input 'name' tags)
            String regNum = request.getParameter("registrationNumber");
            String type = request.getParameter("vehicleType");
            String make = request.getParameter("make");
            String model = request.getParameter("model");
            String color = request.getParameter("color");

            // 4. Populate the Vehicle model
            Vehicle vehicle = new Vehicle();
            vehicle.setUserId(user.getUserId()); // Critical for Foreign Key
            vehicle.setRegistrationNumber(regNum);
            vehicle.setVehicleType(type);
            vehicle.setMake(make);
            vehicle.setModel(model);
            vehicle.setColor(color);

            // 5. Save to database using your DAO
            boolean success = vehicleDAO.addVehicle(vehicle);

            // 6. Redirect back with a status message
            if (success) {
                response.sendRedirect("profile.jsp?status=success");
            } else {
                response.sendRedirect("profile.jsp?status=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profile.jsp?status=error");
        }
    }
}