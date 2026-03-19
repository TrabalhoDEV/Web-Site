package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "SignUpServlet", value = "/student/register")
public class SignUpServlet extends HttpServlet {

    /**
     * Handles GET requests for the student signup page.
     *
     * <p>Forwards the request to the student signup JSP page and sets the response content type to HTML.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
    }

    /**
     * Handles POST requests for student signup.
     *
     * <p>Processes form data including name, email, password, confirmation password, and CPF.
     * Validates input, checks for existing student by CPF, sets student attributes, and attempts enrollment.
     * On success, redirects to the index page. On failure, forwards back to the signup page with error messages.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String cpf = request.getParameter("cpf");

        Student newStudent = new Student();
        StudentDAO dao = new StudentDAO();

        try {
            newStudent = dao.findByCpf(cpf);
        } catch (ValidationException ve) {
            request.setAttribute("student", newStudent);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (NotFoundException nfe) {
            request.setAttribute("student", newStudent);
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (DataException de) {
            request.setAttribute("student", newStudent);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        }

        try {
            InputValidation.validateStudentName(name);
            newStudent.setName(InputNormalizer.normalizeName(name));

            InputValidation.validateEmail(email);
            newStudent.setEmail(email);

            InputValidation.validatePassword(password);
            InputValidation.validatePassword(confirmPassword);

            if (!password.equals(confirmPassword)) throw new ValidationException("Senhas informadas devem ser iguais");

            newStudent.setPassword(password);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            return;
        }

        try {
            dao.enrollIn(newStudent);
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } catch (ValidationException ve) {
            request.setAttribute("student", newStudent);
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (NotFoundException nfe) {
            request.setAttribute("student", newStudent);
            request.setAttribute("error", "Matrícula Inválida");
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (DataException de) {
            request.setAttribute("student", newStudent);
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        }
    }
}
