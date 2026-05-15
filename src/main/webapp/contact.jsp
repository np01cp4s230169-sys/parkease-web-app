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
        <div class="auth-card-wide">
            <h1>Contact Support</h1>
            <p>Have a question or need help? Our team is here to assist you.</p>

            <!-- Contact information block -->
            <div class="contact-info">
                <p><strong>Email:</strong> support@parkease.com</p>
                <p><strong>Phone:</strong> +977 9800000000</p>
                <p><strong>Office:</strong> Kathmandu, Bagmati Province, Nepal</p>
            </div>

            <!-- Success message displayed after successful form submission -->
            <% if (request.getAttribute("successMessage") != null) { %>
                <div class="alert-success">
                    <%= request.getAttribute("successMessage") %>
                </div>
            <% } %>

            <!-- Error message displayed when form submission fails -->
            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert-danger">
                    <%= request.getAttribute("errorMessage") %>
                </div>
            <% } %>

            <!-- Contact inquiry form - submits to PageServlet -->
            <form action="${pageContext.request.contextPath}/pages" method="POST">
                <div class="form-group">
                    <label for="name">Your Name</label>
                    <input type="text" id="name" name="name"
                           class="form-input" placeholder="Enter your name" required>
                </div>

                <div class="form-group">
                    <label for="email">Your Email</label>
                    <input type="email" id="email" name="email"
                           class="form-input" placeholder="Enter your email" required>
                </div>

                <div class="form-group">
                    <label for="message">Your Message</label>
                    <textarea id="message" name="message" rows="5"
                              placeholder="Write your inquiry here..." required></textarea>
                </div>

                <button type="submit" class="btn-primary">Send Inquiry</button>
            </form>

            <!-- Navigation link back to home -->
            <div class="auth-footer">
                <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Home</a></p>
            </div>
        </div>
    </div>

</body>
</html>
