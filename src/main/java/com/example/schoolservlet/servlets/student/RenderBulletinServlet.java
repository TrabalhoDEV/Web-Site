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
 * @author School Servlet System
 * @version 1.0
 */
@WebServlet("/student/bulletin")
public class RenderBulletinServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RenderBulletinServlet.class.getName());

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
        if (!Utils.isAuthenticated(request, response, UserRoleEnum.STUDENT, "/WEB-INF/index.jsp", false)) {
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

        // Calculate pagination: extract requested page and calculate database offset
        int page = Utils.extractNextPage(request);
        int offset = page * Constants.DEFAULT_TAKE;

        // Fetch student's subjects and grades from database with pagination
        StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
        Map<Integer, StudentSubject> studentSubjectMap = studentSubjectDAO.findMany(
                offset,
                Constants.DEFAULT_TAKE,
                authenticatedUser.id()
        );

        // Prepare request attributes for view rendering
        request.setAttribute("studentSubjectMap", studentSubjectMap);
        request.setAttribute("currentPage", page);

        // Forward to bulletin view template for display
        request.getRequestDispatcher("/WEB-INF/views/student/bulletin.jsp").forward(request, response);
    }
}