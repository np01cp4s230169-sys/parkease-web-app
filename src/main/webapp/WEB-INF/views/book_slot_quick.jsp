<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<%
    /* Security check: Redirect to login if user session is not active */
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Load slot and vehicle data from request attributes set by BookingServlet */
    ParkingSlot slot = (ParkingSlot) request.getAttribute("slot");
    List<Vehicle> vehicles = (List<Vehicle>) request.getAttribute("vehicles");

    /* Redirect back to search if required data is missing */
    if (slot == null || vehicles == null) {
        response.sendRedirect(request.getContextPath() + "/SlotServlet?action=search");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Confirm Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">

        <!-- Sidebar navigation for user portal -->
        <aside class="sidebar">
            <div class="sidebar-header"><h2>ParkEase</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/UserServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=search">Search Slots</a>
                <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings">My Bookings</a>
                <a href="${pageContext.request.contextPath}/UserServlet?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>Confirm Your Booking</h1>
                <div class="user-badge"><%= user.getRole() %></div>
            </header>

            <!-- Selected slot details summary -->
            <section class="stat-card" style="margin-bottom: 1.5rem;">
                <h3>Selected Parking Slot</h3>
                <div class="profile-info">
                    <p><strong>Slot Number:</strong> <%= slot.getSlotNumber() %></p>
                    <p><strong>Zone:</strong> Zone <%= slot.getZoneId() %></p>
                    <p><strong>Vehicle Type:</strong> <%= slot.getVehicleType() %></p>
                    <p><strong>Hourly Rate:</strong> Rs. <%= String.format("%.2f", slot.getHourlyRate()) %> per hour</p>
                    <p><strong>Status:</strong>
                        <span class="status-badge <%= slot.getStatus().toLowerCase() %>">
                            <%= slot.getStatus() %>
                        </span>
                    </p>
                </div>
            </section>

            <!-- Vehicle selection and booking confirmation form -->
            <section class="stat-card" style="margin-bottom: 1.5rem;">
                <h3>Select Your Vehicle</h3>
                <form action="${pageContext.request.contextPath}/BookingServlet" method="POST" class="profile-form">
                    <input type="hidden" name="action" value="confirmBooking">
                    <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">

                    <div class="form-group">
                        <label for="vehicleId">Choose Vehicle</label>
                        <select name="vehicleId" id="vehicleId" class="form-input" required>
                            <option value="">-- Select Your Vehicle --</option>
                            <% for (Vehicle v : vehicles) { %>
                                <option value="<%= v.getVehicleId() %>">
                                    <%= v.getRegistrationNumber() %> -
                                    <%= v.getMake() %> <%= v.getModel() %>
                                    (<%= v.getVehicleType() %>)
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <div style="display: flex; gap: 12px; margin-top: 1rem;">
                        <button type="submit" class="btn-primary">Confirm Booking</button>
                        <a href="${pageContext.request.contextPath}/SlotServlet?action=search"
                           class="btn-secondary" style="text-align: center; padding: 0.8rem 1.5rem;">
                            Cancel
                        </a>
                    </div>
                </form>
            </section>

            <!-- Booking information notice -->
            <section class="stat-card">
                <h3>Booking Information</h3>
                <div class="profile-info">
                    <p>Your parking session will start immediately after confirmation.</p>
                    <p>Hourly rate: Rs. <%= String.format("%.2f", slot.getHourlyRate()) %> per hour.</p>
                    <p>Payment will be calculated automatically upon checkout.</p>
                    <p>You can view and manage your booking in the My Bookings section.</p>
                </div>
            </section>

        </main>
    </div>
</body>
</html>
