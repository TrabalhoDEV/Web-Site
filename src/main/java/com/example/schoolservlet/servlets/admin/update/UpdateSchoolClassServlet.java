package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.SchoolClassDAO;
<<<<<<< HEAD
import com.example.schoolservlet.daos.SchoolClassSubjectDAO;
import com.example.schoolservlet.daos.SubjectDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.InvalidNumberException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.SchoolClassSubject;
import com.example.schoolservlet.models.Subject;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet(name = "UpdateSchoolClassSubjectServlet", value = "/admin/school-class/update")
public class UpdateSchoolClassServlet extends HttpServlet {
    private SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();
    private SchoolClassSubjectDAO schoolClassSubjectDAO = new SchoolClassSubjectDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

=======
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
>>>>>>> 987874a (feat: creating routes to create or update school_class)
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

<<<<<<< HEAD
        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "O ID não pode estar vazio");
            }

            int id = Integer.parseInt(idParam);
            InputValidation.validateId(id, "id");

            loadUpdateData(request, id);

            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);

        } catch (NumberFormatException nfe) {
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (NotFoundException nfe) {
            nfe.printStackTrace();
            request.getSession(false).setAttribute("error", nfe.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (ValidationException | DataException e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        int schoolClassId = -1;

        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {
                throw new InvalidNumberException(idParam, "O ID não pode estar vazio");
            }

            schoolClassId = Integer.parseInt(idParam);
            InputValidation.validateId(schoolClassId, "id");

            SchoolClass schoolClass = schoolClassDAO.findById(schoolClassId);

            String[] subjectIdsParam = request.getParameterValues("subjectIds");

            List<Integer> validSubjectIds = InputValidation.validateIdsExist(
                    subjectIdsParam,
                    subjectDAO.findAllIds()
            );

            Set<Integer> newSubjectIds = new HashSet<>();
            if (validSubjectIds != null) {
                newSubjectIds.addAll(validSubjectIds);
            }

            List<Subject> currentSubjects = subjectDAO.findBySchoolClassId(schoolClassId);

            Set<Integer> currentSubjectIds = new HashSet<>();
            for (Subject s : currentSubjects) {
                currentSubjectIds.add(s.getId());
            }

            Set<Integer> toAddSubjects = new HashSet<>(newSubjectIds);
            toAddSubjects.removeAll(currentSubjectIds);

            Set<Integer> toRemoveSubjects = new HashSet<>(currentSubjectIds);
            toRemoveSubjects.removeAll(newSubjectIds);

            if (!toAddSubjects.isEmpty()) {
                List<SchoolClassSubject> schoolClassSubjectsToInsert = new ArrayList<>();

                for (Integer subjectId : toAddSubjects) {
                    Subject subject = subjectDAO.findById(subjectId);

                    SchoolClassSubject scs = new SchoolClassSubject();
                    scs.setSchoolClass(schoolClass);
                    scs.setSubject(subject);
                    schoolClassSubjectsToInsert.add(scs);
                }

                schoolClassSubjectDAO.createMany(schoolClassSubjectsToInsert);
            }

            schoolClassSubjectDAO.deleteManyBySchoolClassAndSubjects(schoolClassId, toRemoveSubjects);

            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (NumberFormatException nfe) {
            request.getSession(false).setAttribute("error", "ID precisa ser um valor numérico");
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (NotFoundException | InvalidNumberException e) {
            e.printStackTrace();
            request.getSession(false).setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/school-class/find-many");

        } catch (ValidationException | DataException e) {
            e.printStackTrace();
            loadSafely(request, schoolClassId);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/update/school-class.jsp")
                    .forward(request, response);
        }
    }

    private void loadUpdateData(HttpServletRequest request, int schoolClassId)
            throws DataException, NotFoundException, ValidationException {

        SchoolClass schoolClass = schoolClassDAO.findById(schoolClassId);
        List<Subject> allSubjects = subjectDAO.findAll();
        List<Subject> schoolClassSubjects = subjectDAO.findBySchoolClassId(schoolClassId);

        request.setAttribute("schoolClass", schoolClass);
        request.setAttribute("subjects", allSubjects);
        request.setAttribute("schoolClassSubjects", schoolClassSubjects);
    }

    private void loadSafely(HttpServletRequest request, int schoolClassId) {
        try {
            loadUpdateData(request, schoolClassId);
        } catch (Exception ignored) {}
    }
}
=======
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
>>>>>>> 987874a (feat: creating routes to create or update school_class)
