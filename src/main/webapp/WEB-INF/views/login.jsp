<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

    <div class="auth-card">
        <h1>ParkEase</h1>
        <p>Welcome back! Please login to your account.</p>

        <!-- Error message displayed when login credentials are invalid -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!-- Success message displayed after successful registration -->
        <% if (request.getAttribute("success") != null) { %>
            <div class="alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <!-- Login form - submits credentials to LoginServlet for authentication -->
        <form action="${pageContext.request.contextPath}/LoginServlet" method="POST">
            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-input"
                       required placeholder="Enter your email">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-input"
                       required placeholder="Enter your password">
            </div>

            <button type="submit" class="btn-primary">Login</button>
        </form>

        <!-- Link to registration page for new users -->
        <div class="auth-footer">
            <p>Don't have an account?
                <a href="${pageContext.request.contextPath}/UserServlet?action=register">Register here</a>
            </p>
        </div>
    </div>

</body>
</html>
