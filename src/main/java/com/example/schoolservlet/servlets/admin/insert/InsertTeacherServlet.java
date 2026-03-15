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
import java.util.ArrayList;
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
     * <p>
     * If any validation fails after the teacher is created (e.g., invalid subject or class associations),
     * the method performs a rollback by deleting the created teacher record. Database CASCADE rules
     * automatically clean up any related subject-teacher and class-teacher relationships.
     * </p>
     *
     * @param request  the HTTP request containing name, email, username, password, subjectIds, and schoolClassIds parameters
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

        try {
            InputValidation.validateTeacherName(name);
            InputValidation.validateEmail(email);
            InputValidation.validateUserName(username);
            InputValidation.validatePassword(password);

            FieldAlreadyUsedValidation.exists("teacher","email","Email",email);
            FieldAlreadyUsedValidation.exists("teacher","username","Username",username);

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
                List<SubjectTeacher> subjectTeachersToInsert = new ArrayList<>();
                for (Integer subjectId : validSubjectIds) {
                    if (!subjectDAO.hasStudentsById(subjectId)) {
                        // Rollback teacher creation
                        teacherDAO.delete(teacherId);
                        throw new ValidationException("A matéria selecionada não possui alunos vinculados.");
                    }

                    Subject subject = subjectDAO.findById(subjectId);

                    SubjectTeacher subjectTeacher = new SubjectTeacher();
                    subjectTeacher.setTeacher(teacher);
                    subjectTeacher.setSubject(subject);
                    subjectTeachersToInsert.add(subjectTeacher);
                }
                subjectTeacherDAO.createMany(subjectTeachersToInsert);
            }

            try {
                String assunto = "Acesso ao Sistema Escolar";
                String mensagem = "Olá " + OutputFormatService.formatName(teacher.getName()) + ",<br><br>"
                        + "Você já pode acessar o sistema escolar.<br>"
                        + "A senha será enviada pelo administrador da escola.<br>"
                        + String.format("<a href=\"%s/index.jsp\">Clique aqui para logar</a><br><br>", request.getContextPath())
                        + "Atenciosamente,<br>"
                        + "Secretaria Vértice";

                EmailService.sendEmail(teacher.getEmail(), assunto, mensagem);
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.setAttribute("success",true);
            response.sendRedirect(request.getContextPath() + "/admin/teacher/find-many");

        } catch (NumberFormatException nfe){
            getAllSubjects(request);
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
        } catch (ValidationException | NotFoundException | DataException e) {
            getAllSubjects(request);
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/findMany/teacher.jsp");
        }
    }

    /**
     * Handles GET requests to display the teacher registration form.
     * Loads all available subjects and school classes for selection.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws ServletException if servlet processing fails
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        if (!AccessValidation.isAdmin(request, response)) return;
        getAllSubjects(request);
        request.getRequestDispatcher("/WEB-INF/views/admin/insert/teacher.jsp").forward(request,response);
    }

    /**
     * Retrieves all subjects from the database and sets them as a request attribute.
     * If an error occurs, sets the error message as a request attribute.
     *
     * @param request the HTTP request object to set attributes
     */
    private void getAllSubjects(HttpServletRequest request){
        try {
            List<Subject> subjects = subjectDAO.findAll();
            request.setAttribute("subjects", subjects);
        } catch (DataException de){
            request.setAttribute("error", de.getMessage());
        }
    }
}
