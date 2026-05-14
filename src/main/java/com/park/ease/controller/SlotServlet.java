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

@WebServlet("/SlotServlet")
public class SlotServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SlotService slotService = new SlotService();
    private ZoneService zoneService = new ZoneService();
    private VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("list".equals(action)) {
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

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
    }

    // --- NEW CRUD HANDLERS ---

    private void handleAddSlot(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int zoneId = Integer.parseInt(request.getParameter("zoneId"));
            String slotNumber = request.getParameter("slotNumber");
            String vehicleType = request.getParameter("vehicleType");
            double rate = Double.parseDouble(request.getParameter("hourlyRate"));
            String status = request.getParameter("status");

            if (status == null || status.isBlank()) {
                status = "available";
            }

            ParkingSlot slot = new ParkingSlot();
            slot.setZoneId(zoneId);
            slot.setSlotNumber(slotNumber);
            slot.setVehicleType(vehicleType);
            slot.setHourlyRate(rate);
            slot.setStatus(status);

            boolean success = slotService.addParkingSlot(slot);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&msg=slot_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=zone_full_or_error");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=invalid_input");
        }
    }
    private void handleEditSlot(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int slotId = Integer.parseInt(request.getParameter("slotId"));

            ParkingSlot selectedSlot = slotService.getSlotById(slotId);
            List<ParkingSlot> slotList = slotService.getAllSlots();
            List<Zone> zoneList = zoneService.getAllZones();

            request.setAttribute("selectedSlot", selectedSlot);
            request.setAttribute("slotList", slotList);
            request.setAttribute("zoneList", zoneList);

            request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=invalid_slot");
        }
    }
    private void handleUpdateSlot(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            int zoneId = Integer.parseInt(request.getParameter("zoneId"));
            String slotNumber = request.getParameter("slotNumber");
            String vehicleType = request.getParameter("vehicleType");
            double rate = Double.parseDouble(request.getParameter("hourlyRate"));
            String status = request.getParameter("status");

            ParkingSlot slot = new ParkingSlot();
            slot.setSlotId(slotId);
            slot.setZoneId(zoneId);
            slot.setSlotNumber(slotNumber);
            slot.setVehicleType(vehicleType);
            slot.setHourlyRate(rate);
            slot.setStatus(status);

            boolean success = slotService.updateParkingSlot(slot);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&msg=slot_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=update_failed");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=invalid_input");
        }
    }

    private void handleDeleteSlot(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int slotId = Integer.parseInt(request.getParameter("slotId"));
            boolean success = slotService.deleteParkingSlot(slotId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&msg=slot_deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=delete_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/SlotServlet?action=list&error=invalid_slot");
        }
    }
    // --- EXISTING UTILITY METHODS ---

    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Zone> zoneList = zoneService.getAllZones();
        request.setAttribute("zoneList", zoneList);

        String zoneIdParam = request.getParameter("zoneId");
        String vehicleType = request.getParameter("vehicleType");
        String slotNumber = request.getParameter("slotNumber");

        request.setAttribute("selectedZoneId", zoneIdParam);
        request.setAttribute("selectedVehicleType", vehicleType);
        request.setAttribute("enteredSlotNumber", slotNumber);

        Integer zoneId = null;
        try {
            if (zoneIdParam != null && !zoneIdParam.trim().isEmpty()) {
                zoneId = Integer.parseInt(zoneIdParam);
            }
        } catch (NumberFormatException e) { zoneId = null; }

        boolean hasSearchInput = (zoneIdParam != null && !zoneIdParam.trim().isEmpty()) ||
                                 (vehicleType != null && !vehicleType.trim().isEmpty()) ||
                                 (slotNumber != null && !slotNumber.trim().isEmpty());

        if (hasSearchInput) {
            List<ParkingSlot> searchResults = slotService.searchAvailableSlots(zoneId, vehicleType, slotNumber);
            request.setAttribute("searchResults", searchResults);
        }

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

    private void handleAddToWishlist(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
        } catch (Exception e) {
            response.sendRedirect(buildSearchRedirectUrl(request, "wishlist_error"));
        }
    }

    private void handleRemoveFromWishlist(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            List<Integer> wishlistSlotIds = (List<Integer>) session.getAttribute("wishlistSlotIds");
            if (wishlistSlotIds != null) {
                try {
                    int slotId = Integer.parseInt(request.getParameter("slotId"));
                    wishlistSlotIds.remove(Integer.valueOf(slotId));
                    session.setAttribute("wishlistSlotIds", wishlistSlotIds);
                } catch (Exception e) {}
            }
        }
        response.sendRedirect(buildSearchRedirectUrl(request, "wishlist_removed"));
    }

    private String buildSearchRedirectUrl(HttpServletRequest request, String msg) {
        String zoneId = request.getParameter("zoneId");
        String vehicleType = request.getParameter("vehicleType");
        String slotNumber = request.getParameter("slotNumber");
        StringBuilder url = new StringBuilder(request.getContextPath() + "/SlotServlet?action=search");

        if (zoneId != null && !zoneId.trim().isEmpty()) url.append("&zoneId=").append(URLEncoder.encode(zoneId, StandardCharsets.UTF_8));
        if (vehicleType != null && !vehicleType.trim().isEmpty()) url.append("&vehicleType=").append(URLEncoder.encode(vehicleType, StandardCharsets.UTF_8));
        if (slotNumber != null && !slotNumber.trim().isEmpty()) url.append("&slotNumber=").append(URLEncoder.encode(slotNumber, StandardCharsets.UTF_8));
        if (msg != null && !msg.trim().isEmpty()) url.append("&msg=").append(URLEncoder.encode(msg, StandardCharsets.UTF_8));
        
        return url.toString();
    }

    private void handleUserSlotView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        List<Vehicle> vehicleList = vehicleService.getUserVehicles(currentUser.getUserId());
        List<Zone> zoneList = zoneService.getAllZones();
        List<ParkingSlot> slotList = new ArrayList<>();
        String zoneIdParam = request.getParameter("zoneId");

        if (zoneIdParam != null && !zoneIdParam.trim().isEmpty()) {
            try {
                int zoneId = Integer.parseInt(zoneIdParam);
                slotList = slotService.getAvailableSlotsByZone(zoneId);
            } catch (NumberFormatException e) { }
        }

        request.setAttribute("vehicleList", vehicleList);
        request.setAttribute("zoneList", zoneList);
        request.setAttribute("slotList", slotList);
        request.getRequestDispatcher("/WEB-INF/views/book_slot.jsp").forward(request, response);
    }
}