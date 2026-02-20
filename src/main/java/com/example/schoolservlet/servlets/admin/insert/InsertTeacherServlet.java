package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "admin-insert-teacher", value = "/admin/teacher/insert")
public class InsertTeacherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        getAllData(request, response);

        request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private void getAllData(HttpServletRequest request, HttpServletResponse response){
        SubjectDAO subjectDAO = new SubjectDAO();

        try {
            List<Subject> subjects = subjectDAO.findMany();
            request.setAttribute("subjects", subjects);
        } catch (DataException de){
            request.setAttribute("error", de);
        }
    }
}
