package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.StudentStatusEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "ValidateCpfServlet", value = "/student/validate/cpf")
public class ValidateCpfServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cpf = request.getParameter("cpf");

        try {
            InputValidation.validateCpf(cpf);
            cpf = InputNormalizer.normalizeCpf(cpf);

            Student student = new StudentDAO().findByCpf(cpf);

            if (student.getStatus() == StudentStatusEnum.INACTIVE) {
                request.setAttribute("student", student);
                request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Aluno já cadastrado");
                request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
            }
        } catch (DataException | ValidationException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
        } catch (NotFoundException nfe) {
            request.setAttribute("error", "Aluno não encontrado");
            request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
        }
    }
}
