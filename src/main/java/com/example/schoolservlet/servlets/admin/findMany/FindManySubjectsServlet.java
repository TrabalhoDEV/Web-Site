package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet for listing subjects with pagination in the admin panel.
 *
 * <p>This servlet handles HTTP GET requests to fetch and display a paginated list
 * of subjects. It validates admin access, reads the optional page parameter, and
 * calculates the total number of pages based on the total count of subjects.
 * The subject data for the current page is retrieved using SubjectDAO and set
 * as request attributes along with pagination info for rendering in the JSP view.
 * Any errors during data access are forwarded to the JSP view with the appropriate
 * HTTP status code.</p>
 *
 * @see HttpServlet
 * @see SubjectDAO
 * @see AccessValidation
 */
@WebServlet(name = "admin-subjects-find-many", value = "/admin/subject/find-many")
public class FindManySubjectsServlet extends HttpServlet {
    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        HttpSession session = request.getSession(false);
        String pageParam = request.getParameter("page");
        String nameFilter = request.getParameter("name");
        String error = (String) session.getAttribute("error");

        int take = Constants.MAX_TAKE;
        int page = 1;

        request.setAttribute("nameFilter", nameFilter != null ? nameFilter : "");

        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error");
        }

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            try {
                InputValidation.validateSubjectName(nameFilter);

                int totalCount = subjectDAO.count(nameFilter);
                int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

                page = Math.max(1, Math.min(page, totalPages));
                int skip = take * (page - 1);

                Map<Integer, Subject> subjectMap = subjectDAO.findMany(skip, take, nameFilter);

                request.setAttribute("subjectMap", subjectMap);
                request.setAttribute("page", page);
                request.setAttribute("totalPages", totalPages);

            } catch (RequiredFieldException rfe) {
                getDefaultData(request, page);
            } catch (ValidationException ve) {
                getDefaultData(request, page);
                ErrorHandler.forward(request, response, ve.getStatus(), ve.getMessage(),
                        "/WEB-INF/views/admin/findMany/subject.jsp");
                return;
            }
        } catch (DataException de) {
            ErrorHandler.forward(request, response, de.getStatus(), de.getMessage(),
                    "/WEB-INF/views/admin/findMany/subject.jsp");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/subject.jsp")
                .forward(request, response);
    }

    private void getDefaultData(HttpServletRequest request, int page) throws DataException {
        int take = Constants.MAX_TAKE;
        int totalCount = subjectDAO.totalCount();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

        page = Math.max(1, Math.min(page, totalPages));
        int skip = take * (page - 1);

        Map<Integer, Subject> subjectMap = subjectDAO.findMany(skip, take);

        request.setAttribute("subjectMap", subjectMap);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
}