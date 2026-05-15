<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>ParkEase | Reports</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">
        <aside class="sidebar admin-sidebar">
            <div class="sidebar-header"><h2>Admin Portal</h2></div>
            <nav class="sidebar-nav">
                <a href="AdminServlet?action=dashboard">Dashboard</a>
                <a href="AdminServlet?action=manageUsers">Approve Users</a>
                <a href="ZoneServlet?action=list">Manage Zones</a>
                <a href="SlotServlet?action=list">Manage Slots</a>
                <a href="AdminServlet?action=reports" class="active">View Reports</a>
                <a href="LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <main class="main-content">
            <h1>System Analytics</h1>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <h3>Total Earnings</h3>
                    <p class="stat-number">$<%= request.getAttribute("totalRevenue") %></p>
                </div>
                <div class="stat-card">
                    <h3>Current Occupancy</h3>
                    <p class="stat-number"><%= request.getAttribute("occupancyRate") %>%</p>
                </div>
                <div class="stat-card">
                    <h3>Occupied Slots</h3>
                    <p class="stat-number"><%= request.getAttribute("occupiedSlots") %></p>
                </div>
                <div class="stat-card">
                    <h3>Total Slots</h3>
                    <p class="stat-number"><%= request.getAttribute("totalSlots") %></p>
                </div>
            </div>

            <section class="management-section">
                <h2>Recent Completed Transactions</h2>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Session ID</th>
                                <th>Hours</th>
                                <th>Amount Paid</th>
                                <th>Status</th>
                                <th style="text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                // Fetching the dynamic list passed by the AdminServlet
                                List<ParkingSession> completedSessions = (List<ParkingSession>) request.getAttribute("completedSessions");
                                
                                if (completedSessions == null || completedSessions.isEmpty()) { 
                            %>
                                <tr>
                                    <td colspan="5" style="text-align: center; padding: 20px; color: #718096; font-style: italic;">
                                        No completed transactions found.
                                    </td>
                                </tr>
                            <% } else { 
                                for (ParkingSession s : completedSessions) { %>
                                <tr>
                                    <td>#<%= s.getSessionId() %></td>
                                    <td><%= s.getTotalHours() %> hrs</td>
                                    <td>$<%= String.format("%.2f", s.getTotalCharges()) %></td>
                                    <td>
                                        <span style="color: #2f855a; font-weight: 600;">
                                            <%= s.getPaymentStatus() %>
                                        </span>
                                    </td>
                                    <td style="text-align: center;">
                                        <a href="AdminServlet?action=downloadReceipt&sessionId=<%= s.getSessionId() %>" 
                                           target="_blank" 
                                           style="color: #3182ce; text-decoration: none; font-weight: 600;">
                                           Download Receipt
                                        </a>
                                    </td>
                                </tr>
                            <% } 
                            } %>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</body>
</html>