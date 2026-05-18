<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - ParkEase</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="error-page">
        <div class="error-card">

            <div class="error-icon">&#9888;</div>

            <%
                /* Establish the error on HTTP code */
                int statusCode = 500;
                Object codeAttr = request.getAttribute("jakarta.servlet.error.status_code");
                if (codeAttr != null) {
                    statusCode = (Integer) codeAttr;
                }

                /* Shows the appropriate title based on error */
                String title = "Something Went Wrong";
                if (statusCode == 404) {
                    title = "Page Not Found";
                } else if (statusCode == 403) {
                    title = "Access Denied";
                } else if (statusCode == 400) {
                    title = "Bad Request";
                }

                /* Verify the custom error message from the servlet */
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = (String) request.getAttribute("jakarta.servlet.error.message");
                }
                if (errorMessage == null || errorMessage.isEmpty()) {
                    if (statusCode == 404) {
                        errorMessage = "The page you are looking for does not exist or has been moved.";
                    } else if (statusCode == 403) {
                        errorMessage = "You do not have permission to access this page.";
                    } else {
                        errorMessage = "An unexpected error occurred. Please try again later or contact the administrator.";
                    }
                }
            %>

            <h1 class="error-code"><%= statusCode %></h1>
            <h2 class="error-title"><%= title %></h2>
            <p class="error-message"><%= errorMessage %></p>

            <div class="error-actions">
                <a href="javascript:history.back()" class="btn-primary">Go Back</a>
                <a href="${pageContext.request.contextPath}/index.jsp" class="btn-outline-primary">Back to Home</a>
            </div>

            <div class="error-footer-text">
                ParkEase Parking Management System
            </div>

        </div>
    </div>
</body>
</html>