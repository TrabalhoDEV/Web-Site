package com.example.schoolservlet.servlets.auth;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "admin-auth", value = "/admin/auth")
public class AdminLoginServlet extends HttpServlet {
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
        try {
            cpf = request.getParameter("cpf");
            password = request.getParameter("password");

            InputValidation.validateCpf(cpf);
            InputValidation.validateIsNull("senha", password);

            if (adminDAO.login(cpf.trim(), password.trim())) {
                Admin admin = adminDAO.findByDocument(cpf);
                AuthenticatedUser user = new AuthenticatedUser(admin.getId(), admin.getEmail(), UserRoleEnum.ADMIN);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 60);


//            TO DO: add context in AdminHomeServlet and change this redirect that brings all information necessarily to admin/index.jsp
                response.sendRedirect(request.getContextPath() + "/admin/add-student");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.setAttribute("error", "Cpf e/ou senha incorretos");
                request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
            }
        } catch (ValidationException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
        } catch (DataException dae){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", dae.getMessage());
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
        } catch (NotFoundException nfe){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Cpf e/ou senha incorretos");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
        }
    }
}
