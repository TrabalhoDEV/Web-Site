package com.example.schoolservlet.servlets.auth;

import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PasswordValidationEnum;
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
        boolean userOrPasswordWrong = false;

        if (identifier == null || identifier.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Matrícula/usuário é obrigatório");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        if (password == null || password.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Senha é obrigatória");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        if (InputValidation.validatePassword(password) != PasswordValidationEnum.RIGHT){
            userOrPasswordWrong = true;
        }

        if (InputValidation.validateUserName(identifier) && !userOrPasswordWrong){
            TeacherDAO teacherDAO = new TeacherDAO();
            String userName = InputNormalizer.normalizeUserName(identifier);

            if (teacherDAO.login(userName, password)){
                Teacher teacher = teacherDAO.findByUserName(userName);

                AuthenticatedUser user = new AuthenticatedUser(teacher.getId(), teacher.getEmail(), UserRoleEnum.TEACHER);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 60);

                response.sendRedirect(request.getContextPath() + "/teacher/home");
                return;
            }
        } else if (InputValidation.validateEnrollment(identifier) && !userOrPasswordWrong){
            StudentDAO studentDAO = new StudentDAO();

            if (studentDAO.login(identifier, password)){
                Student student = studentDAO.findById(InputNormalizer.normalizeEnrollment(identifier));

                AuthenticatedUser user = new AuthenticatedUser(student.getId(), student.getEmail(), UserRoleEnum.STUDENT);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 60);

                response.sendRedirect(request.getContextPath() + "/student/home");
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        request.setAttribute("error", "Usuário e/ou senha inválidos");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
