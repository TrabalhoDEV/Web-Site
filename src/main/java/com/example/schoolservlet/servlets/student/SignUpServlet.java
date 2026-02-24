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

@WebServlet(name = "SignUpServlet", value = "/student/register")
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
            request.setAttribute("error", ve.getMessage());
        }
        try {
            InputValidation.validatePassword(password);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
        }

        int enrollNum = 0;
        try {
            enrollNum = Integer.parseInt(enroll);
            newStudent.setId(enrollNum);
        } catch (NumberFormatException nfe) {
            request.setAttribute("error", nfe.getMessage());
        }

        StudentDAO dao = new StudentDAO();
        try {
            dao.update(newStudent);
            dao.updatePassword(enrollNum, password);
            response.sendRedirect(request.getContextPath() + "signup.jsp?register=sucess");
        } catch (NotFoundException nfe) {
            request.setAttribute("error", nfe.getMessage());
        } catch (DataException de) {
            request.setAttribute("error", de.getMessage());
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
        }
    }
}
