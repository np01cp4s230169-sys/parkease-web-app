<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
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
        <!-- Sidebar Navigation Element Area -->
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

        <!-- Main Workspace Workspace Section Area -->
        <main class="main-content">
            <header class="content-header">
                <h1>Welcome, <%= user.getName() %>!</h1>
                <div class="user-badge"><%= user.getRole() %></div>
            </header>

            <!-- Grid container leverages structural CSS percentages for fluid cards -->
            <section class="stats-grid">
                <div class="stat-card">
                    <h3>Active Bookings</h3>
                    <p class="stat-number">0</p>
                </div>
                <div class="stat-card">
                    <h3>Available Slots</h3>
                    <p class="stat-number">20</p>
                </div>
                <div class="stat-card">
                    <h3>Total Spent</h3>
                    <p class="stat-number">RS0.00</p>
                </div>
            </section>

            <!-- Quick Action Layout Handles Multi-Device Window Breakpoints Fluently -->
            <section class="quick-actions">
                <h2>Quick Actions</h2>
                <div class="action-buttons">
                    <!-- Wrapped classes within explicit button-level properties to match media guidelines -->
                    <a href="${pageContext.request.contextPath}/SlotServlet?action=search" class="btn-primary" style="text-align: center;">Find Parking</a>
                    <a href="${pageContext.request.contextPath}/UserServlet?action=profile" class="btn-secondary" style="text-align: center;">Add Vehicle</a>
                </div>
            </section>
        </main>
    </div>

</body>
</html>
