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
        String pageAttr = request.getParameter("nextPage");
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

    /**
     * Calculates the total number of pages based on the total number of subjects and items per page.
     * 
     * This method:
     * 1. Validates the maxTake parameter to prevent division by zero
     * 2. Calculates the total pages using ceiling division
     * 3. Returns one less than the calculated total (0-indexed)
     * 4. Logs validation and calculation details
     * 
     * @param totalSubjects the total number of subjects to paginate
     * @param maxTake the maximum number of items per page
     * @return the total number of pages minus 1 (0-indexed), or 0 if calculation results in negative value
     */
    public static int calculateTotalPages(int totalSubjects, int maxTake) {
        if (maxTake <= 0) {
            LOGGER.log(Level.WARNING, "Invalid maxTake value: " + maxTake + ". Defaulting to 1 to avoid division by zero.");
            maxTake = 1; // Avoid division by zero
        }
        int totalPages = (int) Math.ceil((double) totalSubjects / maxTake);
        LOGGER.log(Level.FINE, "Total subjects: " + totalSubjects + ", Max take: " + maxTake + ", Total pages calculated: " + totalPages);
        return totalPages -1;
    }
}