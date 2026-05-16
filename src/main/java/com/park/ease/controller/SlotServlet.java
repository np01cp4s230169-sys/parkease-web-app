package com.park.ease.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.model.ParkingSlot;
import com.park.ease.model.User;
import com.park.ease.model.Vehicle;
import com.park.ease.model.Zone;
import com.park.ease.service.SlotService;
import com.park.ease.service.VehicleService;
import com.park.ease.service.ZoneService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * SlotServlet handles all parking slot operations for both admin and user roles.
 * 
 * Admin Operations: CRUD (Create, Read, Update, Delete) on parking slots.
 * User Operations: View available slots, search by filters, manage wishlist,
 *                  and access the booking page.
 * 
 * URL Pattern: /SlotServlet
 * Actions (GET): list, edit, delete, view, search
 * Actions (POST): addSlot, updateSlot, addToWishlist, removeFromWishlist
 */
@WebServlet("/SlotServlet")
public class SlotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependencies
    private SlotService slotService = new SlotService();
    private ZoneService zoneService = new ZoneService();
    private VehicleService vehicleService = new VehicleService();

    /**
     * Handles GET requests - routes to appropriate handler based on action parameter.
     * Admin actions: list, edit, delete
     * User actions: view, search
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("list".equals(action)) {
                // Admin: Load all slots and zones for admin dashboard
                List<ParkingSlot> slotList = slotService.getAllSlots();
                List<Zone> zoneList = zoneService.getAllZones();

                request.setAttribute("slotList", slotList);
                request.setAttribute("zoneList", zoneList);
                request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
            }
            else if ("add".equals(action)) {
                // Admin: Show the add slot form with zone list populated
                List<ParkingSlot> slotList = slotService.getAllSlots();
                List<Zone> zoneList = zoneService.getAllZones();

                request.setAttribute("slotList", slotList);
                request.setAttribute("zoneList", zoneList);
                request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
            }
            else if ("edit".equals(action)) {
                handleEditSlot(request, response);
            }
            else if ("delete".equals(action)) {
                handleDeleteSlot(request, response);
            }
            else if ("view".equals(action)) {
                handleUserSlotView(request, response);
            }
            else if ("search".equals(action)) {
                handleSearch(request, response);
            }
            else {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests - processes slot creation, updates, and wishlist actions.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("addSlot".equals(action)) {
                handleAddSlot(request, response);
            }
            else if ("updateSlot".equals(action)) {
                handleUpdateSlot(request, response);
            }
            else if ("addToWishlist".equals(action)) {
                handleAddToWishlist(request, response);
            }
            else if ("removeFromWishlist".equals(action)) {
                handleRemoveFromWishlist(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // ==================== ADMIN: CRUD OPERATIONS ====================

    /**
     * Handles adding a new parking slot to a zone.
     * Validates all required fields before creating the slot record.
     */
    private void handleAddSlot(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        try {
            String zoneIdParam = request.getParameter("zoneId");
            String slotNumber = request.getParameter("slotNumber");
            String vehicleType = request.getParameter("vehicleType");
            String rateParam = request.getParameter("hourlyRate");
            String status = request.getParameter("status");

            // Validate required fields are not empty
            if (zoneIdParam == null || zoneIdParam.trim().isEmpty()
                    || slotNumber == null || slotNumber.trim().isEmpty()
                    || vehicleType == null || vehicleType.trim().isEmpty()
                    || rateParam == null || rateParam.trim().isEmpty()) {
                session.setAttribute("errorMsg", "All fields are required to add a slot.");
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
                return;
            }

            int zoneId = Integer.parseInt(zoneIdParam.trim());
            double rate = Double.parseDouble(rateParam.trim());

            // Validate hourly rate is positive
            if (rate <= 0) {
                session.setAttribute("errorMsg", "Hourly rate must be a positive number.");
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
                return;
            }

            if (status == null || status.isBlank()) {
                status = "available";
            }

            ParkingSlot slot = new ParkingSlot();
            slot.setZoneId(zoneId);
            slot.setSlotNumber(slotNumber.trim());
            slot.setVehicleType(vehicleType.trim());
            slot.setHourlyRate(rate);
            slot.setStatus(status);

            boolean success = slotService.addParkingSlot(slot);

            if (success) {
                session.setAttribute("successMsg", "Parking slot '" + slotNumber.trim() + "' added successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to add slot. The zone may be full or slot number already exists.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid number format. Please check Zone ID and Hourly Rate.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "An unexpected error occurred while adding the slot.");
        }

        response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
    }

    /**
     * Loads a specific slot for editing in the admin dashboard.
     * Retrieves slot details, all slots, and zones for the edit form.
     */
    private void handleEditSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String slotIdParam = request.getParameter("slotId");

        // Validate slotId is provided
        if (slotIdParam == null || slotIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Slot ID is required for editing.");
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
            return;
        }

        try {
            int slotId = Integer.parseInt(slotIdParam.trim());

            ParkingSlot selectedSlot = slotService.getSlotById(slotId);
            List<ParkingSlot> slotList = slotService.getAllSlots();
            List<Zone> zoneList = zoneService.getAllZones();

            request.setAttribute("selectedSlot", selectedSlot);
            request.setAttribute("slotList", slotList);
            request.setAttribute("zoneList", zoneList);

            request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Invalid Slot ID format.");
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
        }
    }

    /**
     * Handles updating an existing parking slot record.
     * Validates all fields before performing the update operation.
     */
    private void handleUpdateSlot(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();

        try {
            String slotIdParam = request.getParameter("slotId");
            String zoneIdParam = request.getParameter("zoneId");
            String slotNumber = request.getParameter("slotNumber");
            String vehicleType = request.getParameter("vehicleType");
            String rateParam = request.getParameter("hourlyRate");
            String status = request.getParameter("status");

            // Validate required fields
            if (slotIdParam == null || slotIdParam.trim().isEmpty()
                    || zoneIdParam == null || zoneIdParam.trim().isEmpty()
                    || slotNumber == null || slotNumber.trim().isEmpty()
                    || vehicleType == null || vehicleType.trim().isEmpty()
                    || rateParam == null || rateParam.trim().isEmpty()) {
                session.setAttribute("errorMsg", "All fields are required to update a slot.");
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
                return;
            }

            int slotId = Integer.parseInt(slotIdParam.trim());
            int zoneId = Integer.parseInt(zoneIdParam.trim());
            double rate = Double.parseDouble(rateParam.trim());

            // Validate hourly rate is positive
            if (rate <= 0) {
                session.setAttribute("errorMsg", "Hourly rate must be a positive number.");
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
                return;
            }

            ParkingSlot slot = new ParkingSlot();
            slot.setSlotId(slotId);
            slot.setZoneId(zoneId);
            slot.setSlotNumber(slotNumber.trim());
            slot.setVehicleType(vehicleType.trim());
            slot.setHourlyRate(rate);
            slot.setStatus(status);

            boolean success = slotService.updateParkingSlot(slot);

            if (success) {
                session.setAttribute("successMsg", "Slot '" + slotNumber.trim() + "' updated successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to update slot. Please try again.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid number format. Please check Slot ID, Zone ID, and Hourly Rate.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "An unexpected error occurred while updating the slot.");
        }

        response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
    }

    /**
     * Handles deleting a parking slot by its ID.
     * Validates the slot ID before attempting deletion.
     */
    private void handleDeleteSlot(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        String slotIdParam = request.getParameter("slotId");

        // Validate slotId is provided
        if (slotIdParam == null || slotIdParam.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Slot ID is required for deletion.");
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
            return;
        }

        try {
            int slotId = Integer.parseInt(slotIdParam.trim());
            boolean success = slotService.deleteParkingSlot(slotId);

            if (success) {
                session.setAttribute("successMsg", "Parking slot deleted successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to delete slot. It may be currently in use.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid Slot ID format.");
        }

        response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list");
    }

    // ==================== USER: SEARCH & WISHLIST ====================

    /**
     * Handles slot search with multiple filter criteria.
     * Users can search by zone, vehicle type, and slot number.
     * Also loads the user's wishlist from session for display.
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Load all zones for the search filter dropdown
        List<Zone> zoneList = zoneService.getAllZones();
        request.setAttribute("zoneList", zoneList);

        // Retrieve search parameters
        String zoneIdParam = request.getParameter("zoneId");
        String vehicleType = request.getParameter("vehicleType");
        String slotNumber = request.getParameter("slotNumber");

        // Preserve search inputs for form re-population
        request.setAttribute("selectedZoneId", zoneIdParam);
        request.setAttribute("selectedVehicleType", vehicleType);
        request.setAttribute("enteredSlotNumber", slotNumber);

        // Parse zone ID safely
        Integer zoneId = null;
        try {
            if (zoneIdParam != null && !zoneIdParam.trim().isEmpty()) {
                zoneId = Integer.parseInt(zoneIdParam);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Execute search only if at least one filter is provided
        boolean hasSearchInput = (zoneIdParam != null && !zoneIdParam.trim().isEmpty()) ||
                                 (vehicleType != null && !vehicleType.trim().isEmpty()) ||
                                 (slotNumber != null && !slotNumber.trim().isEmpty());

        if (hasSearchInput) {
            List<ParkingSlot> searchResults = slotService.searchAvailableSlots(zoneId, vehicleType, slotNumber);
            request.setAttribute("searchResults", searchResults);
        }

        // Load wishlist slots from session for display
        HttpSession session = request.getSession(false);
        List<Integer> wishlistSlotIds = (session != null) ? (List<Integer>) session.getAttribute("wishlistSlotIds") : null;
        List<ParkingSlot> wishlistSlots = new ArrayList<>();

        if (wishlistSlotIds != null && !wishlistSlotIds.isEmpty()) {
            List<ParkingSlot> allSlots = slotService.getAllSlots();
            for (Integer wishId : wishlistSlotIds) {
                for (ParkingSlot slot : allSlots) {
                    if (slot.getSlotId() == wishId) {
                        wishlistSlots.add(slot);
                        break;
                    }
                }
            }
        }
        request.setAttribute("wishlistSlots", wishlistSlots);
        request.getRequestDispatcher("/WEB-INF/views/search.jsp").forward(request, response);
    }

    /**
     * Adds a parking slot to the user's wishlist stored in session.
     * Prevents duplicate entries in the wishlist.
     */
    private void handleAddToWishlist(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        List<Integer> wishlistSlotIds = (List<Integer>) session.getAttribute("wishlistSlotIds");
        if (wishlistSlotIds == null) wishlistSlotIds = new ArrayList<>();

        try {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            if (!wishlistSlotIds.contains(slotId)) {
                wishlistSlotIds.add(slotId);
                session.setAttribute("wishlistSlotIds", wishlistSlotIds);
                response.sendRedirect(buildSearchRedirectUrl(request, "wishlist_added"));
            } else {
                response.sendRedirect(buildSearchRedirectUrl(request, "already_in_wishlist"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(buildSearchRedirectUrl(request, "wishlist_error"));
        }
    }

    /**
     * Removes a parking slot from the user's wishlist stored in session.
     */
    private void handleRemoveFromWishlist(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            List<Integer> wishlistSlotIds = (List<Integer>) session.getAttribute("wishlistSlotIds");
            if (wishlistSlotIds != null) {
                try {
                    int slotId = Integer.parseInt(request.getParameter("slotId"));
                    wishlistSlotIds.remove(Integer.valueOf(slotId));
                    session.setAttribute("wishlistSlotIds", wishlistSlotIds);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        response.sendRedirect(buildSearchRedirectUrl(request, "wishlist_removed"));
    }

    /**
     * Builds redirect URL preserving current search parameters.
     * Ensures search filters persist after wishlist operations.
     */
    private String buildSearchRedirectUrl(HttpServletRequest request, String msg) {
        String zoneId = request.getParameter("zoneId");
        String vehicleType = request.getParameter("vehicleType");
        String slotNumber = request.getParameter("slotNumber");
        StringBuilder url = new StringBuilder(request.getContextPath() + "/SlotServlet?action=search");

        if (zoneId != null && !zoneId.trim().isEmpty())
            url.append("&zoneId=").append(URLEncoder.encode(zoneId, StandardCharsets.UTF_8));
        if (vehicleType != null && !vehicleType.trim().isEmpty())
            url.append("&vehicleType=").append(URLEncoder.encode(vehicleType, StandardCharsets.UTF_8));
        if (slotNumber != null && !slotNumber.trim().isEmpty())
            url.append("&slotNumber=").append(URLEncoder.encode(slotNumber, StandardCharsets.UTF_8));
        if (msg != null && !msg.trim().isEmpty())
            url.append("&msg=").append(URLEncoder.encode(msg, StandardCharsets.UTF_8));

        return url.toString();
    }

    // ==================== USER: SLOT VIEW & BOOKING ====================

    /**
     * Loads available slots for the user booking page.
     * Retrieves user's vehicles, all zones, and available slots
     * filtered by the selected zone.
     */
    private void handleUserSlotView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Redirect to login if user session is not found
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        // Load user's registered vehicles for booking dropdown
        List<Vehicle> vehicleList = vehicleService.getUserVehicles(currentUser.getUserId());
        List<Zone> zoneList = zoneService.getAllZones();
        List<ParkingSlot> slotList = new ArrayList<>();
        String zoneIdParam = request.getParameter("zoneId");

        // Filter slots by selected zone if provided
        if (zoneIdParam != null && !zoneIdParam.trim().isEmpty()) {
            try {
                int zoneId = Integer.parseInt(zoneIdParam);
                slotList = slotService.getAvailableSlotsByZone(zoneId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("vehicleList", vehicleList);
        request.setAttribute("zoneList", zoneList);
        request.setAttribute("slotList", slotList);
        request.getRequestDispatcher("/WEB-INF/views/book_slot.jsp").forward(request, response);
    }
}