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
    private VehicleDAO vehicleDAO = new VehicleDAOImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 1. Fetch vehicles for this specific user
        List<Vehicle> userVehicles = vehicleDAO.getVehiclesByUserId(user.getUserId());

        // 2. Pass the list to the JSP
        request.setAttribute("vehicles", userVehicles);

        // 3. Forward the request to profile.jsp (The View)
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}