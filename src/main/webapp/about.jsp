<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | System Profile & Technical Specifications</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page">

    <!-- =============================================
         CENTRAL WORKSPACE LAYOUT CONTAINER
         ============================================= -->
    <div class="profile-container">
        
        <!-- =============================================
             SECTION 1: SYSTEM OVERVIEW HEADER
             ============================================= -->
        <div class="profile-header">
            <h1 class="profile-title">
                Smart Parking Management Infrastructure Profile
            </h1>
            <p class="profile-description">
                ParkEase is a native web application built explicitly using the Java 21 platform specification, utilizing Jakarta EE Servlets and dynamic JavaServer Pages to manage core facility assets without high-level framework abstraction tools.
            </p>
        </div>

        <!-- =============================================
             SECTION 2: ARCHITECTURAL CONTENT AREA
             ============================================= -->
        <div class="profile-content-wrapper">
            
            <!-- LEFT COLUMN: PROBLEMS WE SOLVE -->
            <div class="profile-column">
                <h2 class="profile-section-title">
                    Problems We Solve
                </h2>
                <p class="profile-text">
                    Our system addresses the operational vulnerabilities found in traditional parking administration by eliminating paper ticket records and unsecured manual slot tracking. By implementing a thread-isolated database access interface layer, the platform prevents concurrent slot booking collisions, handles invalid state booking logic errors, and stops horizontal privilege security violations across user accounts via a custom authentication filter processing loop.
                </p>
            </div>

            <!-- RIGHT COLUMN: WHY CHOOSE US -->
            <div class="profile-column">
                <h2 class="profile-section-title">
                    Why choose us?
                </h2>
                <ul class="profile-list">
                    <li>Maximizes asset monetization by optimizing the utilization and turnover rates of available parking spaces.</li>
                    <li>Reduces facility administrative overhead costs by fully automating booking lifecycles and validation workflows.</li>
                    <li>Mitigates financial and compliance risks through robust data validation and parameterized security mechanisms.</li>
                    <li>Protects customer retention metrics by eliminating double-booking friction points and transaction delay limits.</li>
                    <li>Provides real-time business reporting data regarding zone performance metrics and facility monetization levels.</li>
                </ul>
            </div>

        </div>

        <!-- =============================================
             SECTION 3: SYSTEM MATRIX BOTTOM DISPLAY LAYER
             ============================================= -->
        <div class="profile-footer-section">
            <div class="profile-grid-wrapper">
                
                <!-- SUMMARY COLUMN -->
                <div class="profile-grid-item">
                    <div class="profile-brand">
                        <span class="profile-brand-name">ParkEase</span>
                        <span class="profile-brand-version">3D</span>
                    </div>
                    <p class="profile-brand-description">
                        ParkEase maps system data properties cleanly to specific zones, available slots, active user reservations, and live booking sessions without relying on heavy external runtime frameworks.
                    </p>
                    <div class="profile-social-links">
                        <span class="profile-social-icon" title="Facebook">FB</span>
                        <span class="profile-social-icon" title="WhatsApp">WA</span>
                        <span class="profile-social-icon" title="YouTube">YT</span>
                        <span class="profile-social-icon" title="LinkedIn">IN</span>
                    </div>
                </div>

                <!-- QUICK NAVIGATION COLUMN -->
                <div class="profile-grid-item">
                    <h4 class="profile-grid-title">Quick Links</h4>
                    <ul class="profile-link-list">
                        <li><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/pages?action=about" class="active-link">About Us</a></li>
                        <li><a href="${pageContext.request.contextPath}/pages?action=contact">Contact Us</a></li>
                    </ul>
                </div>

                <!-- CONTACT COLUMN -->
                <div class="profile-grid-item">
                    <h4 class="profile-grid-title">Get In Touch</h4>
                    <div class="profile-contact-info">
                        <p>
                            <strong>Email:</strong> support@parkease.com
                        </p>
                        <p>
                            <strong>Phone:</strong> +977 9824135234 PARK-EASE
                        </p>
                        <p class="profile-address">
                            <strong>Add:</strong> Building 1, Kamal Pokhari Park, No. 420, Messed Road, Kathmandu Town, Bagmati Province, Nepal
                        </p>
                    </div>
                </div>

            </div>
        </div>

        <!-- =============================================
             FOOTER SYSTEM MARKER
             ============================================= -->
        <div class="profile-footer">
            <p>&copy; 2026 ParkEase System. All Rights Reserved.</p>
        </div>

    </div>

</body>
</html>