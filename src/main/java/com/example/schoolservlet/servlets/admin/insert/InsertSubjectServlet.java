package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.FieldAlreadyUsedValidation;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Date;

@WebServlet(name = "admin-insert-subject", value = "/admin/subject/insert")
public class InsertSubjectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");
        String deadlineParam = request.getParameter("deadline");
        Date deadline;

        try{
            InputValidation.validateSubjectName(name);
            InputValidation.validateIsNull("data limite", deadlineParam);
        } catch (ValidationException e){
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
            return;
        }

        try{
            name = InputNormalizer.normalizeName(name.trim());
            deadline = InputNormalizer.normalizeDate(deadlineParam.trim());
        } catch (TransformTypeException vte){
            vte.printStackTrace();
            request.setAttribute("error", vte.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
            return;
        }

        Subject subject = new Subject();
        subject.setName(name);
        subject.setDeadline(deadline);

        try {
            FieldAlreadyUsedValidation.exists("subject", "name", "nome", name);

            SubjectDAO subjectDAO = new SubjectDAO();

            subjectDAO.create(subject);
        } catch (DataException de){
            de.printStackTrace();
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
            return;
        } catch (RequiredFieldException rfe){
            rfe.printStackTrace();
            request.setAttribute("error", rfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
            return;
        } catch (InvalidDateException ide){
            ide.printStackTrace();
            request.setAttribute("error", ide.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
            return;
        } catch (ValueAlreadyExistsException vaee){
            vaee.printStackTrace();
            request.setAttribute("error", vaee.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/subject.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
    }
}
