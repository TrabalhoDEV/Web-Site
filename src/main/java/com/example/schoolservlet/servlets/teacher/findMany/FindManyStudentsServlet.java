package com.example.schoolservlet.servlets.teacher.findMany;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "teacher-find-many-students", value = "/teacher/students")
public class FindManyStudentsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isTeacher(request, response)) return;

        AuthenticatedUser user;
        try {
            HttpSession session = request.getSession(false);
            user = (AuthenticatedUser) session.getAttribute("user");
        } catch (NullPointerException npe) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return;
        }

        String pageParam = request.getParameter("page");
        int take = Constants.MAX_TAKE;
        int skip = 0;
        int page;
        int count = 0;

        request.setAttribute("studentMap", new HashMap<Integer, Student>());
        request.setAttribute("page", 1);
        request.setAttribute("totalPages", 1);

        try {
            page = Integer.parseInt(pageParam);
        } catch (NumberFormatException nfe) {
            page = 1;
        }

        StudentDAO studentDAO = new StudentDAO();
        try{
            count = studentDAO.countByTeacherId(user.id());

            int totalPages = Math.max(1, (int)Math.ceil((double) count / Constants.MAX_TAKE));

            page = Math.max(1, Math.min(page, totalPages));

            skip = take * (page - 1);

            Map<Integer, Student> studentMap = studentDAO.findManyByTeacherId(skip, take, user.id());

            request.setAttribute("studentMap", studentMap);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
        } catch (DataException | ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/teacher/student/find-many.jsp");
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        request.getRequestDispatcher("/WEB-INF/views/teacher/student/find-many.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
