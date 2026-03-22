package com.example.schoolservlet.servlets.auth;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "admin-auth", value = "/admin/auth")
public class AdminLoginServlet extends HttpServlet {

    /**
     * Servlet for handling admin authentication and login.
     *
     * <p>GET requests forward the user to the admin login JSP page.
     * POST requests validate CPF and password input, authenticate the admin using {@link AdminDAO},
     * create an {@link AuthenticatedUser} session if successful, and redirect to the admin dashboard.
     * Invalid credentials or errors forward back to the login page with appropriate error messages.</p>
     *
     * @param request  HttpServletRequest containing login form data.
     * @param response HttpServletResponse used to forward or redirect the user.
     * @throws ServletException If a servlet error occurs during forwarding.
     * @throws IOException      If an I/O error occurs during forwarding or redirect.
     */
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

            cpf = InputNormalizer.normalizeCpf(cpf);

            if (adminDAO.login(cpf, password.trim())) {
                Admin admin = adminDAO.findByDocument(cpf);
                AuthenticatedUser user = new AuthenticatedUser(admin.getId(), admin.getEmail(), UserRoleEnum.ADMIN);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60 * 60);

                response.sendRedirect(request.getContextPath() + "/admin/student/find-many");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                request.setAttribute("error", "Cpf e/ou senha incorretos");
                request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
            }
        } catch (ValidationException | DataException e){
            e.printStackTrace();
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/pages/admin/login.jsp");
        }  catch (NotFoundException nfe){
            nfe.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Cpf e/ou senha incorretos");
            request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
        }
    }
}
