<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.List, java.util.Map" %>
<%
    /* Admin and Users are only allowed */
    User user = (User) session.getAttribute("user");
    if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    String action = request.getParameter("action");

    /* Reads the session messages and clears after display */
    String successMsg = (String) session.getAttribute("successMsg");
    String errorMsg = (String) session.getAttribute("errorMsg");
    if (successMsg != null) session.removeAttribute("successMsg");
    if (errorMsg != null) session.removeAttribute("errorMsg");

    /* Loads the data from the request attribute */
    Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("dashboardStats");
    double revenue = (request.getAttribute("totalRevenue") != null)
            ? (Double) request.getAttribute("totalRevenue") : 0.0;

    Zone selectedZone = (Zone) request.getAttribute("selectedZone");
    ParkingSlot selectedSlot = (ParkingSlot) request.getAttribute("selectedSlot");

    List<Zone> zoneList = (List<Zone>) request.getAttribute("zoneList");
    List<ParkingSlot> slotList = (List<ParkingSlot>) request.getAttribute("slotList");
    List<Inquiry> inquiries = (List<Inquiry>) request.getAttribute("inquiriesList");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">

        <!-- Navigation for Sidebar -->
        <aside class="sidebar admin-sidebar">
            <div class="sidebar-header">
                <h2>Admin Portal</h2>
            </div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/AdminServlet?action=dashboard" class="active">Dashboard</a>
                <a href="${pageContext.request.contextPath}/AdminServlet?action=manageUsers">Approve Users</a>
                <a href="${pageContext.request.contextPath}/ZoneServlet?action=list">Manage Zones</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=list">Manage Slots</a>
                <a href="${pageContext.request.contextPath}/AdminServlet?action=reports">View Reports</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content -->
        <main class="main-content">
            <header class="content-header">
                <h1>System Overview</h1>
                <div class="user-badge admin-tag">Master Admin</div>
            </header>

            <!-- Message for success and error alerts from session -->
            <% if (successMsg != null) { %>
                <div class="alert-success"><%= successMsg %></div>
            <% } %>
            <% if (errorMsg != null) { %>
                <div class="alert-danger"><%= errorMsg %></div>
            <% } %>

            <!-- DASHBOARD STATISTICS CARDS -->
            <section class="stats-grid">
                <div class="stat-card">
                    <h3>Pending Approvals</h3>
                    <p class="stat-number"><%= stats != null ? stats.getOrDefault("pendingApprovals", 0) : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Available Slots</h3>
                    <p class="stat-number"><%= stats != null ? stats.getOrDefault("availableSlots", 0) : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Occupied Slots</h3>
                    <p class="stat-number"><%= stats != null ? stats.getOrDefault("occupiedSlots", 0) : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Total Revenue</h3>
                    <p class="stat-number">Rs. <%= String.format("%.2f", revenue) %></p>
                </div>
            </section>

            <!-- ZONE MANAGEMENT SECTION -->
            <% if (("list".equals(action) || "edit".equals(action) || "add".equals(action)) && zoneList != null && slotList == null) { %>
                <section class="management-section">
                    <div class="section-header">
                        <h2>Zone Management</h2>
                        <a href="${pageContext.request.contextPath}/ZoneServlet?action=add" class="btn-primary" style="width:auto; padding: 0.6rem 1.5rem;">Add Zone</a>
                    </div>

                    <!-- ZONE ADD/EDIT FORM -->
                    <% if (selectedZone != null || "add".equals(action)) { %>
                        <div class="stat-card" style="margin-bottom: 1.5rem;">
                            <h3><%= selectedZone != null ? "Edit Zone" : "Create New Zone" %></h3>
                            <form action="${pageContext.request.contextPath}/ZoneServlet" method="POST" class="profile-form">
                                <input type="hidden" name="action" value="<%= selectedZone != null ? "update" : "create" %>">
                                <% if (selectedZone != null) { %>
                                    <input type="hidden" name="zoneId" value="<%= selectedZone.getZoneId() %>">
                                <% } %>

                                <div class="form-group">
                                    <label>Zone Name</label>
                                    <input type="text" name="zoneName" class="form-input" required
                                           value="<%= selectedZone != null ? selectedZone.getZoneName() : "" %>">
                                </div>

                                <div class="form-group">
                                    <label>Total Capacity</label>
                                    <input type="number" name="capacity" class="form-input" required min="1"
                                           value="<%= selectedZone != null ? selectedZone.getCapacity() : "" %>">
                                </div>

                                <div class="form-group">
                                    <label>Description</label>
                                    <textarea name="description" class="form-input"><%= selectedZone != null && selectedZone.getDescription() != null ? selectedZone.getDescription() : "" %></textarea>
                                </div>

                                <button type="submit" class="btn-primary" style="width:auto; padding: 0.7rem 1.5rem;">
                                    <%= selectedZone != null ? "Update Zone" : "Save Zone" %>
                                </button>
                                <a href="${pageContext.request.contextPath}/ZoneServlet?action=list" class="btn-secondary" style="padding: 0.7rem 1.5rem;">Cancel</a>
                            </form>
                        </div>
                    <% } %>

                    <!-- ZONE LIST TABLE -->
                    <div class="table-scroll-container">
                        <table class="responsive-data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Capacity</th>
                                    <th>Description</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (zoneList != null && !zoneList.isEmpty()) {
                                    for (Zone z : zoneList) { %>
                                    <tr>
                                        <td><%= z.getZoneId() %></td>
                                        <td><strong><%= z.getZoneName() %></strong></td>
                                        <td><%= z.getCapacity() %> Slots</td>
                                        <td><%= z.getDescription() != null ? z.getDescription() : "-" %></td>
                                        <td>
                                            <div class="table-btn-flexbox">
                                                <a href="${pageContext.request.contextPath}/ZoneServlet?action=edit&zoneId=<%= z.getZoneId() %>" class="btn-primary" style="width:auto; padding:6px 12px; font-size:13px;">Edit</a>
                                                <a href="${pageContext.request.contextPath}/ZoneServlet?action=delete&zoneId=<%= z.getZoneId() %>" class="btn-danger" onclick="return confirm('Are you sure you want to delete this zone?');" style="padding:6px 12px; font-size:13px;">Delete</a>
                                            </div>
                                        </td>
                                    </tr>
                                <% }
                                } else { %>
                                    <tr><td colspan="5" class="no-data">No zones configured yet.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </section>
            <% } %>

            <!-- SLOT MANAGEMENT SECTION -->
            <% if (("list".equals(action) || "edit".equals(action) || "add".equals(action)) && slotList != null) { %>
                <section class="management-section">
                    <div class="section-header">
                        <h2>Parking Slot Management</h2>
                        <a href="${pageContext.request.contextPath}/SlotServlet?action=add" class="btn-primary" style="width:auto; padding: 0.6rem 1.5rem;">Add Slot</a>
                    </div>

                    <!-- SLOT ADD/EDIT FORM -->
                    <% if (selectedSlot != null || "add".equals(action)) { %>
                        <div class="stat-card" style="margin-bottom: 1.5rem;">
                            <h3><%= selectedSlot != null ? "Edit Parking Slot" : "Create New Parking Slot" %></h3>
                            <form action="${pageContext.request.contextPath}/SlotServlet" method="POST" class="profile-form">
                                <input type="hidden" name="action" value="<%= selectedSlot != null ? "updateSlot" : "addSlot" %>">
                                <% if (selectedSlot != null) { %>
                                    <input type="hidden" name="slotId" value="<%= selectedSlot.getSlotId() %>">
                                <% } %>

                                <div class="form-group">
                                    <label>Select Zone</label>
                                    <select name="zoneId" class="form-input" required>
                                        <option value="">-- Choose a Zone --</option>
                                        <% if (zoneList != null) {
                                            for (Zone z : zoneList) { %>
                                            <option value="<%= z.getZoneId() %>"
                                                <%= selectedSlot != null && selectedSlot.getZoneId() == z.getZoneId() ? "selected" : "" %>>
                                                <%= z.getZoneName() %>
                                            </option>
                                        <% }} %>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>Slot Number</label>
                                    <input type="text" name="slotNumber" class="form-input" required
                                           value="<%= selectedSlot != null ? selectedSlot.getSlotNumber() : "" %>">
                                </div>

                                <div class="form-group">
                                    <label>Vehicle Type</label>
                                    <select name="vehicleType" class="form-input" required>
                                        <option value="CAR" <%= selectedSlot != null && "CAR".equalsIgnoreCase(selectedSlot.getVehicleType()) ? "selected" : "" %>>Car</option>
                                        <option value="BIKE" <%= selectedSlot != null && "BIKE".equalsIgnoreCase(selectedSlot.getVehicleType()) ? "selected" : "" %>>Bike</option>
                                        <option value="TRUCK" <%= selectedSlot != null && "TRUCK".equalsIgnoreCase(selectedSlot.getVehicleType()) ? "selected" : "" %>>Truck</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>Hourly Rate (Rs.)</label>
                                    <input type="number" name="hourlyRate" class="form-input" step="0.01" required min="0"
                                           value="<%= selectedSlot != null ? selectedSlot.getHourlyRate() : "" %>">
                                </div>

                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="status" class="form-input" required>
                                        <option value="available" <%= selectedSlot == null || "available".equalsIgnoreCase(selectedSlot.getStatus()) ? "selected" : "" %>>Available</option>
                                        <option value="occupied" <%= selectedSlot != null && "occupied".equalsIgnoreCase(selectedSlot.getStatus()) ? "selected" : "" %>>Occupied</option>
                                        <option value="maintenance" <%= selectedSlot != null && "maintenance".equalsIgnoreCase(selectedSlot.getStatus()) ? "selected" : "" %>>Maintenance</option>
                                    </select>
                                </div>

                                <button type="submit" class="btn-primary" style="width:auto; padding: 0.7rem 1.5rem;">
                                    <%= selectedSlot != null ? "Update Slot" : "Save Slot" %>
                                </button>
                                <a href="${pageContext.request.contextPath}/SlotServlet?action=list" class="btn-secondary" style="padding: 0.7rem 1.5rem;">Cancel</a>
                            </form>
                        </div>
                    <% } %>

                    <!-- SLOT LIST TABLE -->
                    <div class="table-scroll-container">
                        <table class="responsive-data-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Slot #</th>
                                    <th>Zone</th>
                                    <th>Type</th>
                                    <th>Rate</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (slotList != null && !slotList.isEmpty()) {
                                    for (ParkingSlot s : slotList) { %>
                                    <tr>
                                        <td><%= s.getSlotId() %></td>
                                        <td><strong><%= s.getSlotNumber() %></strong></td>
                                        <td>Zone <%= s.getZoneId() %></td>
                                        <td><%= s.getVehicleType() %></td>
                                        <td>Rs. <%= String.format("%.2f", s.getHourlyRate()) %></td>
                                        <td><span class="status-badge <%= s.getStatus().toLowerCase() %>"><%= s.getStatus() %></span></td>
                                        <td>
                                            <div class="table-btn-flexbox">
                                                <a href="${pageContext.request.contextPath}/SlotServlet?action=edit&slotId=<%= s.getSlotId() %>" class="btn-primary" style="width:auto; padding:6px 12px; font-size:13px;">Edit</a>
                                                <a href="${pageContext.request.contextPath}/SlotServlet?action=delete&slotId=<%= s.getSlotId() %>" class="btn-danger" onclick="return confirm('Are you sure you want to delete this slot?');" style="padding:6px 12px; font-size:13px;">Delete</a>
                                            </div>
                                        </td>
                                    </tr>
                                <% }
                                } else { %>
                                    <tr><td colspan="7" class="no-data">No parking slots found.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </section>
            <% } %>

            <!-- USER SUPPORT INQUIRIES SECTION -->
            <% if (inquiries != null) { %>
            <section class="management-section">
                <h2>User Support Inquiries</h2>
                <% if (inquiries.isEmpty()) { %>
                    <p class="no-data">No support inquiries submitted yet.</p>
                <% } else { %>
                    <div class="table-scroll-container">
                        <table class="responsive-data-table">
                            <thead>
                                <tr>
                                    <th>Submitted</th>
                                    <th>Name</th>
                                    <th>Email</th>
                                    <th>Message</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Inquiry inq : inquiries) { %>
                                    <tr>
                                        <td><%= inq.getSubmittedAt() %></td>
                                        <td><%= inq.getName() %></td>
                                        <td><a href="mailto:<%= inq.getEmail() %>"><%= inq.getEmail() %></a></td>
                                        <td><%= inq.getMessage() %></td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </section>
            <% } %>

        </main>
    </div>
</body>
</html>
