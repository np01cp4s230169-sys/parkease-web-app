package com.park.ease.controller;

import java.io.IOException;
import java.util.List;

import com.park.ease.model.Zone;
import com.park.ease.service.ZoneService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ZoneServlet handles all parking zone management operations for admin users.
 * Provides full CRUD functionality: Create, Read, Update, and Delete zones.
 * 
 * Each zone represents a designated parking area with a name, capacity,
 * and description. Zones contain multiple parking slots.
 * 
 * URL Pattern: /ZoneServlet
 * Actions (GET): list, add, edit, delete
 * Actions (POST): create, update
 */
@WebServlet("/ZoneServlet")
public class ZoneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependency for zone business logic
    private ZoneService zoneService = new ZoneService();

    /**
     * Handles GET requests - routes to appropriate handler based on action parameter.
     * list/add: Displays all zones on admin dashboard
     * edit: Loads a specific zone for editing
     * delete: Removes a zone by ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("list".equals(action) || "add".equals(action)) {
                // Load all zones for display on admin dashboard
                List<Zone> zoneList = zoneService.getAllZones();
                request.setAttribute("zoneList", zoneList);
                request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);

            } else if ("edit".equals(action)) {
                handleEditZone(request, response);

            } else if ("delete".equals(action)) {
                handleDeleteZone(request, response);

            } else {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests - processes zone creation and update operations.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                handleCreateZone(request, response);
            } else if ("update".equals(action)) {
                handleUpdateZone(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    // ==================== CRUD OPERATIONS ====================

    /**
     * Handles creating a new parking zone.
     * Validates zone name, capacity, and description before saving.
     */
    private void handleCreateZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            String name = request.getParameter("zoneName");
            String capacityParam = request.getParameter("capacity");
            String description = request.getParameter("description");

            // Validate required fields are not empty
            if (name == null || name.trim().isEmpty()
                    || capacityParam == null || capacityParam.trim().isEmpty()) {
                session.setAttribute("errorMsg", "Zone name and capacity are required.");
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
                return;
            }

            int capacity = Integer.parseInt(capacityParam.trim());

            // Validate capacity is positive
            if (capacity <= 0) {
                session.setAttribute("errorMsg", "Capacity must be a positive number.");
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
                return;
            }

            Zone newZone = new Zone();
            newZone.setZoneName(name.trim());
            newZone.setCapacity(capacity);
            newZone.setDescription(description != null ? description.trim() : "");

            boolean success = zoneService.createZone(newZone);

            if (success) {
                session.setAttribute("successMsg", "Zone '" + name.trim() + "' created successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to create zone. Zone name may already exist.");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid capacity format. Please enter a valid number.");
        }

        response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
    }

    /**
     * Loads a specific zone for editing in the admin dashboard.
     * Retrieves the selected zone and all zones for the edit form.
     */
    private void handleEditZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String zoneIdParam = request.getParameter("zoneId");

        // Validate zoneId is provided
        if (zoneIdParam == null || zoneIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Zone ID is required for editing.");
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
            return;
        }

        try {
            int zoneId = Integer.parseInt(zoneIdParam.trim());

            Zone selectedZone = zoneService.getZoneById(zoneId);
            List<Zone> zoneList = zoneService.getAllZones();

            request.setAttribute("selectedZone", selectedZone);
            request.setAttribute("zoneList", zoneList);

            request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Invalid Zone ID format.");
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
        }
    }

    /**
     * Handles updating an existing zone record.
     * Validates all fields before performing the update operation.
     */
    private void handleUpdateZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        try {
            String zoneIdParam = request.getParameter("zoneId");
            String name = request.getParameter("zoneName");
            String capacityParam = request.getParameter("capacity");
            String description = request.getParameter("description");

            // Validate required fields
            if (zoneIdParam == null || zoneIdParam.trim().isEmpty()
                    || name == null || name.trim().isEmpty()
                    || capacityParam == null || capacityParam.trim().isEmpty()) {
                session.setAttribute("errorMsg", "Zone ID, name, and capacity are required.");
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
                return;
            }

            int zoneId = Integer.parseInt(zoneIdParam.trim());
            int capacity = Integer.parseInt(capacityParam.trim());

            // Validate capacity is positive
            if (capacity <= 0) {
                session.setAttribute("errorMsg", "Capacity must be a positive number.");
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
                return;
            }

            Zone zone = new Zone();
            zone.setZoneId(zoneId);
            zone.setZoneName(name.trim());
            zone.setCapacity(capacity);
            zone.setDescription(description != null ? description.trim() : "");

            boolean success = zoneService.updateZoneInfo(zone);

            if (success) {
                session.setAttribute("successMsg", "Zone '" + name.trim() + "' updated successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to update zone. Please try again.");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid number format. Please check Zone ID and Capacity.");
        }

        response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
    }

    /**
     * Handles deleting a zone by its ID.
     * Validates the zone ID before attempting deletion.
     */
    private void handleDeleteZone(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        String zoneIdParam = request.getParameter("zoneId");

        // Validate zoneId is provided
        if (zoneIdParam == null || zoneIdParam.trim().isEmpty()) {
            session.setAttribute("errorMsg", "Zone ID is required for deletion.");
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
            return;
        }

        try {
            int zoneId = Integer.parseInt(zoneIdParam.trim());
            boolean success = zoneService.removeZone(zoneId);

            if (success) {
                session.setAttribute("successMsg", "Zone deleted successfully.");
            } else {
                session.setAttribute("errorMsg", "Failed to delete zone. It may contain active slots.");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Invalid Zone ID format.");
        }

        response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
    }
}