package com.example.schoolservlet.servlets.admin.insert;

import com.example.schoolservlet.daos.SchoolClassDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.AccessValidation;
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
        if (!AccessValidation.isAdmin(request, response)) return;
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
        String studentClassParam = request.getParameter("anoEscolar");

        // ============ PARAMETER VALIDATION ============
        // Validate that both parameters are present and not empty
        try {
            InputValidation.validateIsNull("cpf", cpf);
            InputValidation.validateIsNull("ano escolar", studentClassParam);
        } catch (RequiredFieldException re){
            getAllData(request, response);
            request.setAttribute("error", re.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

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

        // ============ CPF NORMALIZATION & VALIDATION ============
        // Normalize the CPF format (remove special characters, etc.)
        cpf = InputNormalizer.normalizeCpf(cpf);

        // Validate CPF using business rules
        try {
            InputValidation.validateCpf(cpf);
        } catch (ValidationException e){
            getAllData(request, response);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        // ============ STUDENT REGISTRATION ============
        // Create a new student object and populate it with validated data
        Student student = new Student();
        StudentDAO studentDAO = new StudentDAO();

        student.setCpf(cpf);
        student.setIdSchoolClass(studentClassId);

        // Attempt to create the student in the database
        try{
            studentDAO.create(student);
            request.setAttribute("success", true);
        } catch (ValidationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
        } catch (DataException de){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", de.getMessage());
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