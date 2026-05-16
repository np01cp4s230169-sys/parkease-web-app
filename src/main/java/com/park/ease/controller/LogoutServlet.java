package com.park.ease.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * LogoutServlet handles user logout by invalidating the current session,
 * deleting the Remember Me cookie if present, and redirecting to the home page.
 *
 * Supports both GET and POST requests to handle logout from
 * different parts of the application (links and forms).
 *
 * URL Pattern: /LogoutServlet
 */
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** Cookie name — must match the constant defined in LoginServlet. */
    private static final String REMEMBER_ME_COOKIE = "rememberedEmail";

    /**
     * Processes logout by deleting the Remember Me cookie,
     * invalidating the user session, and redirecting to the home page.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            /* Delete the Remember Me cookie if it exists in the browser */
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (REMEMBER_ME_COOKIE.equals(cookie.getName())) {
                        Cookie deleteCookie = new Cookie(REMEMBER_ME_COOKIE, "");
                        deleteCookie.setMaxAge(0); // age 0 instructs browser to delete
                        deleteCookie.setPath("/");
                        response.addCookie(deleteCookie);
                        break;
                    }
                }
            }

            /* Invalidate session to clear all user data and attributes */
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            /* Redirect to home page after successful logout */
            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    /**
     * Delegates POST logout requests to doGet handler.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}