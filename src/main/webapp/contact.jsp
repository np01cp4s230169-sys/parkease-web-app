<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Contact Us</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

    <div class="auth-container">
        <div class="auth-card" style="max-width: 600px;">
            <h1>Contact Support</h1>
            <p>Having trouble? Our team is here to help 24/7.</p>

            <div class="contact-info-block">
                <p><strong>Email:</strong> support@parkease.com</p>
                <p><strong>Phone:</strong> +1 (800) PARK-EASE</p>
                <p><strong>Office:</strong> 123 Tech Park Avenue, Silicon Valley</p>
            </div>

            <!-- DYNAMIC STATUS NOTIFICATION AREA -->
            <% if (request.getAttribute("successMessage") != null) { %>
                <div style="background-color: #d4edda; color: #155724; padding: 12px; margin-bottom: 20px; border-radius: 4px; text-align: left; font-size: 0.95rem; border: 1px solid #c3e6cb;">
                    <%= request.getAttribute("successMessage") %>
                </div>
            <% } %>

            <% if (request.getAttribute("errorMessage") != null) { %>
                <div style="background-color: #f8d7da; color: #721c24; padding: 12px; margin-bottom: 20px; border-radius: 4px; text-align: left; font-size: 0.95rem; border: 1px solid #f5c6cb;">
                    <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <!-- FORM ACTION ALIGNED WITH CONTROLLER DOPOST LIFECYCLE -->
            <form action="${pageContext.request.contextPath}/pages" method="post">
                <div class="form-group">
                    <label for="name">Your Name</label>
                    <input type="text" id="name" name="name" placeholder="Enter your name" required>
                </div>

                <div class="form-group">
                    <label for="email">Your Email</label>
                    <input type="email" id="email" name="email" placeholder="Enter your email" required>
                </div>

                <div class="form-group">
                    <label for="message">Your Message</label>
                    <textarea id="message" name="message" rows="5" placeholder="Write your inquiry here..." required></textarea>
                </div>

                <button type="submit" class="btn-primary">Send Inquiry</button>
            </form>

            <div class="auth-footer">
                <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
            </div>
        </div>
    </div>

</body>
</html>
