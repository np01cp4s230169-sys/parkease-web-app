package com.park.ease.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.model.User;
import com.park.ease.model.Vehicle;
import com.park.ease.model.ParkingSession;
import com.park.ease.service.BookingService;
import com.park.ease.service.VehicleService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private BookingService bookingService = new BookingService();
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

        if ("myBookings".equals(action) && currentUser != null) {

            List<Vehicle> vehicleList = vehicleService.getUserVehicles(currentUser.getUserId());

            List<ParkingSession> activeSessions = new ArrayList<>();
            List<ParkingSession> completedSessions = new ArrayList<>();
            int totalBookings = 0;

            for (Vehicle vehicle : vehicleList) {
                List<ParkingSession> sessions = bookingService.getUserBookingHistory(vehicle.getVehicleId());

                for (ParkingSession s : sessions) {
                    totalBookings++;

                    if ("active".equalsIgnoreCase(s.getStatus())) {
                        activeSessions.add(s);
                    } else if ("completed".equalsIgnoreCase(s.getStatus())) {
                        completedSessions.add(s);
                    }
                }
            }

            request.setAttribute("activeSessions", activeSessions);
            request.setAttribute("completedSessions", completedSessions);
            request.setAttribute("totalBookings", totalBookings);

            request.getRequestDispatcher("/WEB-INF/views/my_bookings.jsp").forward(request, response);

        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if ("confirmBooking".equals(action)) {
            handleConfirmBooking(request, response);
        } else if ("endSession".equals(action)) {
            handleEndSession(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    private void handleEndSession(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            int slotId = Integer.parseInt(request.getParameter("slotId"));

            ParkingSession session = bookingService.getSessionDetails(sessionId);

            java.sql.Timestamp exitTime = new java.sql.Timestamp(System.currentTimeMillis());

            double hours = com.park.ease.util.DateTimeUtil.calculateHours(session.getEntryTime(), exitTime);
            double totalCharges = hours * 5.0;

            if (bookingService.releaseSlot(sessionId, slotId, hours, totalCharges)) {
                response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings&msg=checked_out&charge=" + totalCharges);
            } else {
                response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings&error=checkout_failed");
            }
        } catch (NumberFormatException | NullPointerException e) {
            response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings&error=invalid_session");
        }
    }

    private void handleConfirmBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            int slotId = Integer.parseInt(request.getParameter("slotId"));

            boolean success = bookingService.bookSlot(vehicleId, slotId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=dashboard&msg=booking_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=view&error=booking_failed");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=view&error=invalid_selection");
        }
    }
}