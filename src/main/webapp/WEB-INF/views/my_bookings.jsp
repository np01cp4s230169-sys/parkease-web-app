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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
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

            <!-- SUCCESS MESSAGES -->
            <% if ("checked_out".equals(msg)) { %>
                <div class="alert-success">
                    Session ended successfully. Total charge: ₹<%= request.getParameter("charge") %>
                </div>
            <% } else if ("booking_success".equals(msg)) { %>
                <div class="alert-success">Slot booked successfully!</div>
            <% } %>

            <!-- ERROR MESSAGES -->
            <% if ("checkout_failed".equals(error)) { %>
                <div class="alert-danger">Failed to complete checkout.</div>
            <% } else if ("invalid_session".equals(error)) { %>
                <div class="alert-danger">Invalid session details.</div>
            <% } %>

            <!-- TOTAL BOOKINGS CARD -->
            <section class="stat-card">
                <h3>Total Bookings</h3>
                <p class="stat-number"><%= totalBookings %></p>
            </section>

            <!-- ACTIVE SESSIONS TABLE -->
            <section class="management-section">
                <h2>Current Active Sessions</h2>
                <div class="table-scroll-container">
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
                                    <td><span class="status-active">PARKED</span></td>
                                    <td class="table-btn-flexbox">
                                        <form action="${pageContext.request.contextPath}/BookingServlet?action=endSession" method="POST">
                                            <input type="hidden" name="sessionId" value="<%= s.getSessionId() %>">
                                            <input type="hidden" name="slotId" value="<%= s.getSlotId() %>">
                                            <button type="submit" class="btn-primary">End Session</button>
                                        </form>
                                    </td>
                                </tr>
                            <% } } else { %>
                                <tr>
                                    <td colspan="6" class="no-data">No active parking sessions found.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- COMPLETED SESSIONS TABLE -->
            <section class="management-section">
                <h2>Completed Sessions</h2>
                <div class="table-scroll-container">
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
                                <th>Receipt</th> <!-- NEW COLUMN -->
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
                                    <td>₹<%= s.getTotalCharges() %></td>
                                    <td><span class="status-completed">COMPLETED</span></td>
                                    
                                    <!-- NEW: Receipt Button -->
                                    <td class="table-btn-flexbox">
                                        <a href="${pageContext.request.contextPath}/BookingServlet?action=viewReceipt&sessionId=<%= s.getSessionId() %>" 
                                           class="btn-receipt btn-small" target="_blank">
                                            📄 View
                                        </a>
                                    </td>
                                </tr>
                            <% } } else { %>
                                <tr>
                                    <td colspan="9" class="no-data">No completed sessions found.</td>
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