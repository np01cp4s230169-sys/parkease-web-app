package com.park.ease.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.park.ease.model.User;
import com.park.ease.model.ParkingSession;
import com.park.ease.service.SessionService;
import com.park.ease.service.SlotService;
import com.park.ease.service.UserService;
import com.park.ease.model.Inquiry;
import com.park.ease.service.InquiryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // ✅ ADDED

@WebServlet(urlPatterns = {"/AdminServlet", "/admin/manage-users", "/admin/reports"})
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserService userService = new UserService();
    private SlotService slotService = new SlotService();
    private SessionService sessionService = new SessionService();
    private InquiryService inquiryService = new InquiryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ RBAC CHECK - MANDATORY FOR ADMIN ACCESS
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=session_expired");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin Only");
            return;
        }
        // ✅ RBAC CHECK ENDS

        String path = request.getServletPath();

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
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
            }

        } else if ("/admin/manage-users".equals(path)) {
            loadManageUsers(request, response);
        } else if ("/admin/reports".equals(path)) {
            loadReports(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // HELPER METHOD FOR RECEIPT GENERATION
    // ──────────────────────────────────────────────────────────────────────────
    private void handleDownloadReceipt(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sessionIdParam = request.getParameter("sessionId");
        
        if (sessionIdParam != null && !sessionIdParam.isEmpty()) {
            try {
                int sessionId = Integer.parseInt(sessionIdParam);
                ParkingSession sessionData = sessionService.getSessionDetails(sessionId);
                
                if (sessionData != null) {
                    request.setAttribute("receipt", sessionData);
                    request.getRequestDispatcher("/WEB-INF/views/receipt.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=reports&error=not_found");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ✅ RBAC CHECK - MANDATORY FOR ADMIN ACCESS
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=session_expired");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }
        // ✅ RBAC CHECK ENDS

        String path = request.getServletPath();
        String action = request.getParameter("action");
        String userIdParam = request.getParameter("userId");

        if ("/admin/manage-users".equals(path) || "/AdminServlet".equals(path)) {
            if (userIdParam == null || userIdParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers&error=invalid_user");
                return;
            }

            int userId;
            try {
                userId = Integer.parseInt(userIdParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers&error=invalid_user");
                return;
            }
            
            boolean success = false;

            if ("approve".equals(action) || "approveUser".equals(action)) {
                success = userService.changeUserStatus(userId, "approved");
            } else if ("reject".equals(action) || "rejectUser".equals(action)) {
                success = userService.changeUserStatus(userId, "rejected");
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers&msg=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/AdminServlet?action=manageUsers&error=failed");
            }
            return;
        }
        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=dashboard");
    }

    private void loadDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Integer> dashboardStats = slotService.getDashboardStats();
        double totalRevenue = sessionService.getTotalRevenue();
        
        List<Inquiry> activeInquiries = inquiryService.viewAllInquiries();
        request.setAttribute("inquiriesList", activeInquiries); 

        request.setAttribute("dashboardStats", dashboardStats);
        request.setAttribute("totalRevenue", totalRevenue);
        
        request.getRequestDispatcher("/WEB-INF/views/admin_dashboard.jsp").forward(request, response);
    }

    private void loadManageUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<User> pendingUsers = userService.getPendingUsers();
        request.setAttribute("pendingUsers", pendingUsers);
        request.getRequestDispatcher("/WEB-INF/views/user-approval.jsp").forward(request, response);
    }

    private void loadReports(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        double revenue = sessionService.getTotalRevenue();
        int occupied = sessionService.getOccupiedSlotsCount();
        int total = sessionService.getTotalSlotsCount();
        double occupancyRate = sessionService.getOccupancyRate();

        List<ParkingSession> completedSessions = sessionService.getCompletedSessions();
        request.setAttribute("completedSessions", completedSessions);

        request.setAttribute("totalRevenue", revenue);
        request.setAttribute("occupiedSlots", occupied);
        request.setAttribute("totalSlots", total);
        request.setAttribute("occupancyRate", String.format("%.2f", occupancyRate));
        request.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(request, response);
    }
}