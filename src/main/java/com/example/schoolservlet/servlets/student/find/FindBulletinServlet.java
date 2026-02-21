package com.example.schoolservlet.servlets.student.find;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.PaginationUtilities;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet responsible for rendering the student bulletin board with grades and subjects.
 * 
 * This servlet handles GET requests to display paginated student academic records
 * including enrolled subjects and their corresponding grades. Access is restricted
 * to authenticated students only.
 * 
 * @author Vertice
 * @version 1.0
 */
@WebServlet("/student/bulletin")
public class FindBulletinServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FindBulletinServlet.class.getName());

    /**
     * Handles GET requests to render the student bulletin board.
     * 
     * This method:
     * 1. Validates that the user is authenticated and has student role
     * 2. Retrieves the authenticated student's information from session
     * 3. Extracts pagination parameters from the request
     * 4. Fetches the student's subjects and grades with pagination
     * 5. Forwards the data to the bulletin view for rendering
     * 
     * @param request the HTTP servlet request containing pagination parameters
     * @param response the HTTP servlet response to send back to the client
     * @throws ServletException if servlet processing fails
     * @throws IOException if an input/output error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Authentication validation: ensure user is logged in and has student role
        if (!AccessValidation.isStudent(request, response)) {
            LOGGER.log(Level.WARNING, "Access denied: user is not authenticated or lacks permissions");
            return;
        }

        // Retrieve authenticated student information from session
        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.log(Level.SEVERE, "Session not found for authenticated user");
            response.sendRedirect("/WEB-INF/index.jsp");
            return;
        }
        
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) session.getAttribute("user");
        if (authenticatedUser == null) {
            LOGGER.log(Level.SEVERE, "Authenticated user not found in session");
            response.sendRedirect("/WEB-INF/index.jsp");
            return;
        }

        // Calculate pagination: extract requested page and calculate database skip
        int page = PaginationUtilities.extractNextPage(request);
        int skip = page * Constants.MAX_TAKE;

        try {
            // Fetch the total amount of pages
            int totalSubjects = new StudentSubjectDAO().totalCount(authenticatedUser.id());
            int totalPages = PaginationUtilities.calculateTotalPages(totalSubjects, Constants.MAX_TAKE);


            // Fetch student's subjects and grades from database with pagination
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
            Map<Integer, StudentSubject> studentSubjectMap = null;
            
            try {
                studentSubjectMap = studentSubjectDAO.findMany(
                        skip,
                        Constants.MAX_TAKE,
                        authenticatedUser.id()
                );
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Database error while fetching student subjects for student ID: " + authenticatedUser.id(), e);
                request.setAttribute("error", "Unable to load bulletin data. Please try again later.");
                studentSubjectMap = Map.of();
            }

            // Validate returned data
            if (studentSubjectMap == null) {
                LOGGER.log(Level.WARNING, "Student subject map is null for student ID: " + authenticatedUser.id());
                studentSubjectMap = Map.of();
            }

            // Prepare request attributes for view rendering
            request.setAttribute("studentSubjectMap", studentSubjectMap);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            // Forward to bulletin view template for display
            request.getRequestDispatcher("/WEB-INF/views/student/bulletin.jsp").forward(request, response);
            
        } catch (ServletException | IOException e) {
            LOGGER.log(Level.SEVERE, "Error processing bulletin request for student ID: " + authenticatedUser.id(), e);
            treatUnexpectedError(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in bulletin servlet", e);
            treatUnexpectedError(request, response);
        }
    }

    /**
     * Handles unexpected errors by forwarding to the bulletin page with an error message.
     * 
     * This method:
     * 1. Sets an error message in the request attributes
     * 2. Initializes empty collections for the student subject map
     * 3. Resets pagination to the first page
     * 4. Forwards the request to the bulletin view for error display
     * 5. Logs any exceptions that occur during the forward operation
     * 
     * @param request the HTTP servlet request to set error attributes on
     * @param response the HTTP servlet response for the forward operation
     */
    private void treatUnexpectedError(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        request.setAttribute("studentSubjectMap", Map.of());
        request.setAttribute("currentPage", 0);
        try {
            request.getRequestDispatcher("/WEB-INF/views/student/bulletin.jsp").forward(request, response);
        } catch (ServletException | IOException forwardException) {
            LOGGER.log(Level.SEVERE, "Failed to forward to bulletin page", forwardException);
        }
    }
}