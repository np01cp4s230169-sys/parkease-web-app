<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<%
    String msg = request.getParameter("msg");
    String error = request.getParameter("error");

    Integer totalBookings = (Integer) request.getAttribute("totalBookings");
    if (totalBookings == null) {
        totalBookings = 0;
    }

    List<ParkingSession> activeSessions = (List<ParkingSession>) request.getAttribute("activeSessions");
    List<ParkingSession> completedSessions = (List<ParkingSession>) request.getAttribute("completedSessions");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>ParkEase | My Bookings</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header"><h2>ParkEase</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/UserServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=search">Book a Slot</a>
                <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings" class="active">My Bookings</a>
                <a href="${pageContext.request.contextPath}/UserServlet?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <main class="main-content">
            <header class="content-header">
                <h1>My Parking Records</h1>
            </header>

            <% if ("checked_out".equals(msg)) { %>
                <p style="color: green; font-weight: bold;">
                    Session ended successfully. Total charge: <%= request.getParameter("charge") %>
                </p>
            <% } %>

            <% if ("checkout_failed".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Failed to complete checkout.</p>
            <% } else if ("invalid_session".equals(error)) { %>
                <p style="color: red; font-weight: bold;">Invalid session details.</p>
            <% } %>

            <section class="stat-card" style="margin-bottom: 2rem;">
                <h3>Total Bookings</h3>
                <p style="font-size: 2rem; font-weight: bold;"><%= totalBookings %></p>
            </section>

            <section class="management-section">
                <h2>Current Active Sessions</h2>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Session ID</th>
                            <th>Slot #</th>
                            <th>Vehicle ID</th>
                            <th>Entry Time</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (activeSessions != null && !activeSessions.isEmpty()) {
                            for (ParkingSession s : activeSessions) { %>
                            <tr>
                                <td><%= s.getSessionId() %></td>
                                <td><strong><%= s.getSlotId() %></strong></td>
                                <td><%= s.getVehicleId() %></td>
                                <td><%= s.getEntryTime() %></td>
                                <td><span class="status-pill active">PARKED</span></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/BookingServlet?action=endSession" method="POST">
                                        <input type="hidden" name="sessionId" value="<%= s.getSessionId() %>">
                                        <input type="hidden" name="slotId" value="<%= s.getSlotId() %>">
                                        <button type="submit" class="btn-primary" style="background:#e67e22;">End Session & Pay</button>
                                    </form>
                                </td>
                            </tr>
                        <% } } else { %>
                            <tr><td colspan="6">No active parking sessions found.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </section>

            <section class="management-section" style="margin-top: 2rem;">
                <h2>Completed Sessions</h2>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Session ID</th>
                            <th>Slot #</th>
                            <th>Vehicle ID</th>
                            <th>Entry Time</th>
                            <th>Exit Time</th>
                            <th>Total Hours</th>
                            <th>Total Charges</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (completedSessions != null && !completedSessions.isEmpty()) {
                            for (ParkingSession s : completedSessions) { %>
                            <tr>
                                <td><%= s.getSessionId() %></td>
                                <td><%= s.getSlotId() %></td>
                                <td><%= s.getVehicleId() %></td>
                                <td><%= s.getEntryTime() %></td>
                                <td><%= s.getExitTime() %></td>
                                <td><%= s.getTotalHours() %></td>
                                <td><%= s.getTotalCharges() %></td>
                                <td><span class="status-pill"><%= s.getStatus() %></span></td>
                            </tr>
                        <% } } else { %>
                            <tr><td colspan="8">No completed sessions found.</td></tr>
                        <% } %>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>