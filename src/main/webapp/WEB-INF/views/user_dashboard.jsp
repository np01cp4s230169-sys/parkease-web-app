<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User" %>
<%
    /* Security check: Redirect to login if user session is not active */
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Read session messages and clear them after display */
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | User Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

    <div class="dashboard-container">

        <!-- Sidebar navigation for user portal -->
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>ParkEase</h2>
            </div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/UserServlet?action=dashboard" class="active">Dashboard</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=search">Book a Slot</a>
                <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings">My Bookings</a>
                <a href="${pageContext.request.contextPath}/UserServlet?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>Welcome, <%= user.getName() %>!</h1>
                <div class="user-badge"><%= user.getRole() %></div>
            </header>

            <!-- Success and error alerts from session -->
            <% if (successMsg != null) { %>
                <div class="alert-success"><%= successMsg %></div>
            <% } %>
            <% if (errorMsg != null) { %>
                <div class="alert-danger"><%= errorMsg %></div>
            <% } %>

            <!-- Dashboard summary cards -->
            <section class="stats-grid">
                <div class="stat-card">
                    <h3>Active Bookings</h3>
                    <p class="stat-number"><%= request.getAttribute("activeBookings") != null ? request.getAttribute("activeBookings") : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Available Slots</h3>
                    <p class="stat-number"><%= request.getAttribute("availableSlots") != null ? request.getAttribute("availableSlots") : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Total Spent</h3>
                    <p class="stat-number">Rs. <%= request.getAttribute("totalSpent") != null ? request.getAttribute("totalSpent") : "0.00" %></p>
                </div>
            </section>

            <!-- Quick action buttons for common user tasks -->
            <section class="quick-actions">
                <h2>Quick Actions</h2>
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/SlotServlet?action=search" class="btn-primary" style="text-align:center;">Find Parking</a>
                    <a href="${pageContext.request.contextPath}/UserServlet?action=profile" class="btn-secondary" style="text-align:center;">My Profile</a>
                    <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings" class="btn-secondary" style="text-align:center;">My Bookings</a>
                </div>
            </section>
        </main>
    </div>

</body>
</html>
