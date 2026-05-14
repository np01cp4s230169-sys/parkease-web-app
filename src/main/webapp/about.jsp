<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ParkEase | System Profile & Technical Specifications</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="auth-page" style="margin: 0; padding: 0;">

    <!-- CENTRAL WORKSPACE LAYOUT CONTAINER -->
    <div style="max-width: 1200px; margin: 0 auto; padding: 60px 20px 20px 20px; box-sizing: border-box;">
        
        <!-- SECTION 1: SYSTEM OVERVIEW SPECIFICATION HEADER -->
        <div style="margin-bottom: 50px; text-align: left; display: block; clear: both;">
            <h1 style="font-size: 2.2rem; margin: 0 0 15px 0; font-weight: 700;">
                Smart Parking Management Infrastructure Profile
            </h1>
            <p style="font-size: 1.1rem; line-height: 1.6; margin: 0; max-width: 1100px;">
                ParkEase is a native web application built explicitly using the Java 21 platform specification, utilizing Jakarta EE Servlets and dynamic JavaServer Pages to manage core facility assets without high-level framework abstraction tools.
            </p>
        </div>

        <!-- SECTION 2: ARCHITECTURAL CONTENT AREA ALIGNED REALISTICALLY WITH THE PARKEASE CODEBASE -->
        <div style="margin-bottom: 60px; display: block; clear: both;">
            
            <!-- LEFT AREA: PROBLEMS WE SOLVE -->
            <div style="width: 48%; float: left; margin-right: 2%; box-sizing: border-box; min-width: 320px; margin-bottom: 40px;">
                <h2 style="font-size: 1.6rem; margin: 0 0 20px 0; font-weight: 700;">
                    Problems We Solve
                </h2>
                <p style="font-size: 1rem; line-height: 1.7; margin: 0; text-align: justify;">
                    Our system addresses the operational vulnerabilities found in traditional parking administration by eliminating paper ticket records and unsecured manual slot tracking. By implementing a thread-isolated database access interface layer, the platform prevents concurrent slot booking collisions, handles invalid state booking logic errors, and stops horizontal privilege security violations across user accounts via a custom authentication filter processing loop.
                </p>
            </div>

            <!-- RIGHT AREA: WHY CHOOSE US (REWRITTEN FROM A STRATEGIC BUSINESS PERSPECTIVE) -->
            <div style="width: 48%; float: right; box-sizing: border-box; min-width: 320px; margin-bottom: 40px;">
                <h2 style="font-size: 1.6rem; margin: 0 0 20px 0; font-weight: 700;">
                    Why choose us?
                </h2>
                <ul style="margin: 0; padding: 0 0 0 20px; font-size: 1rem; line-height: 1.8;">
                    <li style="margin-bottom: 10px;">Maximizes asset monetization by optimizing the utilization and turnover rates of available parking spaces.</li>
                    <li style="margin-bottom: 10px;">Reduces facility administrative overhead costs by fully automating booking lifecycles and validation workflows.</li>
                    <li style="margin-bottom: 10px;">Mitigates financial and compliance risks through robust data validation and parameterized security mechanisms.</li>
                    <li style="margin-bottom: 10px;">Protects customer retention metrics by eliminating double-booking friction points and transaction delay limits.</li>
                    <li style="margin-bottom: 10px;">Provides real-time business reporting data regarding zone performance metrics and facility monetization levels.</li>
                </ul>
            </div>

        </div>

        <!-- SECTION 3: SYSTEM MATRIX BOTTOM DISPLAY LAYER -->
        <div style="border-top: 1px solid #e2e8f0; padding-top: 50px; margin-top: 50px; display: block; clear: both;">
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 40px; text-align: left; align-items: start;">
                
                <!-- SUMMARY COLUMN -->
                <div style="box-sizing: border-box;">
                    <div style="display: flex; align-items: center; margin-bottom: 15px;">
                        <span style="font-size: 1.5rem; font-weight: 800; letter-spacing: -0.5px;">ParkEase</span>
                        <span style="font-size: 0.6rem; font-weight: 700; vertical-align: super; margin-left: 2px;">3D</span>
                    </div>
                    <p style="font-size: 0.95rem; line-height: 1.6; margin: 0 0 20px 0;">
                        ParkEase maps system data properties cleanly to specific zones, available slots, active user reservations, and live booking sessions without relying on heavy external runtime frameworks.
                    </p>
                    <div style="display: flex; gap: 15px; font-size: 1.1rem; font-weight: bold;">
                        <span style="cursor: pointer;" title="Facebook"></span>
                        <span style="cursor: pointer;" title="WhatsApp"></span>
                        <span style="cursor: pointer;" title="YouTube"></span>
                        <span style="cursor: pointer;" title="LinkedIn"></span>
                    </div>
                </div>

                <!-- QUICK NAVIGATION COLUMN -->
                <div style="box-sizing: border-box; padding-left: 20px;">
                    <h4 style="font-size: 1.1rem; margin: 0 0 20px 0; font-weight: 700;">Quick Links</h4>
                    <ul style="list-style: none; padding: 0; margin: 0; font-size: 0.95rem; line-height: 2.2;">
                        <li><a href="${pageContext.request.contextPath}/index.jsp" style="text-decoration: none;">Home</a></li>
                        <li><a href="${pageContext.request.contextPath}/pages?action=about" style="text-decoration: none; font-weight: 600;">About Us</a></li>
                        <li><a href="${pageContext.request.contextPath}/pages?action=contact" style="text-decoration: none;">Contact Us</a></li>
                    </ul>
                </div>

                <!-- CONTACT COLUMN -->
                <div style="box-sizing: border-box;">
                    <h4 style="font-size: 1.1rem; margin: 0 0 20px 0; font-weight: 700;">Get In Touch</h4>
                    <p style="font-size: 0.95rem; margin: 0 0 12px 0; line-height: 1.5;">
                        <strong>Email:</strong> support@parkease.com
                    </p>
                    <p style="font-size: 0.95rem; margin: 0 0 12px 0; line-height: 1.5;">
                        <strong>Phone:</strong> +977 9824135234 PARK-EASE
                    </p>
                    <p style="font-size: 0.95rem; margin: 0; line-height: 1.6; text-align: justify;">
                        <strong>Add:</strong> Building 1, Kamal Pokhari Park, No. 420, Messed Road, Kathmandu Town, Bagmati Province, Nepal
                    </p>
                </div>

            </div>
        </div>

        <!-- FOOTER SYSTEM MARKER -->
        <div style="text-align: center; border-top: 1px solid #edf2f7; padding-top: 20px; margin-top: 40px; font-size: 0.85rem; display: block; clear: both;">
            <p>&copy; 2026 ParkEase System. All Rights Reserved.</p>
        </div>

    </div>

</body>
</html>
