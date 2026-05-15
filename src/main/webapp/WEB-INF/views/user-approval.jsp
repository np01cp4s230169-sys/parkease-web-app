<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User, java.util.List" %>
<%
    /* Security check: Restrict access to admin users only */
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Read session messages and clear them after display */
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");

    /* Load pending users from request attributes set by AdminServlet */
    List<User> pendingUsers = (List<User>) request.getAttribute("pendingUsers");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | User Approval</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">

        <!-- Sidebar navigation for admin portal -->
        <aside class="sidebar admin-sidebar">
            <div class="sidebar-header"><h2>Admin Portal</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/AdminServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/AdminServlet?action=manageUsers" class="active">Approve Users</a>
                <a href="${pageContext.request.contextPath}/ZoneServlet?action=list">Manage Zones</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=list">Manage Slots</a>
                <a href="${pageContext.request.contextPath}/AdminServlet?action=reports">View Reports</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>User Approval Management</h1>
                <div class="user-badge admin-tag">Admin</div>
            </header>

            <!-- Success and error alerts from session -->
            <% if (successMsg != null) { %>
                <div class="alert-success"><%= successMsg %></div>
            <% } %>
            <% if (errorMsg != null) { %>
                <div class="alert-danger"><%= errorMsg %></div>
            <% } %>

            <!-- Pending user registrations table -->
            <section class="management-section">
                <div class="section-header">
                    <h2>Pending Registrations</h2>
                </div>

                <% if (pendingUsers == null || pendingUsers.isEmpty()) { %>
                    <p class="no-data">No user registrations are currently pending approval.</p>
                <% } else { %>
                    <div class="table-scroll-container">
                        <table class="responsive-data-table">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (User pendingUser : pendingUsers) { %>
                                    <tr>
                                        <td><strong><%= pendingUser.getName() %></strong></td>
                                        <td><%= pendingUser.getEmail() %></td>
                                        <td><%= pendingUser.getPhone() != null ? pendingUser.getPhone() : "-" %></td>
                                        <td>
                                            <div class="table-btn-flexbox">

                                                <!-- Approve user registration -->
                                                <form action="${pageContext.request.contextPath}/admin/manage-users" method="POST" style="margin:0;">
                                                    <input type="hidden" name="userId" value="<%= pendingUser.getUserId() %>">
                                                    <input type="hidden" name="action" value="approve">
                                                    <button type="submit" class="btn-success btn-small">Approve</button>
                                                </form>

                                                <!-- Reject user registration -->
                                                <form action="${pageContext.request.contextPath}/admin/manage-users" method="POST" style="margin:0;"
                                                      onsubmit="return confirm('Are you sure you want to reject this user?');">
                                                    <input type="hidden" name="userId" value="<%= pendingUser.getUserId() %>">
                                                    <input type="hidden" name="action" value="reject">
                                                    <button type="submit" class="btn-danger btn-small">Reject</button>
                                                </form>

                                            </div>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </section>

        </main>
    </div>
</body>
</html>
