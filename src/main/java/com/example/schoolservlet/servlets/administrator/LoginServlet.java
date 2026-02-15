package com.example.schoolservlet.servlets.administrator;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PasswordValidationEnum;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "admin-auth", value = "/admin/auth")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

//        Variables' declaration:
        String cpf = "";
        String password = "";
        HttpSession session = request.getSession();
        AdminDAO adminDAO = new AdminDAO();

//        Getting the user input values:
        cpf = request.getParameter("cpf").trim();
       if(cpf == null || cpf.isEmpty()){
            request.setAttribute("error", "É necessário digitar seu cpf");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
            return;
        }
        password = request.getParameter("password").trim();
        if (password == null || password.isEmpty()){
            request.setAttribute("error", "É necessário digitar sua senha");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
            return;
        }

        if (!InputValidation.validateCpf(cpf)){
            request.setAttribute("error", "Formato de cpf inválido");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
            return;
        }

        if (InputValidation.validatePassword(password) != PasswordValidationEnum.RIGHT){
            request.setAttribute("error", "Cpf e/ou senha incorretos");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
            return;
        }

        if (adminDAO.login(cpf, password)){
            AuthenticatedUser user = new AuthenticatedUser(cpf, UserRoleEnum.ADMIN);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60 * 60);


//            TO DO: add context in AdminHomeServlet and change this redirect that brings all information necessarily to admin/index.jsp
            response.sendRedirect(request.getContextPath() + "/AdminHomeServlet");
        } else {
            request.setAttribute("error", "Cpf e/ou senha incorretos");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
        }
    }
}
