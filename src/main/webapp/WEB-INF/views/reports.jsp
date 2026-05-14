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
                <table class="data-table">
                    <thead>
                        <tr><th>Session ID</th><th>Hours</th><th>Amount Paid</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                        <tr><td colspan="4">Data visualization would load here.</td></tr>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>