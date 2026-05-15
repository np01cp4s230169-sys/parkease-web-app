<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.park.ease.model.*, java.util.Date" %>
<%
    /* Security check: Restrict access to logged in users only */
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }

    /* Load parking session data from request attribute set by AdminServlet */
    ParkingSession receipt = (ParkingSession) request.getAttribute("receipt");
    if (receipt == null) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?action=reports");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | Parking Receipt</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .receipt-container {
            max-width: 500px;
            margin: 40px auto;
            padding: 20px;
        }
        .receipt-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            padding: 2rem;
        }
        .receipt-header {
            text-align: center;
            padding-bottom: 1.5rem;
            border-bottom: 2px dashed #eee;
            margin-bottom: 1.5rem;
        }
        .receipt-header h1 {
            font-size: 1.8rem;
            color: #2c3e50;
            margin-bottom: 0.3rem;
        }
        .receipt-header p {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        .receipt-row {
            display: flex;
            justify-content: space-between;
            padding: 0.6rem 0;
            border-bottom: 1px solid #f0f0f0;
            font-size: 0.95rem;
        }
        .receipt-row:last-of-type {
            border-bottom: none;
        }
        .receipt-row .label {
            color: #7f8c8d;
            font-weight: 500;
        }
        .receipt-row .value {
            color: #2c3e50;
            font-weight: 600;
        }
        .receipt-total {
            margin-top: 1.5rem;
            padding-top: 1rem;
            border-top: 2px solid #2c3e50;
            display: flex;
            justify-content: space-between;
            font-size: 1.2rem;
            font-weight: 700;
            color: #2c3e50;
        }
        .receipt-footer {
            text-align: center;
            margin-top: 1.5rem;
            padding-top: 1rem;
            border-top: 2px dashed #eee;
            color: #7f8c8d;
            font-size: 0.85rem;
        }
        .receipt-actions {
            display: flex;
            gap: 10px;
            margin-top: 1.5rem;
            justify-content: center;
        }
        @media print {
            .receipt-actions { display: none; }
            .receipt-container { margin: 0; padding: 0; }
        }
    </style>
</head>
<body style="background-color: #f4f7f6; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">

    <div class="receipt-container">
        <div class="receipt-card">

            <!-- Receipt header with branding -->
            <div class="receipt-header">
                <h1>ParkEase</h1>
                <p>Parking Management System</p>
                <p>Official Parking Receipt</p>
            </div>

            <!-- Session details -->
            <div class="receipt-row">
                <span class="label">Receipt No.</span>
                <span class="value">#<%= receipt.getSessionId() %></span>
            </div>
            <div class="receipt-row">
                <span class="label">Slot ID</span>
                <span class="value"><%= receipt.getSlotId() %></span>
            </div>
            <div class="receipt-row">
                <span class="label">Vehicle ID</span>
                <span class="value"><%= receipt.getVehicleId() %></span>
            </div>
            <div class="receipt-row">
                <span class="label">Entry Time</span>
                <span class="value"><%= receipt.getEntryTime() %></span>
            </div>
            <div class="receipt-row">
                <span class="label">Exit Time</span>
                <span class="value"><%= receipt.getExitTime() %></span>
            </div>
            <div class="receipt-row">
                <span class="label">Duration</span>
                <span class="value"><%= String.format("%.1f", receipt.getTotalHours()) %> hours</span>
            </div>
            <div class="receipt-row">
                <span class="label">Payment Status</span>
                <span class="value"><%= receipt.getPaymentStatus().toUpperCase() %></span>
            </div>

            <!-- Total charges -->
            <div class="receipt-total">
                <span>Total Amount</span>
                <span>Rs. <%= String.format("%.2f", receipt.getTotalCharges()) %></span>
            </div>

            <!-- Receipt footer -->
            <div class="receipt-footer">
                <p>Thank you for using ParkEase.</p>
                <p>Generated on: <%= new java.util.Date() %></p>
            </div>

            <!-- Action buttons -->
            <div class="receipt-actions">
                <button onclick="window.print()" class="btn-primary" style="width:auto; padding: 0.7rem 1.5rem;">
                    Print Receipt
                </button>
                <a href="javascript:history.back()" class="btn-secondary" style="padding: 0.7rem 1.5rem;">
                    Go Back
                </a>
            </div>

        </div>
    </div>

</body>
</html>
