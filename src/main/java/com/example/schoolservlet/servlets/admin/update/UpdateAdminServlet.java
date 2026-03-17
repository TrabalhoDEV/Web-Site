package com.example.schoolservlet.servlets.admin.update;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputNormalizer;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "UpdateAdminServlet", value = "/admin/update")
public class UpdateAdminServlet extends HttpServlet {
    private final String page = "/WEB-INF/views/admin/update/admin.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        AuthenticatedUser user = null;

        try {
            HttpSession session = request.getSession(false);
            user = (AuthenticatedUser) session.getAttribute("user");
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/pages/admin/login.jsp");
            return;
        }

        try {
            getData(request, response, user);
        } catch (NotFoundException | DataException | ValidationException e) {
            e.printStackTrace();
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), this.page);
            return;
        }

        request.getRequestDispatcher(this.page).forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        AuthenticatedUser user = null;

        try {
            HttpSession session = request.getSession(false);
            user = (AuthenticatedUser) session.getAttribute("user");
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/pages/admin/login.jsp");
            return;
        }

        String email = request.getParameter("email");
        String document = request.getParameter("document");
        String password = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            InputValidation.validateEmail(email);
            InputValidation.validateCpf(document);
        } catch (ValidationException e) {
            e.printStackTrace();
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), this.page);
            return;
        }

        if (password != null && confirmPassword != null) {
            try {
                InputValidation.validatePassword(password);

                getData(request, response, user);

                if (!password.equals(confirmPassword)){
                    request.setAttribute("error", "Senha confirmada deve ser igual a nova senha");

                    request.getRequestDispatcher(this.page).forward(request, response);
                    return;
                }

                AdminDAO adminDAO = new AdminDAO();

                adminDAO.updatePassword(user.id(), password);
            } catch (NotFoundException | DataException | ValidationException e) {
                e.printStackTrace();
                ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), this.page);
                return;
            }
        }

        Admin admin = new Admin();
        admin.setId(user.id());
        admin.setEmail(email);
        admin.setDocument(InputNormalizer.normalizeCpf(document));

        try {
            AdminDAO adminDAO = new AdminDAO();
            adminDAO.update(admin);
        } catch (NotFoundException | DataException | ValidationException e) {
            e.printStackTrace();
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), this.page);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/find-one");
    }

    private void getData(HttpServletRequest request, HttpServletResponse response, AuthenticatedUser user) throws NotFoundException, DataException, ValidationException {
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.findById(user.id());

        request.setAttribute("admin", admin);
    }
}
