<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Access Denied</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

    <div class="auth-container">
        <div class="auth-card">
            <h1>Access Denied</h1>
            <p>You are not authorized to access this page.</p>

            <div class="alert alert-danger">
                Unauthorized access. Please login with the correct account or return to the home page.
            </div>

            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-primary"
               style="display:inline-block; text-decoration:none; text-align:center;">
                Go Back Home
            </a>

            <div class="auth-footer">
                <p>Want to login with another account? <a href="${pageContext.request.contextPath}/LoginServlet">Login here</a></p>
            </div>
        </div>
    </div>

</body>
</html>