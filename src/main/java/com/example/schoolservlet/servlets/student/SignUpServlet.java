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

@WebServlet("/register")
public class SignUpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String enroll = request.getParameter("enroll");

        Student newStudent = new Student();
        newStudent.setName(name);
        try {
            InputValidation.validateEmail(email);
            newStudent.setEmail(email);
        } catch (ValidationException ve) {
            request.setAttribute("errorEmail", ve.getMessage());
        }
        try {
            InputValidation.validatePassword(password);
        } catch (ValidationException ve) {
            request.setAttribute("errorPassword", ve.getMessage());
        }

        int enrollNum = Integer.parseInt(enroll);
        newStudent.setId(enrollNum);

        StudentDAO dao = new StudentDAO();
        try {
            dao.update(newStudent);
            dao.updatePassword(enrollNum, password);
            response.sendRedirect("signup.jsp?register=sucess");
        } catch (NotFoundException nfe) {
            request.setAttribute("errorNotFound", nfe.getMessage());
        } catch (DataException de) {
            request.setAttribute("errorData", de.getMessage());
        } catch (ValidationException ve) {
            request.setAttribute("errorValidation", ve.getMessage());
        }
    }
}
