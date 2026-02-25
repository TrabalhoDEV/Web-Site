package com.example.schoolservlet.servlets.admin.update;

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

@WebServlet(name = "admin-update-subject", value = "/admin/subject/update")
public class UpdateSubjectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.isEmpty()) throw new InvalidNumberException(idParam,"O ID não pode estar vazio");

            SubjectDAO subjectDAO = new SubjectDAO();
            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");
            Subject subject = subjectDAO.findById(id);

            request.setAttribute("subject", subject);
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
        } catch (NotFoundException nfe){
            nfe.printStackTrace();
            request.getSession(false).setAttribute("error", nfe.getMessage());

            response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
        } catch (ValidationException | DataException e){
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
        } catch (NumberFormatException nfe){
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");

            response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String deadlineParam = request.getParameter("deadline");
        Subject subject = new Subject();

        try {
            if (idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "ID inválido");
            }

            int id = Integer.parseInt(idParam);

            InputValidation.validateId(id, "id");
            InputValidation.validateSubjectName(name);
            InputValidation.validateIsNull("data limite", deadlineParam);

            name = InputNormalizer.normalizeName(name.trim());
            Date deadline = InputNormalizer.normalizeDate(deadlineParam.trim());

            SubjectDAO subjectDAO = new SubjectDAO();

            subject = subjectDAO.findById(id);

            if (!name.equals(subject.getName())) {
                FieldAlreadyUsedValidation.exists("subject", "name", "nome", name);
                subject.setName(name);
            }

            if (!deadline.equals(subject.getDeadline())) {
                subject.setDeadline(deadline);
            }

            subjectDAO.update(subject);

            response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
        } catch (NotFoundException | InvalidNumberException e) {
            e.printStackTrace();
            request.getSession(false).setAttribute("error", e.getMessage());

            response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
        }  catch (NumberFormatException nfe){
            nfe.printStackTrace();
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");

            response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
        } catch (ValidationException | DataException e) {
            e.printStackTrace();
            request.setAttribute("subject", subject);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp")
                    .forward(request, response);
        }
    }
}
