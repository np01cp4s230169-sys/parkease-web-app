<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<%
    User user = (User) session.getAttribute("user");
    List<Vehicle> vehicles = (List<Vehicle>) request.getAttribute("vehicleList");
    List<Zone> zones = (List<Zone>) request.getAttribute("zoneList");
    List<ParkingSlot> slots = (List<ParkingSlot>) request.getAttribute("slotList");

    String selectedZoneId = request.getParameter("zoneId");
    String error = request.getParameter("error");
    String msg = request.getParameter("msg");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Book Parking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header"><h2>ParkEase</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/UserServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=view" class="active">Book a Slot</a>
                <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings">My Bookings</a>
                <a href="${pageContext.request.contextPath}/UserServlet?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <main class="main-content">
            <header class="content-header">
                <h1>Secure Your Spot</h1>
            </header>

            <!-- ERROR MESSAGES -->
            <% if ("booking_failed".equals(error)) { %>
                <div class="alert-danger">Booking failed. Please try again.</div>
            <% } else if ("invalid_selection".equals(error)) { %>
                <div class="alert-danger">Please select valid booking details.</div>
            <% } else if ("select_zone_first".equals(error)) { %>
                <div class="alert-danger">Please select a zone first.</div>
            <% } %>

            <!-- SUCCESS MESSAGE -->
            <% if ("booking_success".equals(msg)) { %>
                <div class="alert-success">Slot booked successfully.</div>
            <% } %>

            <!-- BOOKING FORM -->
            <section class="stat-card">
                <form action="${pageContext.request.contextPath}/BookingServlet?action=confirmBooking" method="POST" class="profile-form">
                    
                    <div class="form-group">
                        <label>Choose Your Vehicle</label>
                        <select name="vehicleId" class="form-input" required>
                            <option value="">-- Select Registered Vehicle --</option>
                            <% if (vehicles != null) {
                                for (Vehicle v : vehicles) { %>
                                    <option value="<%= v.getVehicleId() %>">
                                        <%= v.getRegistrationNumber() %> (<%= v.getMake() %> <%= v.getModel() %>)
                                    </option>
                            <%  }
                            } else { %>
                                <option disabled>No vehicles registered</option>
                            <% } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Select Parking Zone</label>
                        <select name="zoneId" class="form-input" required>
                            <option value="">-- Choose Area --</option>
                            <% if (zones != null) {
                                for (Zone z : zones) { %>
                                    <option value="<%= z.getZoneId() %>"
                                        <%= String.valueOf(z.getZoneId()).equals(selectedZoneId) ? "selected" : "" %>>
                                        <%= z.getZoneName() %>
                                    </option>
                            <%  }
                            } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Available Slots</label>
                        <select name="slotId" class="form-input" required>
                            <option value="">-- Pick a Spot --</option>
                            <% if (slots != null) {
                                for (ParkingSlot s : slots) { %>
                                    <option value="<%= s.getSlotId() %>">
                                        <%= s.getSlotNumber() %> - ₹<%= s.getHourlyRate() %>/hr
                                    </option>
                            <%  }
                            } else { %>
                                <option disabled>Select zone first</option>
                            <% } %>
                        </select>
                    </div>

                    <button type="submit" class="btn-primary">Confirm Booking</button>
                </form>
            </section>
        </main>
    </div>
</body>
</html>