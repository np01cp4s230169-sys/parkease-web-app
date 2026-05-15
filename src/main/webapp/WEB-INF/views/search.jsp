<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.park.ease.model.ParkingSlot, com.park.ease.model.Zone" %>
<%
    List<Zone> zoneList = (List<Zone>) request.getAttribute("zoneList");
    List<ParkingSlot> searchResults = (List<ParkingSlot>) request.getAttribute("searchResults");
    List<ParkingSlot> wishlistSlots = (List<ParkingSlot>) request.getAttribute("wishlistSlots");
    String selectedZoneId = (String) request.getAttribute("selectedZoneId");
    String selectedVehicleType = (String) request.getAttribute("selectedVehicleType");
    String slotNumber = (String) request.getAttribute("enteredSlotNumber");
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

        <main class="main-content">
            <header class="content-header">
                <h1>Search Available Parking Slots</h1>
            </header>

            <!-- MESSAGE BANNERS - Now using global alert classes -->
            <% if ("wishlist_added".equals(msg)) { %>
                <div class="alert-success">Slot added to wishlist. </div>
            <% } else if ("already_in_wishlist".equals(msg)) { %>
                <div class="alert-warning">Slot is already in wishlist. </div>
            <% } else if ("wishlist_removed".equals(msg)) { %>
                <div class="alert-success">Slot removed from wishlist. </div>
            <% } else if ("wishlist_error".equals(msg)) { %>
                <div class="alert-danger">Unable to update wishlist. </div>
            <% } %>

            <!-- SEARCH FORM - Fluid responsive -->
            <section class="stat-card search-section">
                <form action="${pageContext.request.contextPath}/SlotServlet" method="GET" class="profile-form search-form">
                    <input type="hidden" name="action" value="search">
                    
                    <div class="form-field-group">
                        <select name="zoneId" class="form-select">
                            <option value="">All Zones</option>
                            <% if (zoneList != null) {
                                for (Zone zone : zoneList) { %>
                                    <option value="<%= zone.getZoneId() %>"
                                        <%= String.valueOf(zone.getZoneId()).equals(selectedZoneId) ? "selected" : "" %>>
                                        <%= zone.getZoneName() %>
                                    </option>
                            <%      }
                               } %>
                        </select>

                        <select name="vehicleType" class="form-select">
                            <option value="">All Vehicle Types</option>
                            <option value="compact" <%= "compact".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Compact</option>
                            <option value="large" <%= "large".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Large</option>
                            <option value="electric" <%= "electric".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Electric</option>
                            <option value="motorcycle" <%= "motorcycle".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Motorcycle</option>
                        </select>

                        <input type="text" name="slotNumber" class="form-input" placeholder="Enter slot number"
                               value="<%= slotNumber != null ? slotNumber : "" %>">
                    </div>
                    
                    <div class="form-actions">
                        <button type="submit" class="btn-primary">🔍 Search</button>
                    </div>
                </form>
            </section>

            <!-- SEARCH RESULTS TABLE - Scroll protected -->
            <section class="management-section">
                <h2>Search Results</h2>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Slot ID</th>
                                <th>Zone ID</th>
                                <th>Slot Number</th>
                                <th>Vehicle Type</th>
                                <th>Hourly Rate</th>
                                <th>Status</th>
                                <th>Wishlist</th>
                                <th>Book</th> <!-- NEW COLUMN -->
                            </tr>
                        </thead>
                        <tbody>
                            <% if (searchResults != null && !searchResults.isEmpty()) {
                                for (ParkingSlot slot : searchResults) { 
                                    // Check if this slot is already in wishlist
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
                                    <td><%= slot.getZoneId() %></td>
                                    <td><%= slot.getSlotNumber() %></td>
                                    <td><%= slot.getVehicleType() %></td>
                                    <td>₹<%= slot.getHourlyRate() %></td>
                                    <td><span class="status-badge <%= slot.getStatus().toLowerCase() %>"><%= slot.getStatus() %></span></td>
                                    
                                    <!-- Wishlist Column -->
                                    <td class="table-btn-flexbox">
                                        <% if (alreadyInWishlist) { %>
                                            <button type="button" class="btn-secondary btn-small" disabled>✅ Added</button>
                                        <% } else { %>
                                            <form action="${pageContext.request.contextPath}/SlotServlet?action=addToWishlist" method="POST" class="wishlist-form">
                                                <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                                <input type="hidden" name="zoneId" value="<%= selectedZoneId != null ? selectedZoneId : "" %>">
                                                <input type="hidden" name="vehicleType" value="<%= selectedVehicleType != null ? selectedVehicleType : "" %>">
                                                <input type="hidden" name="slotNumber" value="<%= slotNumber != null ? slotNumber : "" %>">
                                                <button type="submit" class="btn-primary btn-small">❤️ Add</button>
                                            </form>
                                        <% } %>
                                    </td>
                                    
                                    <!-- NEW: Book Column -->
                                    <td class="table-btn-flexbox">
                                        <form action="${pageContext.request.contextPath}/BookingServlet" method="GET" style="margin: 0;">
                                            <input type="hidden" name="action" value="showQuickBooking">
                                            <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                            <button type="submit" class="btn-success btn-small">🚗 Book</button>
                                        </form>
                                    </td>
                                </tr>
                            <%      }
                                 } else if (searchResults != null) { %>
                                <tr>
                                    <td colspan="8" class="no-data">No matching available slots found. Try different filters.</td>
                                </tr>
                            <% } else { %>
                                <tr>
                                    <td colspan="8" class="no-data">Use the search form above to find available slots.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- WISHLIST TABLE - Scroll protected -->
            <section class="management-section">
                <h2>My Wishlist</h2>
                <div class="table-scroll-container">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Slot ID</th>
                                <th>Zone ID</th>
                                <th>Slot Number</th>
                                <th>Vehicle Type</th>
                                <th>Hourly Rate</th>
                                <th>Status</th>
                                <th>Book</th>   <!-- NEW COLUMN -->
                                <th>Remove</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (wishlistSlots != null && !wishlistSlots.isEmpty()) {
                                for (ParkingSlot slot : wishlistSlots) { %>
                                <tr>
                                    <td><%= slot.getSlotId() %></td>
                                    <td><%= slot.getZoneId() %></td>
                                    <td><%= slot.getSlotNumber() %></td>
                                    <td><%= slot.getVehicleType() %></td>
                                    <td>₹<%= slot.getHourlyRate() %></td>
                                    <td><span class="status-badge <%= slot.getStatus().toLowerCase() %>"><%= slot.getStatus() %></span></td>
                                    
                                    <!-- NEW: Book Column -->
                                    <td class="table-btn-flexbox">
                                        <form action="${pageContext.request.contextPath}/BookingServlet" method="GET" style="margin: 0;">
                                            <input type="hidden" name="action" value="showQuickBooking">
                                            <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                            <button type="submit" class="btn-success btn-small">🚗 Book</button>
                                        </form>
                                    </td>
                                    
                                    <!-- Remove Column -->
                                    <td class="table-btn-flexbox">
                                        <form action="${pageContext.request.contextPath}/SlotServlet?action=removeFromWishlist" method="POST" style="margin: 0;">
                                            <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                            <input type="hidden" name="zoneId" value="<%= selectedZoneId != null ? selectedZoneId : "" %>">
                                            <input type="hidden" name="vehicleType" value="<%= selectedVehicleType != null ? selectedVehicleType : "" %>">
                                            <input type="hidden" name="slotNumber" value="<%= slotNumber != null ? slotNumber : "" %>">
                                            <button type="submit" class="btn-danger btn-small">🗑️ Remove</button>
                                        </form>
                                    </td>
                                </tr>
                            <%      }
                                 } else { %>
                                <tr>
                                    <td colspan="8" class="no-data">No slots added to wishlist yet. Find slots above! ❤️</td>
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