<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | About Us</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Navigation bar -->
    <nav class="navbar">
        <div class="logo">ParkEase</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
            <a href="${pageContext.request.contextPath}/pages?action=contact">Contact Us</a>
            <a href="${pageContext.request.contextPath}/LoginServlet">Login</a>
            <a href="${pageContext.request.contextPath}/UserServlet?action=register" class="btn-outline">Sign Up</a>
        </div>
    </nav>

    <!-- Content for About Us -->
    <div class="profile-container">

        <div class="profile-header">
            <h1 class="profile-title">About ParkEase</h1>
            <p class="profile-description">
                ParkEase is a web-based parking management system built using Java, Jakarta EE Servlets,
                JavaServer Pages, and MySQL. It provides a streamlined platform for managing parking
                zones, slots, bookings, and user accounts.
            </p>
        </div>

        <div class="profile-content-wrapper">

            <!-- Section for What we do -->
            <div class="profile-column">
                <h2>What We Do</h2>
                <p>
                    ParkEase simplifies parking management for both administrators and drivers.
                    Administrators can manage zones, slots, and user approvals through a dedicated
                    dashboard. Users can search for available slots, make bookings, track their
                    parking sessions, and view receipts — all from one platform.
                </p>
            </div>

            <!-- Section for Why choose us -->
            <div class="profile-column">
                <h2>Why Choose ParkEase?</h2>
                <p>Our system offers:</p>
                <ul style="padding-left: 1.2rem; line-height: 2;">
                    <li>Real-time slot availability and booking</li>
                    <li>Secure role-based access for admins and users</li>
                    <li>Automated charge calculation on checkout</li>
                    <li>Revenue and occupancy reports for admins</li>
                    <li>Vehicle wishlist and quick booking features</li>
                </ul>
            </div>

        </div>

        <!-- Section for Contact Us -->
        <div class="stat-card" style="margin-bottom: 2rem;">
            <h3>Get In Touch</h3>
            <div class="profile-info">
                <p><strong>Email:</strong> support@parkease.com</p>
                <p><strong>Phone:</strong> +977 9800000000</p>
                <p><strong>Address:</strong> Kathmandu, Bagmati Province, Nepal</p>
            </div>
        </div>

        <!-- Footer Page -->
        <div class="footer">
            <p>&copy; 2026 ParkEase System. All Rights Reserved.</p>
        </div>

    </div>

</body>
</html>
