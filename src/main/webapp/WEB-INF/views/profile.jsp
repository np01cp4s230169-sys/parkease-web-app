<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User, com.park.ease.model.Vehicle, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    List<Vehicle> vehicleList = (List<Vehicle>) request.getAttribute("vehicleList");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    String msg = request.getParameter("msg");
    String error = request.getParameter("error");
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

        <main class="main-content">
            <header class="content-header">
                <h1>My Profile</h1>
            </header>

            <!-- SUCCESS ALERTS -->
            <% if ("vehicle_added".equals(msg)) { %>
                <div class="alert-success">Vehicle added successfully.</div>
            <% } else if ("password_changed".equals(msg)) { %>
                <div class="alert-success">Password changed successfully.</div>
            <% } else if ("profile_updated".equals(msg)) { %>
                <div class="alert-success">Profile updated successfully.</div>
            <% } %>

            <!-- ERROR ALERTS -->
            <% if ("failed".equals(error)) { %>
                <div class="alert-danger">Operation failed. Please try again.</div>
            <% } else if ("empty_password_fields".equals(error)) { %>
                <div class="alert-danger">All password fields are required.</div>
            <% } else if ("password_mismatch".equals(error)) { %>
                <div class="alert-danger">New password and confirm password do not match.</div>
            <% } else if ("invalid_current_password".equals(error)) { %>
                <div class="alert-danger">Current password is incorrect.</div>
            <% } else if ("empty_profile_fields".equals(error)) { %>
                <div class="alert-danger">Name, email, and phone are required.</div>
            <% } else if ("invalid_email".equals(error)) { %>
                <div class="alert-danger">Invalid email format.</div>
            <% } else if ("email_exists".equals(error)) { %>
                <div class="alert-danger">Email is already used by another account.</div>
            <% } else if ("phone_exists".equals(error)) { %>
                <div class="alert-danger">Phone number is already used by another account.</div>
            <% } else if ("profile_update_failed".equals(error)) { %>
                <div class="alert-danger">Profile update failed. Please try again.</div>
            <% } %>

            <!-- PROFILE GRID 1 -->
            <div class="profile-grid">
                <section class="stat-card">
                    <h3>Account Details</h3>
                    <div class="profile-info">
                        <p><strong>Name:</strong> <%= user.getName() %></p>
                        <p><strong>Email:</strong> <%= user.getEmail() %></p>
                        <p><strong>Phone:</strong> <%= user.getPhone() %></p>
                        <p><strong>Role:</strong> <span class="user-badge"><%= user.getRole() %></span></p>
                    </div>
                </section>

                <section class="stat-card">
                    <h3>Update Profile</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet?action=updateProfile" method="POST" class="profile-form">
                        <input type="text" name="name" class="form-input" value="<%= user.getName() %>" placeholder="Full Name" required>
                        <input type="email" name="email" class="form-input" value="<%= user.getEmail() %>" placeholder="Email Address" required>
                        <input type="tel" name="phone" class="form-input" value="<%= user.getPhone() %>" placeholder="Phone Number" required>
                        <button type="submit" class="btn-primary">Update Profile</button>
                    </form>
                </section>
            </div>

            <!-- PROFILE GRID 2 -->
            <div class="profile-grid">
                <section class="stat-card">
                    <h3>Add New Vehicle</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet?action=addVehicle" method="POST" class="profile-form">
                        <input type="text" name="regNo" class="form-input" placeholder="Registration Number" required>
                        <select name="type" class="form-input">
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                        </select>
                        <input type="text" name="make" class="form-input" placeholder="Make (e.g. Toyota)" required>
                        <input type="text" name="model" class="form-input" placeholder="Model (e.g. Corolla)" required>
                        <input type="text" name="color" class="form-input" placeholder="Color" required>
                        <button type="submit" class="btn-primary">Register Vehicle</button>
                    </form>
                </section>

                <section class="stat-card">
                    <h3>Change Password</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet?action=changePassword" method="POST" class="profile-form">
                        <input type="password" name="currentPassword" class="form-input" placeholder="Current Password" required>
                        <input type="password" name="newPassword" class="form-input" placeholder="New Password" required>
                        <input type="password" name="confirmPassword" class="form-input" placeholder="Confirm New Password" required>
                        <button type="submit" class="btn-primary">Update Password</button>
                    </form>
                </section>
            </div>

            <!-- GARAGE TABLE -->
            <section class="management-section">
                <h2>My Garage</h2>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Reg. Number</th>
                                <th>Type</th>
                                <th>Brand/Model</th>
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