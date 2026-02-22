package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.*;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.*;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for registering new teachers in the system.
 * This endpoint handles teacher registration.
 * Only administrators are allowed to access this functionality
 */
@WebServlet(name = "admin-insert-teacher", value = "/admin/teacher/insert")
public class InsertTeacherServlet extends HttpServlet {
    private SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
    private SubjectDAO subjectDAO = new SubjectDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();
    private SubjectTeacherDAO subjectTeacherDAO = new SubjectTeacherDAO();
    private SchoolClassTeacherDAO schoolClassTeacherDAO = new SchoolClassTeacherDAO();

    /**
     * Handles POST requests for teacher registration.
     *
     * @param request  the HTTP request containing name, email, username, password parameters
     * @param response the HTTP response object
     * @throws ServletException if servlet processing fails
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AccessValidation.isAdmin(request, response)) return;

        String name = InputNormalizer.normalizeName(request.getParameter("name"));
        String email = InputNormalizer.normalizeEmail(request.getParameter("email"));
        String username = InputNormalizer.normalizeUserName(request.getParameter("username"));
        String password = request.getParameter("password");
        String[] subjectIdsParam = request.getParameterValues("subjectIds");
        String[] schoolClassIdsParam = request.getParameterValues("schoolClassIds");

        try {
            InputValidation.validateTeacherName(name);
            InputValidation.validateEmail(email);
            InputValidation.validateUserName(username);
            InputValidation.validatePassword(password);

            FieldAlreadyUsedValidation.exists("teacher","email",email);
            FieldAlreadyUsedValidation.exists("teacher","username",username);

            Teacher teacher = new Teacher();
            teacher.setName(name);
            teacher.setEmail(email);
            teacher.setUsername(username);
            teacher.setPassword(password);
            teacherDAO.create(teacher);

            Teacher createdTeacher = teacherDAO.findByUserName(username);
            int teacherId = createdTeacher.getId();
            teacher.setId(teacherId);

            List<Integer> validSubjectIds = InputValidation.validateIdsExist(subjectIdsParam,subjectDAO.findAllIds());
            if(validSubjectIds != null && !validSubjectIds.isEmpty()) {

                for (Integer subjectId : validSubjectIds) {

                    Subject subject = subjectDAO.findById(subjectId);

                    SubjectTeacher subjectTeacher = new SubjectTeacher();
                    subjectTeacher.setTeacher(teacher);
                    subjectTeacher.setSubject(subject);

                    subjectTeacherDAO.create(subjectTeacher);
                }
            }

            List<Integer> validClassIds = InputValidation.validateIdsExist(schoolClassIdsParam, schoolClassDAO.findAllIds());
            if(validClassIds != null && !validClassIds.isEmpty()){

                for (Integer classId : validClassIds) {
                    SchoolClass schoolClass = schoolClassDAO.findById(classId);
                    SchoolClassTeacher sct = new SchoolClassTeacher();
                    sct.setTeacher(teacher);
                    sct.setSchoolClass(schoolClass);

                    schoolClassTeacherDAO.create(sct);
                }
            }

            try {
                String assunto = "Acesso ao Sistema Escolar";
                String mensagem = "Olá " + teacher.getName() + ",<br><br>"
                        + "Você já pode acessar o sistema escolar.<br>"
                        + "A senha será enviada pelo administrador da escola.<br><br>"
                        + "Atenciosamente,<br>"
                        + "Secretaria Vértice";

                EmailService.sendEmail(teacher.getEmail(), assunto, mensagem);
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("success",true);
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");

        } catch (NumberFormatException nfe){
            getAllSchoolClasses(request);
            getAllSubjects(request);
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
            return;
        } catch (NotFoundException nfe){
            getAllSchoolClasses(request);
            getAllSubjects(request);
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/findMany/teacher.jsp").forward(request, response);
            return;
        } catch (RequiredFieldException rfe){
            getAllSchoolClasses(request);
            getAllSubjects(request);
            request.setAttribute("error", rfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
            return;
        } catch (ValueAlreadyExistsException vaee) {
            getAllSchoolClasses(request);
            getAllSubjects(request);
            request.setAttribute("error", vaee.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
            return;
        } catch (ValidationException ve){
            getAllSchoolClasses(request);
            getAllSubjects(request);
            request.setAttribute("error",ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
            return;
        }catch (DataException de) {
            getAllSchoolClasses(request);
            getAllSubjects(request);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
            return;
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        if (!AccessValidation.isAdmin(request, response)) return;
        getAllSubjects(request);
        getAllSchoolClasses(request);
        request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
    }

    private void getAllSubjects(HttpServletRequest request){
        try {
            List<Subject> subjects = subjectDAO.findAll();
            request.setAttribute("subjects", subjects);
        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
        }
    }

    private void getAllSchoolClasses(HttpServletRequest request) {
        try {
            List<SchoolClass> classes = schoolClassDAO.findAll();
            request.setAttribute("classes", classes);
        } catch (DataException de) {
            request.setAttribute("error", de.getMessage());
        }
    }
}
