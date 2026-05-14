<!-- PART OF admin_dashboard.jsp (Part 2: Slot + Inquiries integration) -->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.User" %>
<%@ page import="com.park.ease.model.Zone" %>
<%@ page import="com.park.ease.model.ParkingSlot" %>
<%@ page import="java.util.List, com.park.ease.model.Inquiry" %>
<%@ page import="java.util.Map" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
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
        <!-- Sidebar -->
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

        <!-- Main Content -->
        <main class="main-content">
            <header class="content-header">
                <h1>System Overview</h1>
                <div class="user-badge admin-tag">Master Admin</div>
            </header>

            <%
                String action = request.getParameter("action");
                String msg = request.getParameter("msg");
                String error = request.getParameter("error");

                Map<String, Integer> stats = (Map<String, Integer>) request.getAttribute("dashboardStats");
                double revenue = (request.getAttribute("totalRevenue") != null)
                        ? (Double) request.getAttribute("totalRevenue") : 0.0;

                Zone selectedZone = (Zone) request.getAttribute("selectedZone");
                ParkingSlot selectedSlot = (ParkingSlot) request.getAttribute("selectedSlot");

                List<Zone> zoneList = (List<Zone>) request.getAttribute("zoneList");
                List<ParkingSlot> slotList = (List<ParkingSlot>) request.getAttribute("slotList");
            %>

            <% if (msg != null) { %>
                <div class="alert alert-success">Action Completed!</div>
            <% } %>

            <% if (error != null) { %>
                <div class="alert alert-danger">Error: <%= error.replaceAll("<", "&lt;") %></div>
            <% } %>

            <!-- Stats Section -->
            <section class="stats-grid">
                <div class="stat-card">
                    <h3>Total Users</h3>
                    <p class="stat-number"><%= stats != null ? stats.getOrDefault("totalUsers", 0) : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Pending</h3>
                    <p class="stat-number" style="color:#e67e22;">
                        <%= stats != null ? stats.getOrDefault("pendingApprovals", 0) : 0 %>
                    </p>
                </div>
                <div class="stat-card">
                    <h3>Occupied</h3>
                    <p class="stat-number"><%= stats != null ? stats.getOrDefault("occupiedSlots", 0) : 0 %></p>
                </div>
                <div class="stat-card">
                    <h3>Revenue</h3>
                    <p class="stat-number">$<%= String.format("%.2f", revenue) %></p>
                </div>
            </section>

            <!-- ZONE MANAGEMENT SECTION -->
            <% if (("list".equals(action) || "edit".equals(action)) && zoneList != null && slotList == null) { %>
                <section class="management-section">
                    <div class="section-header">
                        <h2>Zone Management</h2>
                        <button class="btn-primary"
                                onclick="document.getElementById('zoneFormCard').style.display='block'">
                            + Add Zone
                        </button>
                    </div>

                    <!-- Zone Form -->
                    <div id="zoneFormCard" class="auth-card"
                         style="display:<%= (selectedZone != null ? "block" : "none") %>; margin-bottom: 20px; max-width: 100%;">
                        <h3><%= (selectedZone != null ? "Edit Zone" : "Create New Zone") %></h3>

                        <form action="${pageContext.request.contextPath}/ZoneServlet?action=<%= (selectedZone != null ? "update" : "create") %>" method="POST">
                            <% if (selectedZone != null) { %>
                                <input type="hidden" name="zoneId" value="<%= selectedZone.getZoneId() %>">
                            <% } %>

                            <div class="form-group">
                                <label>Zone Name</label>
                                <input type="text" name="zoneName" required
                                       value="<%= (selectedZone != null ? selectedZone.getZoneName() : "") %>">
                            </div>

                            <div class="form-group">
                                <label>Total Capacity</label>
                                <input type="number" name="capacity" required min="1"
                                       value="<%= (selectedZone != null ? selectedZone.getCapacity() : "") %>">
                            </div>

                            <div class="form-group">
                                <label>Description</label>
                                <textarea name="description"
                                          style="width:100%; border-radius:4px; border:1px solid #ddd; padding:0.5rem;"><%= (selectedZone != null && selectedZone.getDescription() != null ? selectedZone.getDescription() : "") %></textarea>
                            </div>

                            <button type="submit" class="btn-primary">
                                <%= (selectedZone != null ? "Update Zone" : "Save Zone") %>
                            </button>
                            <a href="${pageContext.request.contextPath}/ZoneServlet?action=list" class="btn-secondary">Cancel</a>
                        </form>
                    </div>

                    <!-- Zone Table -->
                    <table class="data-table">
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
                            <%
                                if (zoneList != null && !zoneList.isEmpty()) {
                                    for (Zone z : zoneList) {
                            %>
                            <tr>
                                <td><%= z.getZoneId() %></td>
                                <td><strong><%= z.getZoneName() %></strong></td>
                                <td><%= z.getCapacity() %> Slots</td>
                                <td><%= (z.getDescription() != null ? z.getDescription() : "-") %></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/ZoneServlet?action=edit&zoneId=<%= z.getZoneId() %>" class="btn-secondary">Edit</a>
                                    <a href="${pageContext.request.contextPath}/ZoneServlet?action=delete&zoneId=<%= z.getZoneId() %>"
                                       class="btn-secondary"
                                       onclick="return confirm('Delete this zone?');">Delete</a>
                                </td>
                            </tr>
                            <% 
                                    }
                                } else { 
                            %>
                            <tr>
                                <td colspan="5">No zones configured.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </section>
            <% } %>

            <!-- SLOT MANAGEMENT SECTION -->
            <% if (("list".equals(action) || "edit".equals(action)) && slotList != null) { %>
                <section class="management-section">
                    <div class="section-header">
                        <h2>Parking Slot Management</h2>
                        <button class="btn-primary"
                                onclick="document.getElementById('addSlotForm').style.display='block'"
                                style="width:auto;">
                            + Add New Slot
                        </button>
                    </div>

                    <!-- Slot Form -->
                    <div id="addSlotForm" class="auth-card"
                         style="display:<%= (selectedSlot != null ? "block" : "none") %>; margin: 20px 0; max-width: 100%; text-align: left;">
                        <h3><%= (selectedSlot != null ? "Edit Parking Slot" : "Create New Parking Slot") %></h3>

                        <form action="${pageContext.request.contextPath}/SlotServlet?action=<%= (selectedSlot != null ? "updateSlot" : "addSlot") %>" method="POST">
                            <% if (selectedSlot != null) { %>
                                <input type="hidden" name="slotId" value="<%= selectedSlot.getSlotId() %>">
                            <% } %>

                            <div class="form-group">
                                <label>Select Zone</label>
                                <select name="zoneId" required
                                        style="width:100%; padding:0.8rem; border-radius:4px; border:1px solid #ddd;">
                                    <option value="">-- Choose a Zone --</option>
                                    <%
                                        if (zoneList != null) {
                                            for (Zone z : zoneList) {
                                    %>
                                    <option value="<%= z.getZoneId() %>"
                                        <%= (selectedSlot != null && selectedSlot.getZoneId() == z.getZoneId()) ? "selected" : "" %>>
                                        <%= z.getZoneName() %>
                                    </option>
                                    <% 
                                            }
                                        } 
                                    %>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Slot Number</label>
                                <input type="text" name="slotNumber" required
                                       value="<%= (selectedSlot != null ? selectedSlot.getSlotNumber() : "") %>">
                            </div>

                            <div class="form-group">
                                <label>Vehicle Type</label>
                                <select name="vehicleType" required
                                        style="width:100%; padding:0.8rem; border-radius:4px; border:1px solid #ddd;">
                                    <option value="CAR" <%= (selectedSlot != null && "CAR".equalsIgnoreCase(selectedSlot.getVehicleType())) ? "selected" : "" %>>Car</option>
                                    <option value="BIKE" <%= (selectedSlot != null && "BIKE".equalsIgnoreCase(selectedSlot.getVehicleType())) ? "selected" : "" %>>Bike</option>
                                    <option value="TRUCK" <%= (selectedSlot != null && "TRUCK".equalsIgnoreCase(selectedSlot.getVehicleType())) ? "selected" : "" %>>Truck</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Hourly Rate ($)</label>
                                <input type="number" name="hourlyRate" step="0.01" required
                                       value="<%= (selectedSlot != null ? selectedSlot.getHourlyRate() : "") %>">
                            </div>

                            <div class="form-group">
                                <label>Status</label>
                                <select name="status" required
                                        style="width:100%; padding:0.8rem; border-radius:4px; border:1px solid #ddd;">
                                    <option value="available" <%= (selectedSlot == null || "available".equalsIgnoreCase(selectedSlot.getStatus())) ? "selected" : "" %>>Available</option>
                                    <option value="occupied" <%= (selectedSlot != null && "occupied".equalsIgnoreCase(selectedSlot.getStatus())) ? "selected" : "" %>>Occupied</option>
                                    <option value="maintenance" <%= (selectedSlot != null && "maintenance".equalsIgnoreCase(selectedSlot.getStatus())) ? "selected" : "" %>>Maintenance</option>
                                    <option value="reserved" <%= (selectedSlot != null && "reserved".equalsIgnoreCase(selectedSlot.getStatus())) ? "selected" : "" %>>Reserved</option>
                                </select>
                            </div>

                            <button type="submit" class="btn-primary">
                                <%= (selectedSlot != null ? "Update Slot" : "Save Slot") %>
                            </button>
                            <a href="${pageContext.request.contextPath}/SlotServlet?action=list" class="btn-secondary">Cancel</a>
                        </form>
                    </div>

                    <!-- Slot Table -->
                    <table class="data-table">
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
                            <%
                                if (slotList != null && !slotList.isEmpty()) {
                                    for (ParkingSlot s : slotList) {
                            %>
                            <tr>
                                <td><%= s.getSlotId() %></td>
                                <td><strong><%= s.getSlotNumber() %></strong></td>
                                <td>Zone <%= s.getZoneId() %></td>
                                <td><%= s.getVehicleType() %></td>
                                <td>$<%= String.format("%.2f", s.getHourlyRate()) %></td>
                                <td>
                                    <span class="status-pill <%= s.getStatus().toLowerCase() %>">
                                        <%= s.getStatus() %>
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/SlotServlet?action=edit&slotId=<%= s.getSlotId() %>" class="btn-secondary">Edit</a>
                                    <a href="${pageContext.request.contextPath}/SlotServlet?action=delete&slotId=<%= s.getSlotId() %>"
                                       class="btn-secondary"
                                       onclick="return confirm('Delete this slot?');">Delete</a>
                                </td>
                            </tr>
                            <% 
                                    }
                                } else { 
                            %>
                            <tr>
                                <td colspan="7">No slots found.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </section>
            <% } %>

            <!-- ADD THIS BLOCK: User Support Inquiries (System View Implementation) -->
            <div style="background: #ffffff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); padding: 30px; margin-top: 40px; box-sizing: border-box; text-align: left; display: block; clear: both;">
                <h2 style="font-size: 1.4rem; font-weight: 700; color: #1a202c; margin: 0 0 20px 0; border-left: 4px solid #dd6b20; padding-left: 10px; font-family: inherit;">
                    User Support Inquiries
                </h2>
                
                <% 
                    List<Inquiry> inquiries = (List<Inquiry>) request.getAttribute("inquiriesList");
                    if (inquiries == null || inquiries.isEmpty()) { 
                %>
                    <p style="color: #718096; font-style: italic; margin: 10px 0; font-size: 0.95rem;">No active customer support messages found in database records.</p>
                <% } else { %>
                    <div style="overflow-x: auto; border: 1px solid #edf2f7; border-radius: 6px;">
                        <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.95rem; background-color: #ffffff;">
                            <thead>
                                <tr style="background-color: #f7fafc; border-bottom: 2px solid #edf2f7; color: #4a5568; font-weight: 700;">
                                    <th style="padding: 12px 15px; font-family: inherit;">Submitted Timestamp</th>
                                    <th style="padding: 12px 15px; font-family: inherit;">User Name</th>
                                    <th style="padding: 12px 15px; font-family: inherit;">Email Address</th>
                                    <th style="padding: 12px 15px; font-family: inherit;">Inquiry Message Content</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Inquiry inq : inquiries) { %>
                                    <tr style="border-bottom: 1px solid #edf2f7; transition: background-color 0.2s;">
                                        <td style="padding: 12px 15px; font-size: 0.85rem; color: #718096; white-space: nowrap;"><%= inq.getSubmittedAt() %></td>
                                        <td style="padding: 12px 15px; font-weight: 600; color: #2d3748;"><%= inq.getName() %></td>
                                        <td style="padding: 12px 15px;"><a href="mailto:<%= inq.getEmail() %>" style="text-decoration: none; color: #3182ce; font-weight: 500;"><%= inq.getEmail() %></a></td>
                                        <td style="padding: 12px 15px; line-height: 1.5; color: #4a5568; max-width: 400px; word-wrap: break-word;"><%= inq.getMessage() %></td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>

        </main>
    </div>

</body>
</html>