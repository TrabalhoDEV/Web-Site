package com.example.schoolservlet.utils;

import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for common operations across the application.
 * Provides authentication and authorization validation methods.
 * 
 * @author School System
 * @version 1.0
 */
public class Utils {
    
    private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

    /**
     * Validates if the user is authenticated and has the required role.
     * If authentication fails, either redirects or forwards to the login page.
     *
     * @param request   the HTTP request object
     * @param response  the HTTP response object
     * @param userRole  the required user role for access
     * @param loginPath the path to redirect/forward to if authentication fails
     * @param redirect  true to redirect, false to forward to login page
     * @return true if user is authenticated with correct role, false otherwise
     * @throws IOException      if an I/O error occurs during redirect
     * @throws ServletException if a servlet error occurs during forward
     */
    public static boolean isAuthenticated(
            HttpServletRequest request, 
            HttpServletResponse response, 
            UserRoleEnum userRole, 
            String loginPath, 
            boolean redirect) throws IOException, ServletException {
        
        // Retrieve authenticated user from session
        AuthenticatedUser authenticatedUser = getAuthenticatedUserFromSession(request);
        
        // Validate user exists and has required role
        if (isUserAuthorized(authenticatedUser, userRole)) {
            LOGGER.log(Level.FINE, "User authenticated and authorized with role: " + userRole);
            return true;
        }

        // Log authentication failure
        LOGGER.log(Level.WARNING, "Authentication failed: user not found or insufficient permissions for role: " + userRole);
        
        // Set error message in request
        request.setAttribute("error", Constants.EXPIRED_SESSION_MESSAGE);
        
        // Handle redirect or forward based on configuration
        if (redirect) {
            // Redirect user to login page (browser makes new request)
            response.sendRedirect(request.getContextPath() + loginPath);
        } else {
            // Forward user to login page (server-side forwarding)
            request.getRequestDispatcher(loginPath).forward(request, response);
        }
        
        return false;
    }

    /**
     * Retrieves the authenticated user from the current HTTP session.
     * Safely handles null session or missing user attribute.
     *
     * @param request the HTTP request object
     * @return the AuthenticatedUser if present in session, null otherwise
     */
    private static AuthenticatedUser getAuthenticatedUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        // Return null if session doesn't exist
        if (session == null) {
            LOGGER.log(Level.FINE, "No session found for request");
            return null;
        }
        
        // Safely retrieve and cast user from session
        Object userAttribute = session.getAttribute("user");
        if (userAttribute instanceof AuthenticatedUser) {
            return (AuthenticatedUser) userAttribute;
        }
        
        LOGGER.log(Level.FINE, "User attribute not found or invalid type in session");
        return null;
    }

    /**
     * Validates if the user is authorized for the required role.
     * Performs null-safe comparison of user roles.
     *
     * @param authenticatedUser the user to validate (may be null)
     * @param requiredRole      the required role for access
     * @return true if user exists and matches required role, false otherwise
     */
    private static boolean isUserAuthorized(AuthenticatedUser authenticatedUser, UserRoleEnum requiredRole) {
        return authenticatedUser != null && authenticatedUser.role() == requiredRole;
    }
}