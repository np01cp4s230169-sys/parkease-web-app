<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Admin Dashboard</title>
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
            <header class="content-header">
                <h1>System Analytics Dashboard</h1>
            </header>

            <!-- STATS GRID - Perfectly responsive with existing CSS -->
            <div class="stats-grid">
                <div class="stat-card">
                    <h3>Total Earnings</h3>
                    <p class="stat-number">₹<%= request.getAttribute("totalRevenue") != null ? request.getAttribute("totalRevenue") : "0" %></p>
                </div>
                <div class="stat-card">
                    <h3>Current Occupancy</h3>
                    <p class="stat-number"><%= request.getAttribute("occupancyRate") != null ? request.getAttribute("occupancyRate") : "0" %>%</p>
                </div>
                <div class="stat-card">
                    <h3>Occupied Slots</h3>
                    <p class="stat-number"><%= request.getAttribute("occupiedSlots") != null ? request.getAttribute("occupiedSlots") : "0" %></p>
                </div>
                <div class="stat-card">
                    <h3>Total Slots</h3>
                    <p class="stat-number"><%= request.getAttribute("totalSlots") != null ? request.getAttribute("totalSlots") : "0" %></p>
                </div>
            </div>

            <!-- TRANSACTIONS - Scroll protected -->
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
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                            @SuppressWarnings("unchecked")
                            List<Transaction> transactions = (List<Transaction>) request.getAttribute("recentTransactions");
                            if (transactions != null && !transactions.isEmpty()) {
                                for (Transaction txn : transactions) { %>
                                <tr>
                                    <td><%= txn.getSessionId() %></td>
                                    <td><%= txn.getDurationHours() %></td>
                                    <td>₹<%= txn.getAmountPaid() %></td>
                                    <td class="status-completed">✓ Completed</td>
                                </tr>
                            <% } 
                            } else { %>
                                <tr>
                                    <td colspan="4" class="no-data">📊 No recent transactions. Analytics updating...</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</body>
</html>