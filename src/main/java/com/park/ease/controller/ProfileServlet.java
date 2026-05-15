package com.park.ease.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import com.park.ease.dao.VehicleDAO;
import com.park.ease.daoimpl.VehicleDAOImpl;
import com.park.ease.model.User;
import com.park.ease.model.Vehicle;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Model Layer dependency
    private VehicleDAO vehicleDAO = new VehicleDAOImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 1. Controller Logic: Check Authentication
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Model Logic: Fetch data for the View
        List<Vehicle> userVehicles = vehicleDAO.getVehiclesByUserId(user.getUserId());

        // 3. MVC Communication: Attach data to the Request object
        request.setAttribute("vehicles", userVehicles);

        // 4. Navigation: Forward to the View (profile.jsp)
        // Note: Using forward keeps the URL as 'ProfileServlet' but shows the JSP content
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}