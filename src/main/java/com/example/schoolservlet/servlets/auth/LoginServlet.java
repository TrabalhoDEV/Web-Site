package com.example.schoolservlet.servlets.auth;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.RequiredFieldException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "auth", value = "/auth")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

//        Atributes:
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        UserRoleEnum role = null;

        if (identifier == null ||identifier.isBlank()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Matrícula/usuário é obrigatório");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

        identifier = identifier.trim();

        try{
            InputValidation.validateUserName(identifier);
            role = UserRoleEnum.TEACHER;
        } catch (ValidationException e){
            if (role == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.setAttribute("error", e.getMessage());
            }
        }

        try{
            InputValidation.validateEnrollment(identifier);
            role = UserRoleEnum.STUDENT;
        } catch (ValidationException e){
            if (role == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.setAttribute("error", e.getMessage());
            }
        }

        if (role == null) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        try {
            InputValidation.validateIsNull("senha", password);

            if (role == UserRoleEnum.TEACHER) {
                try {
                    TeacherDAO teacherDAO = new TeacherDAO();
                    String userName = InputNormalizer.normalizeUserName(identifier);

                    if (teacherDAO.login(userName, password)) {
                        Teacher teacher = teacherDAO.findByUserName(userName);

                        AuthenticatedUser user = new AuthenticatedUser(teacher.getId(), teacher.getEmail(), UserRoleEnum.TEACHER);
                        session.setAttribute("user", user);
                        session.setMaxInactiveInterval(60 * 60);

                        response.sendRedirect(request.getContextPath() + "/teacher/home");
                        return;
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        request.setAttribute("error", "Usuário e/ou senha inválidos");
                        request.getRequestDispatcher("index.jsp").forward(request, response);
                    }
                } catch (DataException dae){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    request.setAttribute("error", dae.getMessage());
                } catch (RequiredFieldException rfe){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("error", rfe.getMessage());
                } catch (NotFoundException nfe){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("error", "Usuário e/ou senha inválidos");
                }
            } else if (role == UserRoleEnum.STUDENT){
                try {
                    InputValidation.validateEnrollment(identifier);
                    StudentDAO studentDAO = new StudentDAO();

                    if (studentDAO.login(identifier, password)) {
                        Student student = studentDAO.findById(InputNormalizer.normalizeEnrollment(identifier));

                        AuthenticatedUser user = new AuthenticatedUser(student.getId(), student.getEmail(), UserRoleEnum.STUDENT);
                        session.setAttribute("user", user);
                        session.setMaxInactiveInterval(60 * 60);

                        response.sendRedirect(request.getContextPath() + "/student/home");
                        return;
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        request.setAttribute("error", "Usuário e/ou senha inválidos");
                    }
                } catch (DataException dae){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    request.setAttribute("error", dae.getMessage());
                } catch (RequiredFieldException rfe){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    request.setAttribute("error", rfe.getMessage());
                } catch (NotFoundException nfe){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setAttribute("error", "Usuário e/ou senha inválidos");
                }
            }

            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (ValidationException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
