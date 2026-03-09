package com.example.schoolservlet.servlets.admin.delete;

import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.StudentSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "DeleteSubjectServlet", value = "/admin/subject/delete")
public class DeleteSubjectServlet extends HttpServlet {
    private final String pagePath = "";
    private final StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");


        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        int id = 0;

        try{
            InputValidation.validateIsNull("id", idParam);
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException | RequiredFieldException e){
        }

        try{
            Subject subject = subjectDAO.findById(id);

            subjectDAO.delete(id);
        } catch (DataException | ValidationException | NotFoundException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), pagePath);
        }

        response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
    }
}
