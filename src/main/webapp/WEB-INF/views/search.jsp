<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.park.ease.model.ParkingSlot, com.park.ease.model.Zone, com.park.ease.model.User" %>
<%
    /* Security check: Redirect to login if user session is not active */
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Load search data from request attributes set by SlotServlet */
    List<Zone> zoneList = (List<Zone>) request.getAttribute("zoneList");
    List<ParkingSlot> searchResults = (List<ParkingSlot>) request.getAttribute("searchResults");
    List<ParkingSlot> wishlistSlots = (List<ParkingSlot>) request.getAttribute("wishlistSlots");
    String selectedZoneId = (String) request.getAttribute("selectedZoneId");
    String selectedVehicleType = (String) request.getAttribute("selectedVehicleType");
    String slotNumber = (String) request.getAttribute("enteredSlotNumber");

    /* Wishlist messages preserved in query param by SlotServlet redirect */
    String msg = request.getParameter("msg");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Search Slots</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
    <div class="dashboard-container">

        <!-- Sidebar navigation for user portal -->
        <aside class="sidebar">
            <div class="sidebar-header"><h2>ParkEase</h2></div>
            <nav class="sidebar-nav">
                <a href="${pageContext.request.contextPath}/UserServlet?action=dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/SlotServlet?action=search" class="active">Search Slots</a>
                <a href="${pageContext.request.contextPath}/BookingServlet?action=myBookings">My Bookings</a>
                <a href="${pageContext.request.contextPath}/UserServlet?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-link">Logout</a>
            </nav>
        </aside>

        <!-- Main content area -->
        <main class="main-content">
            <header class="content-header">
                <h1>Search Available Parking Slots</h1>
                <div class="user-badge"><%= currentUser.getRole() %></div>
            </header>

            <!-- Wishlist action messages -->
            <% if ("wishlist_added".equals(msg)) { %>
                <div class="alert-success">Slot added to your wishlist successfully.</div>
            <% } else if ("already_in_wishlist".equals(msg)) { %>
                <div class="alert-warning">This slot is already in your wishlist.</div>
            <% } else if ("wishlist_removed".equals(msg)) { %>
                <div class="alert-success">Slot removed from your wishlist.</div>
            <% } else if ("wishlist_error".equals(msg)) { %>
                <div class="alert-danger">Unable to update wishlist. Please try again.</div>
            <% } %>

            <!-- Slot search filter form -->
            <section class="stat-card" style="margin-bottom: 1.5rem;">
                <h3>Search Filters</h3>
                <form action="${pageContext.request.contextPath}/SlotServlet" method="GET" class="profile-form">
                    <input type="hidden" name="action" value="search">

                    <div class="form-group">
                        <label>Zone</label>
                        <select name="zoneId" class="form-input">
                            <option value="">All Zones</option>
                            <% if (zoneList != null) {
                                for (Zone zone : zoneList) { %>
                                <option value="<%= zone.getZoneId() %>"
                                    <%= String.valueOf(zone.getZoneId()).equals(selectedZoneId) ? "selected" : "" %>>
                                    <%= zone.getZoneName() %>
                                </option>
                            <% }} %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Vehicle Type</label>
                        <select name="vehicleType" class="form-input">
                            <option value="">All Vehicle Types</option>
                            <option value="CAR" <%= "CAR".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Car</option>
                            <option value="BIKE" <%= "BIKE".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Bike</option>
                            <option value="TRUCK" <%= "TRUCK".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Truck</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Slot Number</label>
                        <input type="text" name="slotNumber" class="form-input"
                               placeholder="Enter slot number (optional)"
                               value="<%= slotNumber != null ? slotNumber : "" %>">
                    </div>

                    <button type="submit" class="btn-primary" style="width:auto; padding: 0.7rem 2rem;">Search</button>
                </form>
            </section>

            <!-- Search results table -->
            <section class="management-section">
                <h2>Search Results</h2>
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr>
                                <th>Slot ID</th>
                                <th>Zone</th>
                                <th>Slot Number</th>
                                <th>Vehicle Type</th>
                                <th>Hourly Rate</th>
                                <th>Status</th>
                                <th>Wishlist</th>
                                <th>Book</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (searchResults != null && !searchResults.isEmpty()) {
                                for (ParkingSlot slot : searchResults) {

                                    /* Check if this slot is already in the user's wishlist */
                                    boolean alreadyInWishlist = false;
                                    if (wishlistSlots != null) {
                                        for (ParkingSlot wishSlot : wishlistSlots) {
                                            if (wishSlot.getSlotId() == slot.getSlotId()) {
                                                alreadyInWishlist = true;
                                                break;
                                            }
                                        }
                                    }
                            %>
                                <tr>
                                    <td><%= slot.getSlotId() %></td>
                                    <td>Zone <%= slot.getZoneId() %></td>
                                    <td><strong><%= slot.getSlotNumber() %></strong></td>
                                    <td><%= slot.getVehicleType() %></td>
                                    <td>Rs. <%= String.format("%.2f", slot.getHourlyRate()) %></td>
                                    <td><span class="status-badge <%= slot.getStatus().toLowerCase() %>"><%= slot.getStatus() %></span></td>

                                    <!-- Wishlist action -->
                                    <td>
                                        <% if (alreadyInWishlist) { %>
                                            <button type="button" class="btn-secondary btn-small" disabled>Added</button>
                                        <% } else { %>
                                            <form action="${pageContext.request.contextPath}/SlotServlet" method="POST" style="margin:0;">
                                                <input type="hidden" name="action" value="addToWishlist">
                                                <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                                <input type="hidden" name="zoneId" value="<%= selectedZoneId != null ? selectedZoneId : "" %>">
                                                <input type="hidden" name="vehicleType" value="<%= selectedVehicleType != null ? selectedVehicleType : "" %>">
                                                <input type="hidden" name="slotNumber" value="<%= slotNumber != null ? slotNumber : "" %>">
                                                <button type="submit" class="btn-primary btn-small">Add to Wishlist</button>
                                            </form>
                                        <% } %>
                                    </td>

                                    <!-- Quick booking action -->
                                    <td>
                                        <form action="${pageContext.request.contextPath}/BookingServlet" method="GET" style="margin:0;">
                                            <input type="hidden" name="action" value="showQuickBooking">
                                            <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                            <button type="submit" class="btn-success btn-small">Book Now</button>
                                        </form>
                                    </td>
                                </tr>
                            <% }
                            } else if (searchResults != null) { %>
                                <tr>
                                    <td colspan="8" class="no-data">No matching available slots found. Try different filters.</td>
                                </tr>
                            <% } else { %>
                                <tr>
                                    <td colspan="8" class="no-data">Use the search form above to find available parking slots.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- Wishlist table -->
            <section class="management-section">
                <h2>My Wishlist</h2>
                <div class="table-scroll-container">
                    <table class="responsive-data-table">
                        <thead>
                            <tr>
                                <th>Slot ID</th>
                                <th>Zone</th>
                                <th>Slot Number</th>
                                <th>Vehicle Type</th>
                                <th>Hourly Rate</th>
                                <th>Status</th>
                                <th>Book</th>
                                <th>Remove</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (wishlistSlots != null && !wishlistSlots.isEmpty()) {
                                for (ParkingSlot slot : wishlistSlots) { %>
                                <tr>
                                    <td><%= slot.getSlotId() %></td>
                                    <td>Zone <%= slot.getZoneId() %></td>
                                    <td><strong><%= slot.getSlotNumber() %></strong></td>
                                    <td><%= slot.getVehicleType() %></td>
                                    <td>Rs. <%= String.format("%.2f", slot.getHourlyRate()) %></td>
                                    <td><span class="status-badge <%= slot.getStatus().toLowerCase() %>"><%= slot.getStatus() %></span></td>

                                    <!-- Book from wishlist -->
                                    <td>
                                        <form action="${pageContext.request.contextPath}/BookingServlet" method="GET" style="margin:0;">
                                            <input type="hidden" name="action" value="showQuickBooking">
                                            <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                            <button type="submit" class="btn-success btn-small">Book Now</button>
                                        </form>
                                    </td>

                                    <!-- Remove from wishlist -->
                                    <td>
                                        <form action="${pageContext.request.contextPath}/SlotServlet" method="POST" style="margin:0;">
                                            <input type="hidden" name="action" value="removeFromWishlist">
                                            <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                            <input type="hidden" name="zoneId" value="<%= selectedZoneId != null ? selectedZoneId : "" %>">
                                            <input type="hidden" name="vehicleType" value="<%= selectedVehicleType != null ? selectedVehicleType : "" %>">
                                            <input type="hidden" name="slotNumber" value="<%= slotNumber != null ? slotNumber : "" %>">
                                            <button type="submit" class="btn-danger btn-small">Remove</button>
                                        </form>
                                    </td>
                                </tr>
                            <% }
                            } else { %>
                                <tr>
                                    <td colspan="8" class="no-data">No slots added to wishlist yet.</td>
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
