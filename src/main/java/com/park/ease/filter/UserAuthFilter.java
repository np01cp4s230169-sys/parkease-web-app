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

@WebFilter("/*")
public class UserAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String action = httpRequest.getParameter("action");

        boolean isLoginPage = path.equals("/LoginServlet");
        boolean isLogoutPage = path.equals("/LogoutServlet");
        boolean isRegisterPage = path.equals("/UserServlet")
                && ("register".equals(action) || "doRegister".equals(action));
        boolean isLandingPage = path.equals("/") || path.equals("/index.jsp");
        boolean isAsset = path.startsWith("/assets/");
        
        // CRITICAL UPDATE: Whitelists page view requests (GET) and form processing runs (POST)
        boolean isPublicPage = path.equals("/pages") && (
                "about".equals(action) || 
                "contact".equals(action) || 
                "POST".equalsIgnoreCase(httpRequest.getMethod())
        );

        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        // Include isPublicPage inside your non-authenticated permission group check
        if (isLoginPage || isLogoutPage || isRegisterPage || isLandingPage || isAsset || isPublicPage) {
            chain.doFilter(request, response);
            return;
        }

        if (!isLoggedIn) {
            httpResponse.sendRedirect(contextPath + "/LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole();

        // Admin verification logic path map
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

        // User verification logic path map
        boolean isUserPath = (path.equals("/UserServlet")
                    && !("register".equals(action) || "doRegister".equals(action)))
                || path.equals("/BookingServlet")
                || path.equals("/VehicleServlet")
                || (path.equals("/SlotServlet")
                    && ("view".equals(action)
                        || "search".equals(action)
                        || "addToWishlist".equals(action)
                        || "removeFromWishlist".equals(action)));

        if (isAdminPath && !"ADMIN".equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/unauthorized.jsp")
                       .forward(httpRequest, httpResponse);
            return;
        }

        if (isUserPath && !"USER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role)) {
            httpRequest.getRequestDispatcher("/WEB-INF/views/unauthorized.jsp")
                       .forward(httpRequest, httpResponse);
            return;
        }

        chain.doFilter(request, response);
    }
}
