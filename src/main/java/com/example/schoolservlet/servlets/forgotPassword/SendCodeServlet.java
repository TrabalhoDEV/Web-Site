package com.example.schoolservlet.servlets.forgotPassword;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.models.Teacher;
import com.example.schoolservlet.utils.EmailService;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

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

//        Catching user's input:
        input = request.getParameter("input");

        if (input == null || input.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Digitar algo é obrigatório");
            hasException = true;
        }

        input = input.trim();

        if (InputValidation.validateEnrollment(input)){

        }

        if (InputValidation.validateCpf(input) && !hasException){
            AdminDAO adminDAO = new AdminDAO();
            Admin admin = adminDAO.findByDocument(InputNormalizer.normalizeCpf(input));

            if (admin != null){
                email = admin.getEmail();

                session.setAttribute("userId", admin.getId());
                session.setAttribute("role", UserRoleEnum.ADMIN);
                session.setMaxInactiveInterval(60 * 15);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "Usuário não encontrado");
                hasException = true;
            }
        }

        if (InputValidation.validateUserName(input) && !hasException){
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = teacherDAO.findByUserName(input);

            if (teacher != null){
                email = teacher.getEmail();

                session.setAttribute("userId", teacher.getId());
                session.setAttribute("role", UserRoleEnum.TEACHER);
                session.setMaxInactiveInterval(60 * 15);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.setAttribute("error", "Usuário não encontrado");
                hasException = true;
            }
        }

        if (hasException){
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        if (email != null && !email.isEmpty()){
            String code = String.valueOf((int) Math.round(Math.random() * 900000));
            session.setAttribute("code", code);

            try {
                EmailService.sendEmail(email, "Recuperação de senha", "<h1>Recuperação da sua senha na Vértice</h1>" +
                        "<p>Esse código expirará em 15 minutos, caso não tenha sido você, ignore.</p>" +
                        "<br>" +
                        "<div style=\"background-color:#DDF8FF; border-radius:20px; height:200px;" +
                        "display:flex; flex-direction:column; justify-content: space-around\"><h3 style=\"text-align:center;\">Código:</h3>" +
                        "<h2 style=\"text-align:center;\">"+code+"</h2></div>");
                response.sendRedirect(request.getContextPath() + "/auth/forgot-password/validate-code");
            } catch (Exception e){
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
                request.setAttribute("error", "Não foi possível enviar o email nesse momento, tente novamente mais tarde");
                request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            request.setAttribute("error", "Usuário não encontrado, tente novamente");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
        }
    }
}
