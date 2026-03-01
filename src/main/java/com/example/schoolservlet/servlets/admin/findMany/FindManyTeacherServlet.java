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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String sessionError = (String) request.getSession().getAttribute("error");

        if (sessionError != null) {
            request.setAttribute("error", sessionError);
            request.getSession().removeAttribute("error");
        }

        String pageParam = request.getParameter("page");

        int take = Constants.MAX_TAKE;
        int skip = 0;
        int page;
        int totalCount = 0;
        int totalPages;

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException nfe) {
            page = 1;
        }

        TeacherDAO teacherDAO = new TeacherDAO();
        Map<Integer, Teacher> teacherMap;
        try {
            totalCount = teacherDAO.totalCount();
            totalPages = Math.max(1, (int)Math.ceil((double) totalCount / Constants.MAX_TAKE));
            page = Math.max(1, Math.min(page, totalPages));
            skip = take * (page - 1);
            teacherMap = teacherDAO.findMany(skip, take);
        } catch (DataException de) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("teacherMap", teacherMap);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
