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

@WebServlet("/ZoneServlet")
public class ZoneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ZoneService zoneService = new ZoneService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("list".equals(action) || "add".equals(action)) {
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            handleCreateZone(request, response);
        } else if ("update".equals(action)) {
            handleUpdateZone(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list");
        }
    }

    private void handleCreateZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String name = request.getParameter("zoneName");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String description = request.getParameter("description");

            Zone newZone = new Zone();
            newZone.setZoneName(name);
            newZone.setCapacity(capacity);
            newZone.setDescription(description);

            boolean success = zoneService.createZone(newZone);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&msg=zone_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=add_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=invalid_capacity");
        }
    }

    private void handleEditZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int zoneId = Integer.parseInt(request.getParameter("zoneId"));

            Zone selectedZone = zoneService.getZoneById(zoneId);
            List<Zone> zoneList = zoneService.getAllZones();

            request.setAttribute("selectedZone", selectedZone);
            request.setAttribute("zoneList", zoneList);

            request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=invalid_zone");
        }
    }

    private void handleUpdateZone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int zoneId = Integer.parseInt(request.getParameter("zoneId"));
            String name = request.getParameter("zoneName");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String description = request.getParameter("description");

            Zone zone = new Zone();
            zone.setZoneId(zoneId);
            zone.setZoneName(name);
            zone.setCapacity(capacity);
            zone.setDescription(description);

            boolean success = zoneService.updateZoneInfo(zone);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&msg=zone_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=invalid_input");
        }
    }

    private void handleDeleteZone(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int zoneId = Integer.parseInt(request.getParameter("zoneId"));
            boolean success = zoneService.removeZone(zoneId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&msg=zone_deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=delete_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/ZoneServlet?action=list&error=invalid_zone");
        }
    }
}