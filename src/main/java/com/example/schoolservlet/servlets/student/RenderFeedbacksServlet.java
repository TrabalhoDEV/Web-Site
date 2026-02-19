package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.Utils;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsible for rendering student feedbacks.
 * Loads disciplines and observations of the authenticated student with pagination support.
 * 
 * @author School System
 * @version 1.0
 */
@WebServlet("/student/home")
public class RenderFeedbacksServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RenderFeedbacksServlet.class.getName());
    private static final int MIN_PAGE = 0;

    /**
     * Processes GET requests to load and display student feedbacks.
     * Implements pagination and authenticated user validation.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Validate user authentication and authorization
        if (!Utils.isAuthenticated(request, response, UserRoleEnum.STUDENT, "/WEB-INF/index.jsp", false)) {
            LOGGER.log(Level.WARNING, "Access denied: user is not authenticated or lacks permissions");
            return;
        }

        try {
            // Extract current page number from request
            int currentPage = extractCurrentPage(request);
            
            // Process page navigation if requested
            int pageDirection = extractPageDirection(request);
            currentPage = calculateNewPage(currentPage, pageDirection);
            
            // Update page attribute in request
            request.setAttribute("page", currentPage);
            
            // Load student feedbacks for the current page
            loadStudentFeedbacks(request, currentPage);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing feedbacks request", e);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        }

        // Forward to view
        request.getRequestDispatcher("/WEB-INF/views/student/index.jsp").forward(request, response);
    }

    /**
     * Extracts the current page number from request attributes.
     * Returns 0 (first page) if not defined or invalid.
     *
     * @param request the HTTP request
     * @return current page number (minimum 0)
     */
    private int extractCurrentPage(HttpServletRequest request) {
        String pageAttr = (String) request.getAttribute("page");
        int page = MIN_PAGE;

        if (pageAttr != null && !pageAttr.isBlank()) {
            try {
                page = Integer.parseInt(pageAttr);
                // Ensure page number is never negative
                page = Math.max(page, MIN_PAGE);
                LOGGER.log(Level.FINE, "Page extracted from attribute: " + page);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid page value: " + pageAttr + ". Using default page.");
                page = MIN_PAGE;
            }
        }

        return page;
    }

    /**
     * Extracts the pagination navigation direction from request parameters.
     * Returns 1 (next), -1 (previous) or 0 (no change).
     *
     * @param request the HTTP request
     * @return navigation direction: 1, -1 or 0
     */
    private int extractPageDirection(HttpServletRequest request) {
        String pageDirectionParam = request.getParameter("pageDirection");
        
        if (pageDirectionParam != null && !pageDirectionParam.isBlank()) {
            try {
                int direction = Integer.parseInt(pageDirectionParam);
                if (direction == 1 || direction == -1) {
                    return direction;
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid page direction value: " + pageDirectionParam);
            }
        }
        
        return 0;
    }

    /**
     * Calculates the new page number based on navigation direction.
     * Ensures page number never becomes negative.
     *
     * @param currentPage   the current page number
     * @param pageDirection navigation direction (1, -1 or 0)
     * @return new page number
     */
    private int calculateNewPage(int currentPage, int pageDirection) {
        int newPage = currentPage + pageDirection;
        newPage = Math.max(newPage, MIN_PAGE);
        
        if (pageDirection != 0) {
            LOGGER.log(Level.FINE, "Page updated from " + currentPage + " to " + newPage);
        }
        
        return newPage;
    }

    /**
     * Loads the feedbacks (observations) of the authenticated student.
     * Uses pagination according to application constants.
     *
     * @param request     the HTTP request
     * @param currentPage the page number to load
     */
    private void loadStudentFeedbacks(HttpServletRequest request, int currentPage) {
        HttpSession session = request.getSession();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) session.getAttribute("user");

        // Validate authenticated user exists in session
        if (authenticatedUser == null) {
            LOGGER.log(Level.WARNING, "Authenticated user not found in session");
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
            return;
        }

        LOGGER.log(Level.FINE, "Loading feedbacks for user ID: " + authenticatedUser.id());

        try {
            // Calculate offset based on page number and page size
            int offset = currentPage * Constants.DEFAULT_TAKE;
            
            // Query database for student's subjects and observations
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
            Map<Integer, StudentSubject> studentSubjectMap = studentSubjectDAO.findMany(
                    offset,
                    Constants.DEFAULT_TAKE,
                    authenticatedUser.id()
            );

            // Extract non-empty observations into a list for display
            List<String> observationsList = new ArrayList<>();
            for (StudentSubject studentSubject : studentSubjectMap.values()) {
                String observation = studentSubject.getObs();
                if (observation != null && !observation.isBlank()) {
                    observationsList.add(observation);
                }
            }

            // Store observations list in request for view rendering
            request.setAttribute("observationsList", observationsList);
            LOGGER.log(Level.INFO, "Feedbacks loaded successfully. Count: " + observationsList.size());

        } catch (NullPointerException npe) {
            LOGGER.log(Level.SEVERE, "Error loading feedbacks - unexpected null value", npe);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading feedbacks from database", e);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        }
    }
}
