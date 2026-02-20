package com.example.schoolservlet.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for common operations across the application.
 * Provides authentication and authorization validation methods.
 * 
 * @author Vertice
 * @version 1.0
 */
public class PaginationUtilities {
    
    private static final Logger LOGGER = Logger.getLogger(PaginationUtilities.class.getName());

    /**
     * Extracts the next page number from request attributes.
     * Returns 0 (first page) if not defined or invalid.
     *
     * @param request the HTTP request
     * @return next page number (minimum 0)
     */
    public static int extractNextPage(HttpServletRequest request) {
        String pageAttr = (String) request.getParameter("nextPage");
        int page = Constants.MIN_PAGE;

        if (pageAttr != null && !pageAttr.isBlank()) {
            try {
                page = Integer.parseInt(pageAttr);
                // Ensure page number is never negative
                page = Math.max(page, Constants.MIN_PAGE);
                LOGGER.log(Level.FINE, "Page extracted from attribute: " + page);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid page value: " + pageAttr + ". Using default page.");
                page = Constants.MIN_PAGE;
            }
        } else {
            LOGGER.log(Level.FINE, "No page attribute found. Using default page: " + Constants.MIN_PAGE);
        }

        return page;
    }
}