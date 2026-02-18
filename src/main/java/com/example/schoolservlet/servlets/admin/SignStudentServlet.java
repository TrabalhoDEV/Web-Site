package com.example.schoolservlet.servlets.admin;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;

/**
 * Servlet responsible for registering new students in the system.
 * This endpoint handles student registration with CPF and school year/grade validation.
 * Only administrators are allowed to access this functionality.
 */
@WebServlet("/admin/sign-student")
public class SignStudentServlet extends HttpServlet {

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
        try {
            HttpSession session = request.getSession();
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            // Only administrators can register students
            if (user.role() != UserRoleEnum.ADMIN) {
                request.getRequestDispatcher("/pages/admin/login.jsp")
                        .forward(request, response);
                return;
            }

        } catch (NullPointerException npe) {
            // User not authenticated or session attribute missing
            request.getRequestDispatcher("/pages/admin/login.jsp")
                    .forward(request, response);
            return;
        }

        // ============ PARAMETER EXTRACTION ============
        // Retrieve CPF and school grade from request parameters
        String studentCpfParam = request.getParameter("cpf");
        String studentClassParam = request.getParameter("anoEscolar");

        // ============ PARAMETER VALIDATION ============
        // Validate that both parameters are present and not empty
        if (studentCpfParam == null || studentCpfParam.isBlank() || studentClassParam == null || studentClassParam.isBlank()) {
            request.setAttribute("success", false);
            request.setAttribute("error", "Parâmetros inválidos");
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        // Validate that anoEscolar is a valid integer
        int studentClass;
        try {
            studentClass = Integer.parseInt(studentClassParam);
        } catch (NumberFormatException nfe) {
            request.setAttribute("success", false);
            request.setAttribute("error", "Série deve ser um número válido");
            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        // ============ CPF NORMALIZATION & VALIDATION ============
        // Normalize the CPF format (remove special characters, etc.)
        String studentCpf = InputNormalizer.normalizeCpf(studentCpfParam);

        // Validate CPF using business rules
        if (!InputValidation.validateCpf(studentCpf)) {
            request.setAttribute("success", false);
            request.setAttribute("error", "CPF Invalido");

            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        // ============ CLASS VALIDATION ============
        // Validate that the grade is within valid range (1st to 12th grade)
        if (!InputValidation.validateStudentClass(studentClass)) {
            request.setAttribute("success", false);
            request.setAttribute("error", "Série inválida");

            request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                    .forward(request, response);
            return;
        }

        // ============ STUDENT REGISTRATION ============
        // Create a new student object and populate it with validated data
        Student student = new Student();
        StudentDAO studentDAO = new StudentDAO();

        student.setCpf(studentCpf);
        student.setIdSchoolClass(studentClass);

        // Attempt to create the student in the database
        try{
            if (studentDAO.create(student)) {
                request.setAttribute("success", true);
            } else {
                request.setAttribute("success", false);
                request.setAttribute("error", "Erro ao cadastrar aluno");
            }
        } catch (IllegalArgumentException iae) {
            request.setAttribute("success", false);
            request.setAttribute("error", iae.getMessage());
        }


        // Forward to admin dashboard with result attributes
        request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp")
                .forward(request, response);
    }
}
