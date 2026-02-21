package com.example.schoolservlet.servlets.forgotPassword;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.EmailService;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.security.SecureRandom;

@WebServlet(name = "forget-password-send-code", value = "/auth/forgot-password/send-code")
public class SendCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

//        Variables:
        String input = "";
        HttpSession session = request.getSession();
        boolean hasException = false;
        String email = null;
        UserRoleEnum role = null;

//        Catching user's input:
        input = request.getParameter("input");

        try {
            InputValidation.validateIsNull("identificador", input);
        } catch (ValidationException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        input = input.trim();

        try {
            InputValidation.validateEnrollment(input);
            role = UserRoleEnum.STUDENT;
        } catch (ValidationException e) {}

        try {
            InputValidation.validateCpf(input);
            role = UserRoleEnum.ADMIN;
        } catch (ValidationException e) {}

        try{
            InputValidation.validateUserName(input);
            role = UserRoleEnum.TEACHER;
        } catch (ValidationException e) {}

        if (role == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Identificador inválido");
            hasException = true;
        }

        if (role == UserRoleEnum.STUDENT) {
            try {
                int id = InputNormalizer.normalizeEnrollment(input);
                StudentDAO studentDAO = new StudentDAO();
                Student student = studentDAO.findById(id);

                if (student != null) {
                    email = student.getEmail();

                    session.setAttribute("userId", student.getId());
                    session.setAttribute("role", UserRoleEnum.STUDENT);
                    session.setMaxInactiveInterval(60 * 15);
                }
            } catch (ValidationException e) {
                request.setAttribute("error", e.getMessage());
                hasException = true;
            } catch (DataException dae) {
                request.setAttribute("error", dae.getMessage());
                hasException = true;
            } catch (NotFoundException nfe) {
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
                return;
            }
        } else if (role == UserRoleEnum.ADMIN) {
            try {
                AdminDAO adminDAO = new AdminDAO();
                Admin admin = adminDAO.findByDocument(InputNormalizer.normalizeCpf(input));

                if (admin != null) {
                    email = admin.getEmail();

                    session.setAttribute("userId", admin.getId());
                    session.setAttribute("role", UserRoleEnum.ADMIN);
                    session.setMaxInactiveInterval(60 * 15);
                }
            } catch (DataException | ValidationException e) {
                request.setAttribute("error", e.getMessage());
                hasException = true;
            } catch (NotFoundException nfe){
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
                return;
            }
        } else if (role == UserRoleEnum.TEACHER) {
            try {
                TeacherDAO teacherDAO = new TeacherDAO();
                Teacher teacher = teacherDAO.findByUserName(input);

                if (teacher != null) {
                    email = teacher.getEmail();

                    session.setAttribute("userId", teacher.getId());
                    session.setAttribute("role", UserRoleEnum.TEACHER);
                    session.setMaxInactiveInterval(60 * 15);
                }
            } catch (DataException | RequiredFieldException e) {
                request.setAttribute("error", e.getMessage());
                hasException = true;
            } catch (NotFoundException nfe) {
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
                return;
            }
        }

        if (hasException) {
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        if (email != null && !email.isBlank()) {
            String code = String.format("%06d", (new SecureRandom()).nextInt(1000000));
            session.setAttribute("code", code);

            try {
                EmailService.sendEmail(email, "Recuperação de senha", "<h1>Recuperação da sua senha na Vértice</h1>" +
                        "<p>Esse código expirará em 15 minutos, caso não tenha sido você, ignore.</p>" +
                        "<br>" +
                        "<div style=\"background-color:#DDF8FF; border-radius:20px; height:200px;" +
                        "display:flex; flex-direction:column; justify-content: space-around\"><h3 style=\"text-align:center;\">Código:</h3>" +
                        "<h2 style=\"text-align:center;\">" + code + "</h2></div>");
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                request.setAttribute("error", "Não foi possível enviar o email nesse momento, tente novamente mais tarde");
                request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
    }
}
