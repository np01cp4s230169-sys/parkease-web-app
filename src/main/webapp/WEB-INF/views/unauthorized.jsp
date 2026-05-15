<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User" %>
<%
    /* Retrieve user session to determine redirect destination */
    User user = (User) session.getAttribute("user");
    String redirectUrl = request.getContextPath() + "/LoginServlet";
    if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
        redirectUrl = request.getContextPath() + "/AdminServlet?action=dashboard";
    } else if (user != null) {
        redirectUrl = request.getContextPath() + "/UserServlet?action=dashboard";
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Access Denied</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="error-page">
        <div class="error-card">

            <div class="error-icon">&#9888;</div>

            <h1 class="error-code">403</h1>
            <h2 class="error-title">Access Denied</h2>
            <p class="error-message">
                You do not have permission to access this page.
                Please contact the administrator if you believe this is an error.
            </p>

            <div class="error-actions">
                <a href="<%= redirectUrl %>" class="btn-primary">Go to Dashboard</a>
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn-outline-primary">Back to Home</a>
            </div>

            <div class="error-footer-text">
                ParkEase Parking Management System
            </div>

        </div>
    </div>
</body>
</html>
