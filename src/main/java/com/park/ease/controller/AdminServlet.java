package com.park.ease.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.park.ease.model.User;
import com.park.ease.model.ParkingSession;
import com.park.ease.model.Inquiry;
import com.park.ease.service.SessionService;
import com.park.ease.service.SlotService;
import com.park.ease.service.UserService;
import com.park.ease.service.InquiryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AdminServlet handles all admin-related operations including
 * dashboard statistics, user approval/rejection, report generation,
 * inquiry management, and receipt downloads.
 * 
 * URL Patterns:
 *   /AdminServlet        - Main admin entry point with action parameter
 *   /admin/manage-users  - User approval management
 *   /admin/reports       - Revenue and occupancy reports
 */
@WebServlet(urlPatterns = {"/AdminServlet", "/admin/manage-users", "/admin/reports"})
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service layer dependencies for business logic
    private UserService userService = new UserService();
    private SlotService slotService = new SlotService();
    private SessionService sessionService = new SessionService();
    private InquiryService inquiryService = new InquiryService();

    /**
     * Handles GET requests - routes to appropriate handler based on
     * servlet path and action parameter.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        try {
            if ("/AdminServlet".equals(path)) {
                String action = request.getParameter("action");

                if ("dashboard".equals(action)) {
                    loadDashboard(request, response);
                } else if ("manageUsers".equals(action)) {
                    loadManageUsers(request, response);
                } else if ("reports".equals(action)) {
                    loadReports(request, response);
                } else if ("downloadReceipt".equals(action)) {
                    handleDownloadReceipt(request, response);
                } else {
                    // Default action: redirect to dashboard
                    response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
                }

            } else if ("/admin/manage-users".equals(path)) {
                loadManageUsers(request, response);
            } else if ("/admin/reports".equals(path)) {
                loadReports(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
            }
        } catch (Exception e) {
            // Log the error and forward to error page
            e.printStackTrace();
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles POST requests - processes user approval and rejection actions.
     * Validates userId parameter before processing to prevent invalid data.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String action = request.getParameter("action");
        String userIdParam = request.getParameter("userId");

        try {
            if ("/admin/manage-users".equals(path) || "/AdminServlet".equals(path)) {

                // Validate userId is provided and not empty
                if (userIdParam == null || userIdParam.trim().isEmpty()) {
                    request.getSession().setAttribute("errorMsg", "User ID is required to perform this action.");
                    response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers");
                    return;
                }

                // Validate userId is a valid number
                int userId;
                try {
                    userId = Integer.parseInt(userIdParam.trim());
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("errorMsg", "Invalid User ID format. Please provide a valid number.");
                    response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers");
                    return;
                }

                boolean success = false;
                String statusMessage = "";

                // Process approve or reject action
                if ("approve".equals(action) || "approveUser".equals(action)) {
                    success = userService.changeUserStatus(userId, "approved");
                    statusMessage = success ? "User has been approved successfully." : "Failed to approve user. Please try again.";
                } else if ("reject".equals(action) || "rejectUser".equals(action)) {
                    success = userService.changeUserStatus(userId, "rejected");
                    statusMessage = success ? "User has been rejected successfully." : "Failed to reject user. Please try again.";
                } else {
                    request.getSession().setAttribute("errorMsg", "Invalid action specified.");
                    response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers");
                    return;
                }

                // Set appropriate success or error message in session
                if (success) {
                    request.getSession().setAttribute("successMsg", statusMessage);
                } else {
                    request.getSession().setAttribute("errorMsg", statusMessage);
                }

                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");

        } catch (Exception e) {
            // Log the error and forward to error page
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your request.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    /**
     * Loads admin dashboard with statistics including slot counts,
     * total revenue, and active inquiries from contact form.
     */
    private void loadDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Fetch dashboard statistics: total slots, available, occupied counts
        Map<String, Integer> dashboardStats = slotService.getDashboardStats();
        double totalRevenue = sessionService.getTotalRevenue();

        // Fetch all active inquiries submitted through contact page
        List<Inquiry> activeInquiries = inquiryService.viewAllInquiries();

        request.setAttribute("dashboardStats", dashboardStats);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("inquiriesList", activeInquiries);

        request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
    }

    /**
     * Loads the user approval page with list of pending user registrations.
     * Admin can approve or reject each registration from this view.
     */
    private void loadManageUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> pendingUsers = userService.getPendingUsers();
        request.setAttribute("pendingUsers", pendingUsers);
        request.getRequestDispatcher("/WEB-INF/views/user-approval.jsp").forward(request, response);
    }

    /**
     * Loads the reports page with revenue data, slot occupancy statistics,
     * and a list of completed parking sessions for detailed analysis.
     */
    private void loadReports(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Fetch revenue and occupancy data for report display
        double revenue = sessionService.getTotalRevenue();
        int occupied = sessionService.getOccupiedSlotsCount();
        int total = sessionService.getTotalSlotsCount();
        double occupancyRate = sessionService.getOccupancyRate();

        // Fetch completed sessions list for detailed report table
        List<ParkingSession> completedSessions = sessionService.getCompletedSessions();

        request.setAttribute("totalRevenue", revenue);
        request.setAttribute("occupiedSlots", occupied);
        request.setAttribute("totalSlots", total);
        request.setAttribute("occupancyRate", String.format("%.2f", occupancyRate));
        request.setAttribute("completedSessions", completedSessions);

        request.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(request, response);
    }

    /**
     * Handles receipt download for a specific parking session.
     * Validates sessionId parameter and forwards to receipt view if found.
     */
    private void handleDownloadReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sessionIdParam = request.getParameter("sessionId");

        // Validate sessionId is provided
        if (sessionIdParam == null || sessionIdParam.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Session ID is required to generate receipt.");
            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=reports");
            return;
        }

        try {
            int sessionId = Integer.parseInt(sessionIdParam.trim());

            // Retrieve session details from database
            ParkingSession sessionData = sessionService.getSessionDetails(sessionId);

            if (sessionData != null) {
                request.setAttribute("receipt", sessionData);
                request.getRequestDispatcher("/WEB-INF/views/receipt.jsp").forward(request, response);
            } else {
                // Session not found - redirect with error message
                request.getSession().setAttribute("errorMsg", "No parking session found with ID: " + sessionId);
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=reports");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("errorMsg", "Invalid Session ID format. Please provide a valid number.");
            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=reports");
        }
    }
}