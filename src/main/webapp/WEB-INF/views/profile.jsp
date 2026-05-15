<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User, com.park.ease.model.Vehicle, java.util.List" %>
<%
    /* Security check: Redirect to login if user session is not active */
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Load vehicle list from request attributes set by UserServlet */
    List<Vehicle> vehicleList = (List<Vehicle>) request.getAttribute("vehicleList");

    /* Read session messages and clear them after display */
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | My Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">

        <!-- Sidebar navigation for user portal -->
        <aside class="sidebar">
            <div class="sidebar-header"><h2>ParkEase</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/UserServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=search">Book a Slot</a>
                <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings">My Bookings</a>
                <a href="${pageContext.request.contextPath}/UserServlet?action=profile" class="active">Profile</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>My Profile</h1>
                <div class="user-badge"><%= user.getRole() %></div>
            </header>

            <!-- Success and error alerts from session -->
            <% if (successMsg != null) { %>
                <div class="alert-success"><%= successMsg %></div>
            <% } %>
            <% if (errorMsg != null) { %>
                <div class="alert-danger"><%= errorMsg %></div>
            <% } %>

            <!-- Account details and update profile forms -->
            <div class="profile-grid">

                <!-- Current account information display -->
                <section class="stat-card">
                    <h3>Account Details</h3>
                    <div class="profile-info">
                        <p><strong>Name:</strong> <%= user.getName() %></p>
                        <p><strong>Email:</strong> <%= user.getEmail() %></p>
                        <p><strong>Phone:</strong> <%= user.getPhone() %></p>
                        <p><strong>Role:</strong> <span class="user-badge"><%= user.getRole() %></span></p>
                        <p><strong>Status:</strong> <%= user.getStatus() %></p>
                    </div>
                </section>

                <!-- Update profile form -->
                <section class="stat-card">
                    <h3>Update Profile</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet" method="POST" class="profile-form">
                        <input type="hidden" name="action" value="updateProfile">
                        <input type="text" name="name" class="form-input"
                               value="<%= user.getName() %>" placeholder="Full Name" required>
                        <input type="email" name="email" class="form-input"
                               value="<%= user.getEmail() %>" placeholder="Email Address" required>
                        <input type="tel" name="phone" class="form-input"
                               value="<%= user.getPhone() %>" placeholder="Phone Number" required>
                        <button type="submit" class="btn-primary">Update Profile</button>
                    </form>
                </section>
            </div>

            <!-- Vehicle registration and password change forms -->
            <div class="profile-grid">

                <!-- Add new vehicle form -->
                <section class="stat-card">
                    <h3>Add New Vehicle</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet" method="POST" class="profile-form">
                        <input type="hidden" name="action" value="addVehicle">
                        <input type="text" name="regNo" class="form-input"
                               placeholder="Registration Number" required>
                        <select name="type" class="form-input" required>
                            <option value="">-- Select Vehicle Type --</option>
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                            <option value="TRUCK">Truck</option>
                        </select>
                        <input type="text" name="make" class="form-input"
                               placeholder="Make (e.g. Toyota)" required>
                        <input type="text" name="model" class="form-input"
                               placeholder="Model (e.g. Corolla)" required>
                        <input type="text" name="color" class="form-input"
                               placeholder="Color">
                        <button type="submit" class="btn-primary">Register Vehicle</button>
                    </form>
                </section>

                <!-- Change password form -->
                <section class="stat-card">
                    <h3>Change Password</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet" method="POST" class="profile-form">
                        <input type="hidden" name="action" value="changePassword">
                        <input type="password" name="currentPassword" class="form-input"
                               placeholder="Current Password" required>
                        <input type="password" name="newPassword" class="form-input"
                               placeholder="New Password" required>
                        <input type="password" name="confirmPassword" class="form-input"
                               placeholder="Confirm New Password" required>
                        <button type="submit" class="btn-primary">Update Password</button>
                    </form>
                </section>
            </div>

            <!-- Registered vehicles table -->
            <section class="management-section">
                <h2>My Garage</h2>
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr>
                                <th>Reg. Number</th>
                                <th>Type</th>
                                <th>Make / Model</th>
                                <th>Color</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (vehicleList != null && !vehicleList.isEmpty()) {
                                for (Vehicle v : vehicleList) { %>
                                <tr>
                                    <td><strong><%= v.getRegistrationNumber() %></strong></td>
                                    <td><%= v.getVehicleType() %></td>
                                    <td><%= v.getMake() %> <%= v.getModel() %></td>
                                    <td><%= v.getColor() %></td>
                                </tr>
                            <% } } else { %>
                                <tr>
                                    <td colspan="4" class="no-data">No vehicles registered yet.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

        </main>
    </div>
</body>
</html>
