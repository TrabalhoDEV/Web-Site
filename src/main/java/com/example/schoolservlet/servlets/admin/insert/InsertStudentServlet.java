package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.StudentSubjectDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.EmailService;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet responsible for registering new students in the system.
 * This endpoint handles student registration with CPF and school year/grade validation.
 * Only administrators are allowed to access this functionality.
 */
@WebServlet(name = "admin-add-student",value = "/admin/add-student")
public class InsertStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        getAllData(request, response);

        request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
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
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        cpf = InputNormalizer.normalizeCpf(cpf);
        email = InputNormalizer.normalizeEmail(email);

        // Validate that anoEscolar is a valid integer
        int studentClassId = 0;
        try {
            studentClassId = Integer.parseInt(studentClassParam);
        } catch (NumberFormatException nfe) {
            getAllData(request, response);
            request.setAttribute("error", "ID precisa ser um valor numérico inteiro");
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        try {
            SchoolClassDAO schoolClassDAO = new SchoolClassDAO();
            schoolClassDAO.findById(studentClassId);
        } catch (DataException | ValidationException | NotFoundException e){
            getAllData(request, response);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
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

            EmailService.sendEmail(student.getEmail(), "Cadastro na Vértice",
            "<h2>Faça sua matrícula na Vétice</h2>" +
                    "<p>Se você realmente for o próximo aluno da Vértice:</p>" +
                    "<p><a href=\"#\">Clique aqui</a> para fazer o seu cadastro</p>"
                    );
        } catch (DataException de) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", de.getMessage());
        }  catch (NotFoundException nfe){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", nfe.getMessage());
        } catch (Exception e){
            request.setAttribute("error", e.getMessage());
        }

        getAllData(request, response);
        // Forward to admin dashboard with result attributes
        request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                .forward(request, response);
    }

    private void getAllData(HttpServletRequest request, HttpServletResponse response){
        SchoolClassDAO schoolClassDAO = new SchoolClassDAO();

        try {
            List<SchoolClass> schoolClasses = schoolClassDAO.findAll();
            request.setAttribute("schoolClasses", schoolClasses);
        } catch (DataException de){
            request.setAttribute("error", de);
        }
    }
}