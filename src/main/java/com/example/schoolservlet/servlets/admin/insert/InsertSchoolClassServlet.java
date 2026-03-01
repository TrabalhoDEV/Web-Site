package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.exceptions.ValueAlreadyExistsException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.FieldAlreadyUsedValidation;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "InsertSchoolClassServlet", value = "/admin/school-class/insert")
public class InsertSchoolClassServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        request.getRequestDispatcher("/WEB-INF/views/admin/insert/school-class.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");

        try{
            InputValidation.validateIsNull("turma", name);

            name = name.trim().toLowerCase();

            InputValidation.validateSchoolClassName(name);

            FieldAlreadyUsedValidation.exists("school_class", "school_year", "nome da turma", name);

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

            SchoolClass schoolClass = new SchoolClass();

            schoolClass.setSchoolYear(name);

            schoolClassDAO.create(schoolClass);

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (DataException | ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/insert/school-class.jsp");
        }
    }
}
