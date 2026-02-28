package com.example.schoolservlet.servlets.admin.findMany;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.*;
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

        if (!AccessValidation.isAdmin(request, response)) return;

        String pageParam = request.getParameter("page");
        String responsePath = "/WEB-INF/views/admin/findMany/student.jsp";

        int take = Constants.MAX_TAKE;
        int skip = 0;
        int page;
        int totalPages;

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException nfe) {
            page = 1;
        }

        StudentDAO studentDAO = new StudentDAO();

        Map<Integer, Student> studentMap;
        try{
            int totalCount = studentDAO.totalCount();

            totalPages = Math.max(1, (int)Math.ceil((double) totalCount / Constants.MAX_TAKE));

            page = Math.max(1, Math.min(page, totalPages));

            skip = take * (page - 1);

            studentMap = studentDAO.findMany(skip, take);
        } catch (DataException de){
            ErrorHandler.forward(request, response, de.getStatus(), de.getMessage(), responsePath);
            return;
        }

        request.setAttribute("studentMap", studentMap);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher(responsePath).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
