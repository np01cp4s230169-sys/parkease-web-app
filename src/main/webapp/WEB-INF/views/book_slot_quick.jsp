<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    ParkingSlot slot = (ParkingSlot) request.getAttribute("slot");
    List<Vehicle> vehicles = (List<Vehicle>) request.getAttribute("vehicles");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

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

        <main class="main-content">
            <header class="content-header">
                <h1>🚗 Confirm Your Booking</h1>
            </header>

            <!-- SELECTED SLOT DETAILS -->
            <section class="stat-card booking-details-card">
                <h2>📋 Selected Parking Slot</h2>
                <div class="booking-details-grid">
                    <div class="detail-item">
                        <span class="detail-label">Slot Number:</span>
                        <span class="detail-value"><strong><%= slot.getSlotNumber() %></strong></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Zone ID:</span>
                        <span class="detail-value"><%= slot.getZoneId() %></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Vehicle Type:</span>
                        <span class="detail-value"><%= slot.getVehicleType() %></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Hourly Rate:</span>
                        <span class="detail-value price">₹<%= slot.getHourlyRate() %></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Status:</span>
                        <span class="status-badge <%= slot.getStatus().toLowerCase() %>">
                            <%= slot.getStatus() %>
                        </span>
                    </div>
                </div>
            </section>

            <!-- BOOKING FORM -->
            <section class="stat-card">
                <h2>🚘 Select Your Vehicle</h2>
                <form action="${pageContext.request.contextPath}/BookingServlet?action=confirmBooking" 
                      method="POST" class="profile-form">
                    
                    <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                    
                    <div class="form-group">
                        <label for="vehicleId">Choose Vehicle <span class="required">*</span></label>
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

                    <div class="form-actions" style="display: flex; gap: 12px; margin-top: 24px;">
                        <button type="submit" class="btn-primary" style="flex: 1;">
                            ✅ Confirm Booking
                        </button>
                        <a href="${pageContext.request.contextPath}/SlotServlet?action=search" 
                           class="btn-secondary" style="flex: 1; text-align: center; padding: 12px;">
                            ❌ Cancel
                        </a>
                    </div>
                </form>
            </section>

            <!-- INFO BOX -->
            <section class="info-box">
                <h3>ℹ️ Booking Information</h3>
                <ul>
                    <li>✓ Your parking session will start immediately after confirmation</li>
                    <li>✓ You can view active bookings in "My Bookings" section</li>
                    <li>✓ Hourly rate: ₹<%= slot.getHourlyRate() %>/hour</li>
                    <li>✓ Payment will be calculated upon checkout</li>
                </ul>
            </section>
        </main>
    </div>
</body>
</html>