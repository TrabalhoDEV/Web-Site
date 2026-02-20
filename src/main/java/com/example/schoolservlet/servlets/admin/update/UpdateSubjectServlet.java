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

        HttpSession session = request.getSession(false);

        String idParam = request.getParameter("id");

        try {
            SubjectDAO subjectDAO = new SubjectDAO();

            if (idParam == null || idParam.isEmpty()) throw new InvalidNumberException(idParam,"O ID não pode estar vazio");

            Subject subject = subjectDAO.findById(Integer.parseInt(idParam));
            session.setAttribute("subject", subject);
            request.setAttribute("subject", subject);
        } catch (DataException de){
            de.printStackTrace();
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        } catch (ValidationException e){
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            nfe.printStackTrace();
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        } catch (NumberFormatException nfe) {
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/teacher.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        HttpSession session = request.getSession(false);

        Subject oldSubject;
        try {
            oldSubject = (Subject) session.getAttribute("subject");
        } catch (NullPointerException npe){
            npe.printStackTrace();
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/pages/admin/login.jsp")
                    .forward(request, response);
            return;
        }

        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");
        String deadlineParam = request.getParameter("deadline");
        Date deadline;

        try{
            InputValidation.validateSubjectName(name);
            InputValidation.validateIsNull("data limite", deadlineParam);
        } catch (ValidationException e){
            e.printStackTrace();
            request.setAttribute("subject", oldSubject);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        }

        try{
            name = InputNormalizer.normalizeName(name.trim());
            deadline = InputNormalizer.normalizeDate(deadlineParam.trim());
        } catch (TransformTypeException vte){
            vte.printStackTrace();
            request.setAttribute("subject", oldSubject);
            request.setAttribute("error", vte.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        }

        Subject subject = new Subject();
        subject.setName(name);
        subject.setDeadline(deadline);
        subject.setId(oldSubject.getId());

        try {
            if (!name.equals(oldSubject.getName())){
                FieldAlreadyUsedValidation.exists("subject", "name", "nome", name);
            }

            SubjectDAO subjectDAO = new SubjectDAO();

            subjectDAO.update(subject);
        } catch (DataException de){
            de.printStackTrace();
            request.setAttribute("subject", oldSubject);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        } catch (ValidationException ve){
            ve.printStackTrace();
            request.setAttribute("subject", oldSubject);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe){
            nfe.printStackTrace();
            request.setAttribute("subject", oldSubject);
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/subject.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/subject/find-many");
    }
}
