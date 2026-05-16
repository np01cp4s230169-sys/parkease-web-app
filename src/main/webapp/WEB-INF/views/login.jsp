<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .remember-me {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 1rem;
            font-size: 0.9rem;
            color: #555;
        }
        .remember-me input[type="checkbox"] {
            width: 16px;
            height: 16px;
            cursor: pointer;
        }
        .cookie-notice {
            font-size: 0.78rem;
            color: #888;
            text-align: center;
            margin-top: 0.5rem;
        }
    </style>
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

        <%
            /* Read remembered email from request attribute set by LoginServlet doGet */
            String rememberedEmail = (String) request.getAttribute("rememberedEmail");
            if (rememberedEmail == null) rememberedEmail = "";
            boolean hasRememberedEmail = !rememberedEmail.isEmpty();
        %>

        <!-- Login form - submits credentials to LoginServlet for authentication -->
        <form action="${pageContext.request.contextPath}/LoginServlet" method="POST">

            <div class="form-group">
                <label for="email">Email Address</label>
                <!-- Pre-fill email from Remember Me cookie if available -->
                <input type="email" id="email" name="email" class="form-input"
                       required placeholder="Enter your email"
                       value="<%= rememberedEmail %>">
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-input"
                       required placeholder="Enter your password">
            </div>

            <!-- Remember Me checkbox - stores email in cookie for 7 days -->
            <div class="remember-me">
                <input type="checkbox" id="rememberMe" name="rememberMe"
                       <%= hasRememberedEmail ? "checked" : "" %>>
                <label for="rememberMe">Remember me for 7 days</label>
            </div>

            <button type="submit" class="btn-primary">Login</button>

            <!-- Cookie notice for transparency -->
            <% if (hasRememberedEmail) { %>
                <p class="cookie-notice">
                    Your email is saved by a cookie.
                    Uncheck "Remember me" and login to remove it.
                </p>
            <% } %>
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
