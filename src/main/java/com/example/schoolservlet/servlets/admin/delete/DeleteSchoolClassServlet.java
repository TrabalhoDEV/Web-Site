package com.example.schoolservlet.servlets.admin.delete;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "DeleteSchoolClassServlet", value = "/admin/school-class/delete")
public class DeleteSchoolClassServlet extends HttpServlet {
    private final String responsePath = "/admin/school-class/find-many";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        HttpSession session = request.getSession(false);

        try {
            InputValidation.validateIsNull("id", idParam);
            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            schoolClassDAO.findById(id);

            StudentDAO studentDAO = new StudentDAO();

            int howManyStudents = studentDAO.countBySchoolClass(id);

            if (howManyStudents <= 0){
                schoolClassDAO.delete(id);
            } else {
                getAllData(request, response);
                request.setAttribute("id", id);
                request.getRequestDispatcher("/WEB-INF/views/admin/delete/school-class.jsp").forward(request, response);
                return;
            }

        } catch (NumberFormatException nfe){
            session.setAttribute("error", "ID deve ser um número");
        } catch (DataException | NotFoundException | ValidationException e){
            session.setAttribute("error", e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + responsePath);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        String newIdParam = request.getParameter("newId");
        HttpSession session = request.getSession(false);

        try {
            InputValidation.validateIsNull("id", idParam);
            InputValidation.validateIsNull("id da nova turma", newIdParam);
            int id = Integer.parseInt(idParam);
            int newId = Integer.parseInt(newIdParam);

            if (id == newId) throw new ValidationException("Essa turma será deletada, escolha outra");

            InputValidation.validateId(id, "id");

            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            schoolClassDAO.findById(id);

            StudentDAO studentDAO = new StudentDAO();

            studentDAO.updateManyIdSchoolClass(id, newId);

            schoolClassDAO.delete(id);
        } catch (NumberFormatException nfe){
            session.setAttribute("error", "ID deve ser um número");
            getAllData(request, response);
            request.setAttribute("id", idParam);
            request.getRequestDispatcher("/WEB-INF/views/admin/delete/school-class.jsp").forward(request, response);
            return;
        } catch (DataException | NotFoundException | ValidationException e){
            session.setAttribute("error", e.getMessage());
            getAllData(request, response);
            request.setAttribute("id", idParam);
            request.getRequestDispatcher("/WEB-INF/views/admin/delete/school-class.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + responsePath);
    }

    private void getAllData(HttpServletRequest request, HttpServletResponse response){
        SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

        try {
            List<SchoolClass> schoolClasses = schoolClassDAO.findAll();
            request.setAttribute("schoolClasses", schoolClasses);
        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
        }
    }
}
