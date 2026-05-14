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

            <% if ("wishlist_added".equals(msg)) { %>
                <p style="color: green; font-weight: bold;">Slot added to wishlist.</p>
            <% } else if ("already_in_wishlist".equals(msg)) { %>
                <p style="color: orange; font-weight: bold;">Slot is already in wishlist.</p>
            <% } else if ("wishlist_removed".equals(msg)) { %>
                <p style="color: green; font-weight: bold;">Slot removed from wishlist.</p>
            <% } else if ("wishlist_error".equals(msg)) { %>
                <p style="color: red; font-weight: bold;">Unable to update wishlist.</p>
            <% } %>

            <section class="stat-card">
                <form action="${pageContext.request.contextPath}/SlotServlet" method="GET" class="profile-form">
                    <input type="hidden" name="action" value="search">

                    <select name="zoneId">
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

                    <select name="vehicleType">
                        <option value="">All Vehicle Types</option>
                        <option value="CAR" <%= "CAR".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Car</option>
                        <option value="BIKE" <%= "BIKE".equalsIgnoreCase(selectedVehicleType) ? "selected" : "" %>>Bike</option>
                    </select>

                    <input type="text" name="slotNumber" placeholder="Enter slot number"
                           value="<%= slotNumber != null ? slotNumber : "" %>">

                    <button type="submit" class="btn-primary">Search</button>
                </form>
            </section>

            <section class="management-section" style="margin-top: 2rem;">
                <h2>Search Results</h2>
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
                        </tr>
                    </thead>
                    <tbody>
                        <% if (searchResults != null && !searchResults.isEmpty()) {
                            for (ParkingSlot slot : searchResults) { %>
                            <tr>
                                <td><%= slot.getSlotId() %></td>
                                <td><%= slot.getZoneId() %></td>
                                <td><%= slot.getSlotNumber() %></td>
                                <td><%= slot.getVehicleType() %></td>
                                <td><%= slot.getHourlyRate() %></td>
                                <td><%= slot.getStatus() %></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/SlotServlet?action=addToWishlist" method="POST">
                                        <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                        <input type="hidden" name="zoneId" value="<%= selectedZoneId != null ? selectedZoneId : "" %>">
                                        <input type="hidden" name="vehicleType" value="<%= selectedVehicleType != null ? selectedVehicleType : "" %>">
                                        <input type="hidden" name="slotNumber" value="<%= slotNumber != null ? slotNumber : "" %>">
                                        <button type="submit" class="btn-primary">Add to Wishlist</button>
                                    </form>
                                </td>
                            </tr>
                        <%      }
                           } else if (searchResults != null) { %>
                            <tr>
                                <td colspan="7">No matching available slots found.</td>
                            </tr>
                        <% } else { %>
                            <tr>
                                <td colspan="7">Use the search form above to find available slots.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </section>

            <section class="management-section" style="margin-top: 2rem;">
                <h2>My Wishlist</h2>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Slot ID</th>
                            <th>Zone ID</th>
                            <th>Slot Number</th>
                            <th>Vehicle Type</th>
                            <th>Hourly Rate</th>
                            <th>Status</th>
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
                                <td><%= slot.getHourlyRate() %></td>
                                <td><%= slot.getStatus() %></td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/SlotServlet?action=removeFromWishlist" method="POST">
                                        <input type="hidden" name="slotId" value="<%= slot.getSlotId() %>">
                                        <input type="hidden" name="zoneId" value="<%= selectedZoneId != null ? selectedZoneId : "" %>">
                                        <input type="hidden" name="vehicleType" value="<%= selectedVehicleType != null ? selectedVehicleType : "" %>">
                                        <input type="hidden" name="slotNumber" value="<%= slotNumber != null ? slotNumber : "" %>">
                                        <button type="submit" class="btn-primary" style="background:#c0392b;">Remove</button>
                                    </form>
                                </td>
                            </tr>
                        <%      }
                           } else { %>
                            <tr>
                                <td colspan="7">No slots added to wishlist yet.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</body>
</html>