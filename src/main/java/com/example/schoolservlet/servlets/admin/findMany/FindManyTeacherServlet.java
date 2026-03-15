package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.ErrorHandler;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Servlet responsible for listing multiple teachers in the system with pagination.
 * This endpoint is only accessible by administrators.
 */
@WebServlet(name = "admin-find-many-teachers", value = "/admin/teacher/find-many")
public class FindManyTeacherServlet extends HttpServlet {
    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final String responsePath = "/WEB-INF/views/admin/findMany/teacher.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        HttpSession session = request.getSession(false);
        String pageParam = request.getParameter("page");
        String filter    = request.getParameter("filter");

        int take = Constants.MAX_TAKE;
        int page = 1;

        request.setAttribute("filter", filter != null ? filter : "");

        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            int totalCount = teacherDAO.count(filter);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

            page = Math.max(1, Math.min(page, totalPages));
            int skip = take * (page - 1);

            Map<Integer, Teacher> teacherMap = teacherDAO.findMany(skip, take, filter);

            request.setAttribute("teacherMap", teacherMap);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
        } catch (DataException de) {
            ErrorHandler.forward(request, response, de.getStatus(), de.getMessage(), responsePath);
            return;
        }

        request.getRequestDispatcher(responsePath).forward(request, response);
    }
}