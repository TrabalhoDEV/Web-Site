package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Servlet responsible for registering new students in the system.
 * This endpoint handles student registration with CPF and school year/grade validation.
 * Only administrators are allowed to access this functionality.
 */
@WebServlet(name = "admin-add-student",value = "/admin/student/insert")
public class InsertStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        if (!AccessValidation.isAdmin(request, response)) return;
        getAllData(request, response);

        request.getRequestDispatcher("/WEB-INF/views/admin/insert/student.jsp")
                .forward(request, response);
    }

    /**
     * Handles POST requests for student registration.
     *
     * @param request  the HTTP request containing CPF and school grade parameters
     * @param response the HTTP response object
     * @throws ServletException if servlet processing fails
     * @throws IOException      if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ============ AUTHENTICATION CHECK ============
        // Verify that the user is authenticated and has ADMIN role
        if (!AccessValidation.isAdmin(request, response)) return;

        // ============ PARAMETER EXTRACTION ============
        // Retrieve CPF and school grade from request parameters
        String cpf = request.getParameter("cpf");
        String email = request.getParameter("email");
        String studentClassParam = request.getParameter("anoEscolar");

        // ============ PARAMETER VALIDATION ============
        // Validate that both parameters are present and not empty
        try {
            InputValidation.validateIsNull("cpf", cpf);
            InputValidation.validateIsNull("email", email);
            InputValidation.validateIsNull("ano escolar", studentClassParam);

            cpf = cpf.trim();
            email = email.trim();

            InputValidation.validateCpf(cpf);
            InputValidation.validateEmail(email);
        } catch (ValidationException ve){
            getAllData(request, response);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/student.jsp")
                    .forward(request, response);
            return;
        }

        cpf = InputNormalizer.normalizeCpf(cpf);
        email = InputNormalizer.normalizeEmail(email);

        // Validate duplicates:
        try {
            FieldAlreadyUsedValidation.exists("student", "cpf", "cpf", cpf);
            FieldAlreadyUsedValidation.exists("admin", "document", "cpf", cpf);
            FieldAlreadyUsedValidation.exists("student", "email", "email", email);
        } catch (DataException | ValueAlreadyExistsException e){
            getAllData(request, response);
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/insert/student.jsp");
            return;
        }

        // Validate that anoEscolar is a valid integer
        int studentClassId = 0;
        try {
            studentClassId = Integer.parseInt(studentClassParam);
        } catch (NumberFormatException nfe) {
            getAllData(request, response);
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_REQUEST,"ID precisa ser um valor numérico inteiro", "/WEB-INF/views/admin/insert/student.jsp");
            return;
        }

        try {
            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            schoolClassDAO.findById(studentClassId);
        } catch (DataException | ValidationException | NotFoundException e){
            getAllData(request, response);
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/insert/student.jsp");
            return;
        }

        // ============ STUDENT REGISTRATION ============
        // Create a new student object and populate it with validated data
        Student student = new Student();
        StudentDAO studentDAO = new StudentDAO();
        StudentSubjectDAO studentSubjectDAO = new StudentSubjectDAO();

        student.setCpf(cpf);
        student.setEmail(email);
        student.setIdSchoolClass(studentClassId);

        // Attempt to create the student in the database
        try{
            studentDAO.create(student);

            student = studentDAO.findByCpf(student.getCpf());

            studentSubjectDAO.createManyByStudentClass(student.getId(), studentClassId);

            response.setStatus(HttpServletResponse.SC_OK);

            String link = request.getRequestURL().toString().replace(request.getRequestURI(), "") +
                    request.getContextPath() + "/pages/students/signup.jsp?enrollment=" +
                    URLEncoder.encode(student.getEnrollment(), StandardCharsets.UTF_8);

            EmailService.sendEmail(student.getEmail(), "Cadastro na Vértice",
            "<h2>Faça sua matrícula na Vértice</h2>" +
                    "<p>Se você realmente for o próximo aluno da Vértice:</p>" +
                    "<p><a href=\"https://colegio-vertice.onrender.com/student/register\">Clique aqui</a> para fazer o seu cadastro</p>"
                    );
        } catch (DataException de) {
            getAllData(request, response);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/student.jsp")
                    .forward(request, response);
        }  catch (NotFoundException nfe){
            getAllData(request, response);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/student.jsp")
                    .forward(request, response);
        } catch (Exception e){
            getAllData(request, response);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/insert/student.jsp")
                    .forward(request, response);
        }

        response.sendRedirect(request.getContextPath() + "/admin/student/find-many");
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