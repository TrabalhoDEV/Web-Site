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
 * Servlet responsible for rendering student feedbacks.
 * Loads disciplines and observations of the authenticated student with pagination support.
 * 
 * @author Vertice
 * @version 1.0
 */
@WebServlet("/student/home")
public class FindFeedbacksServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FindFeedbacksServlet.class.getName());
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
        if (!AccessValidation.isStudent(request, response)) {
            LOGGER.log(Level.WARNING, "Access denied: user is not authenticated or lacks permissions");
            return;
        }

        try {
            // Extract next page number from request
            int page = PaginationUtilities.extractNextPage(request);
            
            // Update page attribute in request
            request.setAttribute("currentPage", page);
            
            // Load student feedbacks for the current page
            int totalAmountOfFeedbacks = loadStudentFeedbacks(request, page);

            // Set total pages attribute for pagination controls in the view
            request.setAttribute("totalPages", PaginationUtilities.calculateTotalPages(totalAmountOfFeedbacks, Constants.MAX_TAKE));
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing feedbacks request", e);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        }

        // Forward to view
        request.getRequestDispatcher("/WEB-INF/views/student/index.jsp").forward(request, response);
    }

    /**
     * Loads the feedbacks (observations) of the authenticated student.
     * Uses pagination according to application constants.
     *
     * @param request     the HTTP request
     * @param currentPage the page number to load
     * @return the total amount of feedbacks on the database for the authenticated student
     */
    private int loadStudentFeedbacks(HttpServletRequest request, int currentPage) {
        HttpSession session = request.getSession();
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) session.getAttribute("user");

        // Validate authenticated user exists in session
        if (authenticatedUser == null) {
            LOGGER.log(Level.WARNING, "Authenticated user not found in session");
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
            return 0;
        }

        LOGGER.log(Level.FINE, "Loading feedbacks for user ID: " + authenticatedUser.id());

        try {
            // Calculate offset based on page number and page size
            int offset = currentPage * Constants.MAX_TAKE;
            
            // Query database for student's subjects and observations
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
            Map<Integer, StudentSubject> studentSubjectMap = studentSubjectDAO.findMany(
                    offset,
                    Constants.MAX_TAKE,
                    authenticatedUser.id()
            );

            // Extract non-empty observations and store list in request for view rendering
            request.setAttribute("observationsList",
                    studentSubjectMap.values().stream()
                            .map(StudentSubject::getObs)
                            .filter(obs -> obs != null && !obs.isBlank())
                            .toList()

            );
            LOGGER.log(Level.INFO, "Feedbacks loaded successfully.");

            return studentSubjectDAO.totalCount(authenticatedUser.id());

        } catch (NullPointerException npe) {
            LOGGER.log(Level.SEVERE, "Error loading feedbacks - unexpected null value", npe);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading feedbacks", e);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        }
        return 0;
    }
}
