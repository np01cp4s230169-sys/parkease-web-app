<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<%
    /* Security check: Restrict access to admin users only */
    User user = (User) session.getAttribute("user");
    if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Load report data from request attributes set by AdminServlet */
    List<ParkingSession> completedSessions = (List<ParkingSession>) request.getAttribute("completedSessions");
    double totalRevenue = request.getAttribute("totalRevenue") != null
            ? (Double) request.getAttribute("totalRevenue") : 0.0;
    int occupiedSlots = request.getAttribute("occupiedSlots") != null
            ? (Integer) request.getAttribute("occupiedSlots") : 0;
    int totalSlots = request.getAttribute("totalSlots") != null
            ? (Integer) request.getAttribute("totalSlots") : 0;
    String occupancyRate = request.getAttribute("occupancyRate") != null
            ? (String) request.getAttribute("occupancyRate") : "0.00";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">

        <!-- Sidebar navigation for admin portal -->
        <aside class="sidebar admin-sidebar">
            <div class="sidebar-header"><h2>Admin Portal</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/AdminServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/AdminServlet?action=manageUsers">Approve Users</a>
                <a href="${pageContext.request.contextPath}/ZoneServlet?action=list">Manage Zones</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=list">Manage Slots</a>
                <a href="${pageContext.request.contextPath}/AdminServlet?action=reports" class="active">View Reports</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>System Analytics</h1>
                <div class="user-badge admin-tag">Admin</div>
            </header>

            <!-- Revenue and occupancy summary cards -->
            <section class="stats-grid">
                <div class="stat-card">
                    <h3>Total Revenue</h3>
                    <p class="stat-number">Rs. <%= String.format("%.2f", totalRevenue) %></p>
                </div>
                <div class="stat-card">
                    <h3>Occupancy Rate</h3>
                    <p class="stat-number"><%= occupancyRate %>%</p>
                </div>
                <div class="stat-card">
                    <h3>Occupied Slots</h3>
                    <p class="stat-number"><%= occupiedSlots %></p>
                </div>
                <div class="stat-card">
                    <h3>Total Slots</h3>
                    <p class="stat-number"><%= totalSlots %></p>
                </div>
            </section>

            <!-- Completed transactions table -->
            <section class="management-section">
                <h2>Completed Transactions</h2>
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr>
                                <th>Session ID</th>
                                <th>Slot</th>
                                <th>Vehicle</th>
                                <th>Hours</th>
                                <th>Amount</th>
                                <th>Payment</th>
                                <th>Receipt</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (completedSessions == null || completedSessions.isEmpty()) { %>
                                <tr>
                                    <td colspan="7" class="no-data">No completed transactions found.</td>
                                </tr>
                            <% } else {
                                for (ParkingSession s : completedSessions) { %>
                                <tr>
                                    <td>#<%= s.getSessionId() %></td>
                                    <td><%= s.getSlotId() %></td>
                                    <td><%= s.getVehicleId() %></td>
                                    <td><%= String.format("%.1f", s.getTotalHours()) %> hrs</td>
                                    <td>Rs. <%= String.format("%.2f", s.getTotalCharges()) %></td>
                                    <td><span class="status-badge available"><%= s.getPaymentStatus() %></span></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/AdminServlet?action=downloadReceipt&sessionId=<%= s.getSessionId() %>"
                                           class="btn-primary" style="width:auto; padding:6px 12px; font-size:13px;">
                                            View Receipt
                                        </a>
                                    </td>
                                </tr>
                            <% }} %>
                        </tbody>
                    </table>
                </div>
            </section>

        </main>
    </div>
</body>
</html>
