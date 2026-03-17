package com.example.schoolservlet.servlets.student.find;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AccessValidation.isStudent(request, response)) {
            LOGGER.log(Level.WARNING, "Access denied: user is not authenticated or lacks permissions");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) session.getAttribute("user");
        if (authenticatedUser == null) {
            LOGGER.log(Level.WARNING, "Authenticated user not found in session");
            response.sendRedirect("index.jsp");
            return;
        }

        String pageParam = request.getParameter("page");
        int page = 1;
        int take = Constants.MAX_TAKE;

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

            int totalFeedbacks = studentSubjectDAO.countObs(authenticatedUser.id());
            int totalPages = Math.max(1, (int) Math.ceil((double) totalFeedbacks / take));

            page = Math.max(1, Math.min(page, totalPages));
            int skip = take * (page - 1);

            Map<Integer, StudentSubject> studentSubjectMap = studentSubjectDAO.findManyThatHasFeedbacks(
                    skip,
                    take,
                    authenticatedUser.id()
            );

            request.setAttribute("studentSubjectMap", studentSubjectMap);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            LOGGER.log(Level.INFO, "Feedbacks loaded successfully.");

        } catch (DataException de) {
            LOGGER.log(Level.SEVERE, "Error processing feedbacks request: ", de);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading feedbacks", e);
            request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        }

        request.getRequestDispatcher("/WEB-INF/views/student/index.jsp").forward(request, response);
    }
}