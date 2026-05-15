<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Smart Parking Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

    <!-- Main navigation bar -->
    <nav class="navbar">
        <div class="logo">ParkEase</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/pages?action=contact">Contact Us</a>
            <a href="${pageContext.request.contextPath}/LoginServlet">Login</a>
            <a href="${pageContext.request.contextPath}/UserServlet?action=register" class="btn-outline">Sign Up</a>
        </div>
    </nav>

    <!-- Hero section with call to action -->
    <header class="hero">
        <div class="hero-content">
            <h1>Parking Made Simple.</h1>
            <p>Find, book, and manage your parking slots in seconds with ParkEase.</p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/UserServlet?action=register" class="btn-primary">Get Started Now</a>
                <a href="${pageContext.request.contextPath}/pages?action=about" class="btn-secondary">Learn More</a>
            </div>
        </div>
    </header>

    <!-- Page footer -->
    <footer class="footer">
        <p>&copy; 2026 ParkEase System. All Rights Reserved.</p>
    </footer>

</body>
</html>
