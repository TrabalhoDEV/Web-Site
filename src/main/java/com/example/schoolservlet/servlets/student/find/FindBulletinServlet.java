package com.example.schoolservlet.servlets.student.find;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.PaginationUtilities;
import com.example.schoolservlet.utils.records.AuthenticatedUser;

import com.example.schoolservlet.utils.records.StudentsPerformanceCount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isStudent(request, response)) {
            LOGGER.log(Level.WARNING, "Access denied: user is not authenticated or lacks permissions");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            LOGGER.log(Level.SEVERE, "Session not found for authenticated user");
            response.sendRedirect("index.jsp");
            return;
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) session.getAttribute("user");
        if (authenticatedUser == null) {
            LOGGER.log(Level.SEVERE, "Authenticated user not found in session");
            response.sendRedirect("index.jsp");
            return;
        }

        // Paginação igual ao FindManyStudentsServlet
        String pageParam = request.getParameter("page");
        int page = 1;
        int take = Constants.MAX_TAKE;

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

            int totalSubjects = studentSubjectDAO.totalCount(authenticatedUser.id());
            int totalPages = Math.max(1, (int) Math.ceil((double) totalSubjects / take));

            page = Math.max(1, Math.min(page, totalPages));
            int skip = take * (page - 1);

            Map<Integer, StudentSubject> studentSubjectMap = studentSubjectDAO.findMany(skip, take, authenticatedUser.id());
            StudentsPerformanceCount performanceCount = studentSubjectDAO.studentPerformanceCount(authenticatedUser.id());

            if (studentSubjectMap == null) {
                LOGGER.log(Level.WARNING, "Student subject map is null for student ID: " + authenticatedUser.id());
                studentSubjectMap = Map.of();
            }

            request.setAttribute("studentSubjectMap", studentSubjectMap);
            request.setAttribute("performanceCount", performanceCount);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);

            request.getRequestDispatcher("/WEB-INF/views/student/bulletin.jsp").forward(request, response);

        } catch (DataException | ValidationException e) {
            LOGGER.log(Level.SEVERE, "Data access error while fetching student subjects for student ID: " + authenticatedUser.id(), e);
            response.setStatus(e.getStatus());
            treatUnexpectedError(request, response, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in bulletin servlet", e);
            treatUnexpectedError(request, response, "Ocorreu um erro ao buscar boletim");
        }
    }

    private void treatUnexpectedError(HttpServletRequest request, HttpServletResponse response, String error) {
        request.setAttribute("error", Constants.UNEXPECTED_ERROR_MESSAGE);
        request.setAttribute("studentSubjectMap", Map.of());
        request.setAttribute("page", 1);
        request.setAttribute("totalPages", 1);

        try {
            ErrorHandler.forward(request, response, response.getStatus(), error, "/WEB-INF/views/student/bulletin.jsp");
        } catch (ServletException | IOException forwardException) {
            LOGGER.log(Level.SEVERE, "Failed to forward to bulletin page", forwardException);
        }
    }
}