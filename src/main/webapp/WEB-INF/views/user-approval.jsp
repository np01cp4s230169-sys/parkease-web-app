<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User" %>
<%@ page import="java.util.List" %>

<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | User Approval</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

    <!-- Framework-free flexcard container layout mapped directly to style.css -->
    <div class="auth-card-wide">
        <h1>User Registration Control</h1>
        <p>Review and authorize pending driver registry tokens down to storage tables.</p>

        <!-- Dynamic notification blocks -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <% if (request.getParameter("msg") != null) { %>
            <div class="alert alert-success">
                Action Completed! Status updated successfully.
            </div>
        <% } %>

        <div class="form-group">
            <% 
                List<User> pendingUsers = (List<User>) request.getAttribute("pendingUsers");
                if (pendingUsers == null || pendingUsers.isEmpty()) { 
            %>
                <p style="text-align: center; color: #718096; font-style: italic; padding: 20px 0;">
                    No new user registrations are awaiting approval at this time.
                </p>
            <% } else { %>
                
                <!-- Scroll container prevents layout breaking on smaller viewports -->
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr style="border-bottom: 2px solid #ddd; text-align: left;">
                                <th style="padding: 12px 8px;">Name</th>
                                <th style="padding: 12px 8px;">Email</th>
                                <th style="padding: 12px 8px;">Phone</th>
                                <th style="padding: 12px 8px; text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (User pendingUser : pendingUsers) { %>
                                <tr style="border-bottom: 1px solid #ddd;">
                                    <td style="padding: 12px 8px;"><strong><%= pendingUser.getName() %></strong></td>
                                    <td style="padding: 12px 8px;"><%= pendingUser.getEmail() %></td>
                                    <td style="padding: 12px 8px;"><%= pendingUser.getPhone() != null ? pendingUser.getPhone() : "-" %></td>
                                    <td style="padding: 12px 8px; text-align: center;">
                                        
                                        <!-- Flexbox layout handles form alignments and multi-device stacking -->
                                        <div class="table-btn-flexbox">
                                            
                                            <form action="${pageContext.request.contextPath}/admin/manage-users" method="POST" style="margin: 0;">
                                                <input type="hidden" name="userId" value="<%= pendingUser.getUserId() %>">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn-primary" style="padding: 8px 16px; font-size: 0.9rem; margin: 0; width: auto;">Approve</button>
                                            </form>

                                            <form action="${pageContext.request.contextPath}/admin/manage-users" method="POST" style="margin: 0;">
                                                <input type="hidden" name="userId" value="<%= pendingUser.getUserId() %>">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn-primary" style="padding: 8px 16px; font-size: 0.9rem; margin: 0; width: auto; background-color: #e53e3e; border-color: #e53e3e;">Reject</button>
                                            </form>
                                            
                                        </div>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>

        <div class="auth-footer">
            <p><a href="${pageContext.request.contextPath}/AdminServlet?action=dashboard" style="text-decoration: none; font-weight: 600;">Return to Core Admin Dashboard</a></p>
        </div>
    </div>

</body>
</html>
