package com.park.ease.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.model.User;
import com.park.ease.model.Vehicle;
import com.park.ease.model.ParkingSession;
import com.park.ease.model.ParkingSlot;
import com.park.ease.service.BookingService;
import com.park.ease.service.VehicleService;
import com.park.ease.service.SlotService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * BookingServlet handles all parking booking operations for users.
 * Provides functionality for viewing bookings, quick booking from search,
 * confirming new bookings, and ending parking sessions (checkout).
 * 
 * URL Pattern: /BookingServlet
 * Actions (GET): myBookings, showQuickBooking
 * Actions (POST): confirmBooking, endSession
 */
@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependencies
    private BookingService bookingService = new BookingService();
    private VehicleService vehicleService = new VehicleService();
    private SlotService slotService = new SlotService();

    /**
     * Handles GET requests - routes to booking view or quick booking form.
     * Validates user session before processing any request.
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
            if ("myBookings".equals(action) && currentUser != null) {
                handleMyBookings(request, response, currentUser);
            } else if ("showQuickBooking".equals(action) && currentUser != null) {
                handleShowQuickBooking(request, response, currentUser);
            } else if ("viewReceipt".equals(action) && currentUser != null) {
                handleViewReceipt(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred while loading bookings.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests - processes booking confirmation and session checkout.
     * Validates user session before processing any action.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // Validate action parameter is provided
        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Validate user is logged in before processing POST actions
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        try {
            if ("confirmBooking".equals(action)) {
                handleConfirmBooking(request, response);
            } else if ("endSession".equals(action)) {
                handleEndSession(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your booking.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // ==================== BOOKING VIEWS ====================

    /**
     * Loads the My Bookings page with active and completed parking sessions.
     * Groups sessions by status and calculates total booking count.
     */
    private void handleMyBookings(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {

        // Retrieve all vehicles owned by the current user
        List<Vehicle> vehicleList = vehicleService.getUserVehicles(currentUser.getUserId());

        List<ParkingSession> activeSessions = new ArrayList<>();
        List<ParkingSession> completedSessions = new ArrayList<>();
        int totalBookings = 0;

        // Collect booking history for each vehicle and categorize by status
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
    }

    /**
     * Displays the quick booking form with a pre-selected slot from search results.
     * Validates slot availability and ensures user has registered vehicles.
     */
    private void handleShowQuickBooking(HttpServletRequest request, HttpServletResponse response, User currentUser)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String slotIdParam = request.getParameter("slotId");

        // Validate slotId parameter
        if (slotIdParam == null || slotIdParam.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Slot ID is required for booking.");
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=search");
            return;
        }

        try {
            int slotId = Integer.parseInt(slotIdParam.trim());

            // Retrieve and validate slot details
            ParkingSlot slot = slotService.getSlotById(slotId);

            if (slot == null) {
                session.setAttribute("errorMsg", "The selected parking slot was not found.");
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=search");
                return;
            }

            if (!"available".equalsIgnoreCase(slot.getStatus())) {
                session.setAttribute("errorMsg", "The selected slot is no longer available.");
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=search");
                return;
            }

            // Retrieve user's registered vehicles for booking dropdown
            List<Vehicle> vehicles = vehicleService.getUserVehicles(currentUser.getUserId());

            if (vehicles == null || vehicles.isEmpty()) {
                session.setAttribute("errorMsg", "Please register a vehicle before booking a slot.");
                response.sendRedirect(request.getContextPath() + "/UserServlet?action=profile");
                return;
            }

            // Set attributes for the quick booking form
            request.setAttribute("slot", slot);
            request.setAttribute("vehicles", vehicles);

            request.getRequestDispatcher("/WEB-INF/views/book_slot_quick.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid Slot ID format.");
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=search");
        }
    }

    // ==================== BOOKING ACTIONS ====================

    /**
     * Processes booking confirmation by assigning a vehicle to a parking slot.
     * Validates both vehicleId and slotId before creating the booking record.
     */
    private void handleConfirmBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String vehicleIdParam = request.getParameter("vehicleId");
        String slotIdParam = request.getParameter("slotId");

        // Validate required parameters
        if (vehicleIdParam == null || vehicleIdParam.trim().isEmpty()
                || slotIdParam == null || slotIdParam.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Vehicle and slot selection are required to confirm booking.");
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=search");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vehicleIdParam.trim());
            int slotId = Integer.parseInt(slotIdParam.trim());

            boolean success = bookingService.bookSlot(vehicleId, slotId);

            if (success) {
                session.setAttribute("successMsg", "Parking slot booked successfully! View your booking details below.");
            } else {
                session.setAttribute("errorMsg", "Booking failed. The slot may no longer be available.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid vehicle or slot selection.");
        }

        response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
    }

    /**
     * Handles parking session checkout by calculating duration and charges.
     * Uses the actual hourly rate from the parking slot for charge calculation.
     * Releases the slot back to available status after checkout.
     */
    private void handleEndSession(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String sessionIdParam = request.getParameter("sessionId");
        String slotIdParam = request.getParameter("slotId");

        // Validate required parameters
        if (sessionIdParam == null || sessionIdParam.trim().isEmpty()
                || slotIdParam == null || slotIdParam.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Session ID and Slot ID are required for checkout.");
            response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
            return;
        }

        try {
            int sessionId = Integer.parseInt(sessionIdParam.trim());
            int slotId = Integer.parseInt(slotIdParam.trim());

            // Retrieve session and slot details for charge calculation
            ParkingSession parkingSession = bookingService.getSessionDetails(sessionId);

            if (parkingSession == null) {
                session.setAttribute("errorMsg", "Parking session not found.");
                response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
                return;
            }

            // Calculate exit time and parking duration
            java.sql.Timestamp exitTime = new java.sql.Timestamp(System.currentTimeMillis());
            double hours = com.park.ease.util.DateTimeUtil.calculateHours(parkingSession.getEntryTime(), exitTime);

            // Use actual slot hourly rate instead of hardcoded value
            ParkingSlot slot = slotService.getSlotById(slotId);
            double hourlyRate = (slot != null) ? slot.getHourlyRate() : 5.0;
            double totalCharges = hours * hourlyRate;

            // Release the slot and record the charges
            if (bookingService.releaseSlot(sessionId, slotId, hours, totalCharges)) {
                session.setAttribute("successMsg", 
                    String.format("Checkout successful! Duration: %.1f hrs | Total Charge: Rs. %.2f", hours, totalCharges));
            } else {
                session.setAttribute("errorMsg", "Checkout failed. Please try again or contact admin.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid session or slot ID format.");
        } catch (NullPointerException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Session details could not be retrieved. Please contact admin.");
        }

        response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
    }

    /**
     * Loads a completed parking session receipt for display to the user.
     * Only allows users to view their own session receipts.
     */
    private void handleViewReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String sessionIdStr = request.getParameter("sessionId");
            if (sessionIdStr == null || sessionIdStr.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
                return;
            }

            int sessionId = Integer.parseInt(sessionIdStr);
            ParkingSession parkingSession = bookingService.getSessionDetails(sessionId);

            if (parkingSession == null) {
                request.getSession().setAttribute("errorMsg", "Receipt not found.");
                response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
                return;
            }

            request.setAttribute("receipt", parkingSession);
            request.getRequestDispatcher("/WEB-INF/views/receipt.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/BookingServlet?action=myBookings");
        }
    }
}
