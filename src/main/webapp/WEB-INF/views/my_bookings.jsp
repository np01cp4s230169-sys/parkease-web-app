<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List" %>
<%
    /* Security check: Redirect to login if user session is not active */
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Read session messages and clear them after display */
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");

    /* Load booking data from request attributes set by BookingServlet */
    Integer totalBookings = (Integer) request.getAttribute("totalBookings");
    if (totalBookings == null) totalBookings = 0;

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

        <!-- Sidebar navigation for user portal -->
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

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>My Parking Records</h1>
                <div class="user-badge"><%= currentUser.getRole() %></div>
            </header>

            <!-- Success and error alerts from session -->
            <% if (successMsg != null) { %>
                <div class="alert-success"><%= successMsg %></div>
            <% } %>
            <% if (errorMsg != null) { %>
                <div class="alert-danger"><%= errorMsg %></div>
            <% } %>

            <!-- Total bookings summary card -->
            <section class="stats-grid">
                <div class="stat-card">
                    <h3>Total Bookings</h3>
                    <p class="stat-number"><%= totalBookings %></p>
                </div>
                <div class="stat-card">
                    <h3>Active Sessions</h3>
                    <p class="stat-number"><%= activeSessions != null ? activeSessions.size() : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Completed Sessions</h3>
                    <p class="stat-number"><%= completedSessions != null ? completedSessions.size() : 0 %></p>
                </div>
            </section>

            <!-- Active parking sessions table -->
            <section class="management-section">
                <h2>Current Active Sessions</h2>
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr>
                                <th>Session ID</th>
                                <th>Slot</th>
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
                                    <td><span class="status-badge occupied">PARKED</span></td>
                                    <td>
                                        <!-- End session form with confirmation -->
                                        <form action="${pageContext.request.contextPath}/BookingServlet" method="POST"
                                              onsubmit="return confirm('Are you sure you want to end this session?');">
                                            <input type="hidden" name="action" value="endSession">
                                            <input type="hidden" name="sessionId" value="<%= s.getSessionId() %>">
                                            <input type="hidden" name="slotId" value="<%= s.getSlotId() %>">
                                            <button type="submit" class="btn-danger btn-small">End Session</button>
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

            <!-- Completed parking sessions table -->
            <section class="management-section">
                <h2>Completed Sessions</h2>
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr>
                                <th>Session ID</th>
                                <th>Slot</th>
                                <th>Vehicle ID</th>
                                <th>Entry Time</th>
                                <th>Exit Time</th>
                                <th>Hours</th>
                                <th>Charges</th>
                                <th>Status</th>
                                <th>Receipt</th>
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
                                    <td><%= String.format("%.1f", s.getTotalHours()) %> hrs</td>
                                    <td>Rs. <%= String.format("%.2f", s.getTotalCharges()) %></td>
                                    <td><span class="status-badge available">COMPLETED</span></td>
                                    <td>
                                        <!-- View receipt link for completed sessions -->
                                        <a href="${pageContext.request.contextPath}/AdminServlet?action=downloadReceipt&sessionId=<%= s.getSessionId() %>"
                                           class="btn-primary" style="width:auto; padding:6px 12px; font-size:13px;">
                                            View Receipt
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
