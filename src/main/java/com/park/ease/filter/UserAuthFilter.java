package com.park.ease.filter;

import java.io.IOException;

import com.park.ease.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * UserAuthFilter is a servlet filter that intercepts all incoming requests
 * to enforce authentication and role-based authorization across the application.
 * 
 * Filter Logic:
 *   1. Public paths (login, register, assets, about, contact) pass through freely.
 *   2. Unknown paths (not mapped to any servlet) pass through for 404 handling.
 *   3. Unauthenticated users accessing protected paths are redirected to login.
 *   4. Authenticated users accessing wrong role paths are forwarded to unauthorized page.
 * 
 * URL Pattern: /* (intercepts all requests)
 */
@WebFilter("/*")
public class UserAuthFilter implements Filter {

    /**
     * Core filter method - evaluates each request against authentication
     * and authorization rules before allowing it to proceed.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Extract request path and action parameter for routing decisions
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String action = httpRequest.getParameter("action");

        // ==================== PUBLIC PATH DEFINITIONS ====================

        // Authentication pages - always accessible
        boolean isLoginPage = path.equals("/LoginServlet");
        boolean isLogoutPage = path.equals("/LogoutServlet");

        // Registration - only accessible when action is register or doRegister
        boolean isRegisterPage = path.equals("/UserServlet")
                && ("register".equals(action) || "doRegister".equals(action));

        // Landing page - publicly accessible
        boolean isLandingPage = path.equals("/") || path.equals("/index.jsp");

        // Static assets - CSS, JS, images always pass through
        boolean isAsset = path.startsWith("/assets/");

        // Error page - must be accessible without authentication for 404/500 handling
        boolean isErrorPage = path.equals("/WEB-INF/views/error.jsp");

        // Public info pages - About (GET), Contact (GET and POST for form submission)
        boolean isPublicPage = path.equals("/pages") && (
                "about".equals(action) ||
                "contact".equals(action) ||
                "POST".equalsIgnoreCase(httpRequest.getMethod())
        );

        // ==================== AUTHENTICATION CHECK ====================

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        // Allow all public paths to pass through without authentication
        if (isLoginPage || isLogoutPage || isRegisterPage || isLandingPage
                || isAsset || isPublicPage || isErrorPage) {
            chain.doFilter(request, response);
            return;
        }

        // For unauthenticated requests on unknown paths, allow through for 404 handling
        if (!isLoggedIn) {
            boolean isKnownPath = path.equals("/AdminServlet") || path.startsWith("/admin/")
                    || path.equals("/BookingServlet") || path.equals("/VehicleServlet")
                    || path.equals("/UserServlet") || path.equals("/SlotServlet")
                    || path.equals("/ZoneServlet") || path.equals("/ProfileServlet");

            if (!isKnownPath) {
                // Unknown path - let container handle 404 via web.xml error mapping
                chain.doFilter(request, response);
                return;
            }

            // Known protected path - redirect to login
            httpResponse.sendRedirect(contextPath + "/LoginServlet");
            return;
        }

        // ==================== AUTHORIZATION CHECK ====================

        // Retrieve authenticated user and their role for authorization
        User user = (User) session.getAttribute("user");
        String role = user.getRole();

        // Admin-only paths: dashboard, zone management, slot CRUD operations
        boolean isAdminPath = path.equals("/AdminServlet")
                || path.startsWith("/admin/")
                || path.equals("/ZoneServlet")
                || (path.equals("/SlotServlet")
                    && (action == null
                        || "list".equals(action)
                        || "addSlot".equals(action)
                        || "edit".equals(action)
                        || "delete".equals(action)
                        || "updateSlot".equals(action)));

        // User paths: profile, booking, vehicle management, slot search and wishlist
        boolean isUserPath = (path.equals("/UserServlet")
                    && !("register".equals(action) || "doRegister".equals(action)))
                || path.equals("/BookingServlet")
                || path.equals("/VehicleServlet")
                || path.equals("/ProfileServlet")
                || (path.equals("/SlotServlet")
                    && ("view".equals(action)
                        || "search".equals(action)
                        || "addToWishlist".equals(action)
                        || "removeFromWishlist".equals(action)));

        // Block non-admin users from accessing admin paths
        if (isAdminPath && !"ADMIN".equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/unauthorized.jsp")
                       .forward(httpRequest, httpResponse);
            return;
        }

        // Block users without valid role from accessing user paths
        if (isUserPath && !"USER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/unauthorized.jsp")
                       .forward(httpRequest, httpResponse);
            return;
        }

        // All checks passed - allow request to proceed
        chain.doFilter(request, response);
    }
}