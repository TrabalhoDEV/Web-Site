package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "SignUpServlet", value = "/pages/students/register")
public class SignUpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String cpf = request.getParameter("cpf");

        Student newStudent = new Student();
        StudentDAO dao = new StudentDAO();

        try {
            newStudent = dao.findByCpf(cpf);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (NotFoundException nfe) {
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (DataException de) {
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        }

        newStudent.setName(name);

        try {
            InputValidation.validateEmail(email);
            newStudent.setEmail(email);
            InputValidation.validatePassword(password);
            newStudent.setPassword(password);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            return;
        }

        try {
            dao.enrollIn(newStudent);
            response.sendRedirect(request.getContextPath() + "/pages/students/signup.jsp?register=success");
            return;
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (NotFoundException nfe) {
            request.setAttribute("error", "Matrícula Inválida");
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        } catch (DataException de) {
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        }
    }
}
