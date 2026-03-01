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

        String enrollment = request.getParameter("enrollment");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Student newStudent = new Student();
        int enrollNum = 0;
        try {
            enrollNum = Integer.parseInt(enrollment);
            newStudent.setId(enrollNum);
        } catch (NumberFormatException nfe) {
            request.setAttribute("error", nfe.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            return;
        }

        newStudent.setName(name);
        try {
            InputValidation.validateEmail(email);
            newStudent.setEmail(email);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            return;
        }
        try {
            InputValidation.validatePassword(password);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
            return;
        }

        StudentDAO dao = new StudentDAO();
        try {
            dao.update(newStudent);
            dao.updatePassword(enrollNum, password);
            response.sendRedirect(request.getContextPath() + "/pages/students/signup.jsp?register=sucess");
            return;
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("enrollment", enrollment);
            request.getRequestDispatcher("/pages/students/signup.jsp").forward(request, response);
        }
    }
}
