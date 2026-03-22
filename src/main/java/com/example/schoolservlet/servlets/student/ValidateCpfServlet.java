package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "ValidateCpfServlet", value = "/student/validate/cpf")
public class ValidateCpfServlet extends HttpServlet {

    /**
     * Handles GET requests for the CPF-based student signup page.
     *
     * <p>Forwards the request to the "signupCpf.jsp" page and sets the response content type to HTML.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
    }

    /**
     * Handles POST requests for CPF-based student signup.
     *
     * <p>Validates and normalizes the provided CPF, retrieves the corresponding student,
     * and forwards to the signup page if the student is inactive. If not found or an error occurs,
     * forwards to the CPF signup page with an appropriate error message.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cpf = request.getParameter("cpf");

        HttpSession session = request.getSession(true);

        try {
            InputValidation.validateCpf(cpf);
            cpf = InputNormalizer.normalizeCpf(cpf);

            Student student = new StudentDAO().findByCpf(cpf);

            if (student.getStatus() == StudentStatusEnum.INACTIVE) {
                request.setAttribute("student", student);
                session.setAttribute("student", student);
                request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            } else {
                ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "Aluno não encontrado", "/pages/students/signupCpf.jsp");
            }
        } catch (DataException | ValidationException e) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), "/pages/students/signupCpf.jsp");
        } catch (NotFoundException nfe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, nfe.getMessage(), "/pages/students/signupCpf.jsp");
        }
    }
}
