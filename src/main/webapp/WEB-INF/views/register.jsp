<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

    <div class="auth-card">
        <h1>Create Account</h1>
        <p>Join ParkEase to manage your parking easily.</p>

        <!-- Error message displayed when registration validation fails -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <!-- Registration form - submits to UserServlet for validation and account creation -->
        <form action="${pageContext.request.contextPath}/UserServlet" method="POST">
            <input type="hidden" name="action" value="doRegister">

            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" class="form-input"
                       required placeholder="Enter your full name">
            </div>

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input type="tel" id="phone" name="phone" class="form-input"
                       required placeholder="Enter phone number">
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-input"
                       required placeholder="Enter email address">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-input"
                       required placeholder="Create a password">
            </div>

            <button type="submit" class="btn-primary">Register Now</button>
        </form>

        <!-- Link to login page for existing users -->
        <div class="auth-footer">
            <p>Already have an account?
                <a href="${pageContext.request.contextPath}/LoginServlet">Login here</a>
            </p>
        </div>
    </div>

</body>
</html>
