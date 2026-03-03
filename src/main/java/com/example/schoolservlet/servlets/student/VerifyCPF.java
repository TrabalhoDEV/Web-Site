package com.example.schoolservlet.servlets.student;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "VerifyCpf", value="/pages/students/verifyCpf")
public class VerifyCPF extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cpf = request.getParameter("cpf");

        StudentDAO dao = new StudentDAO();
        Student student = new Student();

        try {
            student = dao.findByCpf(cpf);
            request.setAttribute("student", student);
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            return;
        } catch (DataException de) {
            request.setAttribute("error", de.getMessage());
            request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
            return;
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signupCpf.jsp").forward(request, response);
            return;
        } catch (NotFoundException nfe) {
            request.setAttribute("error", "CPF Inválido");
            request.getRequestDispatcher("/pages/students/signuCpfp.jsp").forward(request, response);
            return;
        }
    }
}
