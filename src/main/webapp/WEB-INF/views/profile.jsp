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

            <% if ("vehicle_added".equals(msg)) { %>
                <p style="color: green; font-weight: bold;">Vehicle added successfully.</p>
            <% } else if ("password_changed".equals(msg)) { %>
                <p style="color: green; font-weight: bold;">Password changed successfully.</p>
            <% } else if ("profile_updated".equals(msg)) { %>
                <p style="color: green; font-weight: bold;">Profile updated successfully.</p>
            <% } %>
            

            <% if ("failed".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Operation failed. Please try again.</p>
            <% } else if ("empty_password_fields".equals(error)) { %>
                <p style="color: red; font-weight: bold;">All password fields are required.</p>
            <% } else if ("password_mismatch".equals(error)) { %>
                <p style="color: red; font-weight: bold;">New password and confirm password do not match.</p>
            <% } else if ("invalid_current_password".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Current password is incorrect.</p>
            <% } else if ("empty_profile_fields".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Name, email, and phone are required.</p>
            <% } else if ("invalid_email".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Invalid email format.</p>
            <% } else if ("email_exists".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Email is already used by another account.</p>
            <% } else if ("phone_exists".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Phone number is already used by another account.</p>
            <% } else if ("profile_update_failed".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Profile update failed. Please try again.</p>
            <% } %>

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
                        <input type="text" name="name" value="<%= user.getName() %>" placeholder="Full Name" required>
                        <input type="email" name="email" value="<%= user.getEmail() %>" placeholder="Email Address" required>
                        <input type="text" name="phone" value="<%= user.getPhone() %>" placeholder="Phone Number" required>
                        <button type="submit" class="btn-primary">Update Profile</button>
                    </form>
                </section>
            </div>

            <div class="profile-grid" style="margin-top: 2rem;">
                <section class="stat-card">
                    <h3>Add New Vehicle</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet?action=addVehicle" method="POST" class="profile-form">
                        <input type="text" name="regNo" placeholder="Registration Number" required>
                        <select name="type">
                            <option value="CAR">Car</option>
                            <option value="BIKE">Bike</option>
                        </select>
                        <input type="text" name="make" placeholder="Make (e.g. Toyota)" required>
                        <input type="text" name="model" placeholder="Model (e.g. Corolla)" required>
                        <input type="text" name="color" placeholder="Color" required>
                        <button type="submit" class="btn-primary">Register Vehicle</button>
                    </form>
                </section>

                <section class="stat-card">
                    <h3>Change Password</h3>
                    <form action="${pageContext.request.contextPath}/UserServlet?action=changePassword" method="POST" class="profile-form">
                        <input type="password" name="currentPassword" placeholder="Current Password" required>
                        <input type="password" name="newPassword" placeholder="New Password" required>
                        <input type="password" name="confirmPassword" placeholder="Confirm New Password" required>
                        <button type="submit" class="btn-primary">Update Password</button>
                    </form>
                </section>
            </div>

            <section class="management-section" style="margin-top: 2rem;">
                <h2>My Garage</h2>
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
                            <tr><td colspan="4">No vehicles registered yet.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>