package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Date;

@WebServlet(name = "UpdateSchoolClassServlet", value = "/admin/school-class/update")
public class UpdateSchoolClassServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.isEmpty()) throw new InvalidNumberException(idParam,"O ID não pode estar vazio");

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");
            SchoolClass schoolClass = schoolClassDAO.findById(id);

            request.setAttribute("schoolClass", schoolClass);
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp").forward(request, response);
        } catch (NotFoundException nfe){
            nfe.printStackTrace();
            request.getSession(false).setAttribute("error", nfe.getMessage());

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (ValidationException | DataException e){
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp").forward(request, response);
        } catch (NumberFormatException nfe){
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        String schoolYear = request.getParameter("schoolYear");
        SchoolClass schoolClass = new SchoolClass();

        try {
            if (idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "ID inválido");
            }

            int id = Integer.parseInt(idParam);

            InputValidation.validateId(id, "id");
            InputValidation.validateSchoolClassName(schoolYear);

            schoolYear = schoolYear.trim().toLowerCase();

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

            schoolClass = schoolClassDAO.findById(id);

            if (!schoolYear.equals(schoolClass.getSchoolYear())) {
                FieldAlreadyUsedValidation.exists("school_class", "school_year", "ano escolar", schoolYear);
                schoolClass.setSchoolYear(schoolYear);
            }

            schoolClassDAO.update(schoolClass);

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (NotFoundException | InvalidNumberException e) {
            e.printStackTrace();
            request.getSession(false).setAttribute("error", e.getMessage());

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (ValidationException | DataException e) {
            e.printStackTrace();
            request.setAttribute("subject", schoolClass);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);
        }
    }
}
