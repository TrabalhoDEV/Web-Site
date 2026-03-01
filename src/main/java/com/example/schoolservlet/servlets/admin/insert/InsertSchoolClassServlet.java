package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.FieldAlreadyUsedValidation;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "InsertSchoolClassServlet", value = "/admin/school-class/insert")
public class InsertSchoolClassServlet extends HttpServlet {
    private SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();
    private SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        getAllSubjects(request);

        request.getRequestDispatcher("/WEB-INF/views/admin/insert/school-class.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        String name = request.getParameter("name");
        String[] subjectIdsParam = request.getParameterValues("subjectIds");

        try{
            InputValidation.validateIsNull("turma", name);

            name = name.trim().toLowerCase();

            InputValidation.validateSchoolClassName(name);

            if (subjectIdsParam == null || subjectIdsParam.length == 0) {
                throw new ValidationException("Selecione pelo menos uma matéria");
            }

            FieldAlreadyUsedValidation.exists("school_class", "school_year", "nome da turma", name);

            List<Integer> validSubjectIds = InputValidation.validateIdsExist(
                    subjectIdsParam,
                    subjectDAO.findAllIds()
            );

            SchoolClass schoolClass = new SchoolClass();

            schoolClass.setSchoolYear(name);

            this.schoolClassDAO.create(schoolClass);

            SchoolClass createdSchoolClass = this.schoolClassDAO.findByName(name);

            List<SchoolClassSubject> schoolClassSubjectsToInsert = new ArrayList<>();

            for (Integer subjectId : validSubjectIds) {
                Subject subject = subjectDAO.findById(subjectId);

                SchoolClassSubject schoolClassSubject = new SchoolClassSubject();
                schoolClassSubject.setSchoolClass(createdSchoolClass);
                schoolClassSubject.setSubject(subject);

                schoolClassSubjectsToInsert.add(schoolClassSubject);
            }

            schoolClassSubjectDAO.createMany(schoolClassSubjectsToInsert);

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");
        } catch (DataException | NotFoundException | ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/insert/school-class.jsp");
        }
    }

    private void getAllSubjects(HttpServletRequest request){
        try {
            List<Subject> subjects = subjectDAO.findAll();
            request.setAttribute("subjects", subjects);
        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
        }
    }
}
