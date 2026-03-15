package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.SchoolClassTeacherDAO;
import com.example.schoolservlet.daos.SubjectTeacherDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.ErrorHandler;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * Servlet responsible for listing teachers linked to a specific school class with pagination.
 * This endpoint is only accessible by administrators.
 */
@WebServlet(name = "admin-find-many-teachers-by-class", value = "/admin/school-class/teacher/find-many")
public class FindManyTeacherByClassServlet extends HttpServlet {
    private final SchoolClassTeacherDAO schoolClassTeacherDAO = new SchoolClassTeacherDAO();
    private final String responsePath = "/WEB-INF/views/admin/findMany/school-class-teachers.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        HttpSession session = request.getSession(false);
        String pageParam     = request.getParameter("page");
        String filter        = request.getParameter("filter");
        String classIdParam  = request.getParameter("classId");

        int take = Constants.MAX_TAKE;
        int page = 1;

        request.setAttribute("filter", filter != null ? filter : "");

        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        // Valida o id da turma
        if (classIdParam == null || classIdParam.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            return;
        }

        int schoolClassId;
        try {
            schoolClassId = Integer.parseInt(classIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            return;
        }

        request.setAttribute("classId", schoolClassId);

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException ignored) {}

        try {
            int totalCount = schoolClassTeacherDAO.countByClass(schoolClassId, filter);
            int totalPages = Math.max(1, (int) Math.ceil((double) totalCount / take));

            page = Math.max(1, Math.min(page, totalPages));
            int skip = take * (page - 1);

            Map<Integer, Teacher> teacherMap = schoolClassTeacherDAO.findManyByClass(skip, take, schoolClassId, filter);

            request.setAttribute("teacherMap",    teacherMap);
            request.setAttribute("page",          page);
            request.setAttribute("totalPages",    totalPages);
            request.setAttribute("schoolClassId", schoolClassId);

        } catch (DataException de) {
            ErrorHandler.forward(request, response, de.getStatus(), de.getMessage(), responsePath);
            return;
        }

        request.getRequestDispatcher(responsePath).forward(request, response);
    }
}