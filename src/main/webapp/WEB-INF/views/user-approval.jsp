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

    <div class="auth-card-wide">
        <h1>User Registration Control</h1>
        <p>Review and authorize pending driver registry tokens down to storage tables.</p>

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
                <div style="text-align: center; background-color: #f8fafc; border: 1px dashed #cbd5e0; border-radius: 8px; padding: 40px 20px; color: #718096; margin: 20px 0;">
                    <p style="font-style: italic; margin: 0;">No new user registrations are awaiting approval at this time.</p>
                </div>
            <% } else { %>
                
                <div class="table-scroll-container" style="margin-top: 20px;">
                    <table class="responsive-data-table" style="width: 100%; border-collapse: collapse;">
                        <thead>
                            <tr style="border-bottom: 2px solid #edf2f7; text-align: left; color: #4a5568;">
                                <th style="padding: 12px 8px;">Name</th>
                                <th style="padding: 12px 8px;">Email</th>
                                <th style="padding: 12px 8px;">Phone</th>
                                <th style="padding: 12px 8px; text-align: center;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (User pendingUser : pendingUsers) { %>
                                <tr style="border-bottom: 1px solid #edf2f7;">
                                    <td style="padding: 12px 8px;"><strong><%= pendingUser.getName() %></strong></td>
                                    <td style="padding: 12px 8px;"><%= pendingUser.getEmail() %></td>
                                    <td style="padding: 12px 8px;"><%= pendingUser.getPhone() != null ? pendingUser.getPhone() : "-" %></td>
                                    <td style="padding: 12px 8px;">
                                        
                                        <div class="table-btn-flexbox" style="display: flex; gap: 8px; justify-content: center; flex-wrap: wrap;">
                                            
                                            <form action="${pageContext.request.contextPath}/admin/manage-users" method="POST" style="margin: 0;">
                                                <input type="hidden" name="userId" value="<%= pendingUser.getUserId() %>">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn-primary" style="padding: 8px 16px; font-size: 0.85rem; cursor: pointer; width: auto;">Approve</button>
                                            </form>

                                            <form action="${pageContext.request.contextPath}/admin/manage-users" method="POST" style="margin: 0;">
                                                <input type="hidden" name="userId" value="<%= pendingUser.getUserId() %>">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn-primary" style="padding: 8px 16px; font-size: 0.85rem; cursor: pointer; width: auto; background-color: #e53e3e; border-color: #e53e3e;">Reject</button>
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

        <div class="auth-footer" style="margin-top: 30px; border-top: 1px solid #edf2f7; padding-top: 20px;">
            <p><a href="${pageContext.request.contextPath}/AdminServlet?action=dashboard" style="text-decoration: none; font-weight: 600; color: #3182ce;">Return to Core Admin Dashboard</a></p>
        </div>
    </div>

</body>
</html>