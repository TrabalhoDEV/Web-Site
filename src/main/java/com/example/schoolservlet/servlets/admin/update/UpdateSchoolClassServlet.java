package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.FieldAlreadyUsedValidation;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "UpdateSchoolClassServlet", value = "/admin/school-class/update")
public class UpdateSchoolClassServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");
        String idParam = request.getParameter("id");

        try{
            InputValidation.validateIsNull("turma", name);
            InputValidation.validateIsNull("id da turma", idParam);

            name = name.trim();
            int id = Integer.parseInt(idParam);

            InputValidation.validateSchoolClassName(name);
            InputValidation.validateId(id, "id da turma");

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

            SchoolClass schoolClass;

            schoolClass = schoolClassDAO.findById(id);

            if (schoolClass.getSchoolYear().equals(name)){
                response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
            }

            FieldAlreadyUsedValidation.exists("school_class", "school_year", "nome da turma", name);

            schoolClass.setSchoolYear(name);
            schoolClassDAO.update(schoolClass);

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (DataException | NotFoundException  | ValidationException e){
            getAllData(request, response, Integer.parseInt(idParam));
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/update/school-class.jsp");
        } catch (NumberFormatException nfe){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "ID da turma precisa ser um número");
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp").forward(request, response);
        }
    }

    private void getAllData(HttpServletRequest request, HttpServletResponse response, int id) throws ValidationException, DataException, NotFoundException{
        SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

        SchoolClass schoolClass = schoolClassDAO.findById(id);

        request.setAttribute("schoolClass", schoolClassDAO);
    }
}
