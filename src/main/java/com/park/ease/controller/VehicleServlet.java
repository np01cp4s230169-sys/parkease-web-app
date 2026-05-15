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
    
    // Controller depends on the DAO (Model Layer interface)
    private VehicleDAO vehicleDAO;

    @Override
    public void init() throws ServletException {
        // Initialize the DAO implementation
        vehicleDAO = new VehicleDAOImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // MVC: Controller redirects to the View (Profile page)
        response.sendRedirect("profile.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. MVC Controller: Get data from Session (The Context)
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. MVC Controller: Capture data from View (The Form)
        String regNum = request.getParameter("registrationNumber");
        String type = request.getParameter("vehicleType");
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        String color = request.getParameter("color");

        // 3. MVC Model: Populate the Data Model
        Vehicle vehicle = new Vehicle();
        vehicle.setUserId(user.getUserId());
        vehicle.setRegistrationNumber(regNum);
        vehicle.setVehicleType(type);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setColor(color);

        // 4. MVC Controller: Ask the Model Layer to save the data
        boolean isSaved = vehicleDAO.addVehicle(vehicle);

        // 5. MVC View Navigation: Direct the user based on the outcome
        if (isSaved) {
            // Success: Send them back to profile with a success flag
            response.sendRedirect("profile.jsp?status=success");
        } else {
            // Failure: Send them back with a fail flag
            response.sendRedirect("profile.jsp?status=failed");
        }
    }
}