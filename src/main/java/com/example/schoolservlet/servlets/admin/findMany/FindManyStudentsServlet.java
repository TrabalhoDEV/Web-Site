package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "admin-find-many-student", value = "/admin/student/find-many")
public class FindManyStudentsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        AccessValidation.isAdmin(request, response);

        String pageParam = request.getParameter("page");

        int take = Constants.MAX_TAKE;
        int skip = 0;
        int page;
        int totalCount = 0;

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException nfe) {
            page = 1;
        }

        StudentDAO studentDAO = new StudentDAO();
        try{
            totalCount = studentDAO.totalCount();
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/student.jsp")
                    .forward(request, response);
            return;
        }

        int totalPages = Math.max(1, (int)Math.ceil((double) totalCount / Constants.MAX_TAKE));

        page = Math.max(1, Math.min(page, totalPages));

        skip = take * (page - 1);

        Map<Integer, Student> studentMap;

        try {
            studentMap = studentDAO.findMany(skip, take);
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/student.jsp")
                    .forward(request, response);
            return;
        }

        request.setAttribute("studentMap", studentMap);
        request.setAttribute("page", page);

        request.getRequestDispatcher("/WEB-INF/views/admin/findMany/student.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
