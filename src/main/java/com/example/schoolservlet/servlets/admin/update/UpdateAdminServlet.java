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

    /**
     * Handles GET requests to load the admin's current data for update.
     *
     * <p>Retrieves the authenticated user from the session, fetches admin data,
     * and forwards to the admin update JSP page. If the session is expired or
     * an error occurs, it forwards to the appropriate error or login page.</p>
     *
     * @param request  HttpServletRequest containing client request data.
     * @param response HttpServletResponse used to forward to JSP or error page.
     * @throws ServletException If a servlet error occurs during forwarding.
     * @throws IOException      If an I/O error occurs during forwarding.
     */
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

    /**
     * Handles POST requests to update admin user information and password.
     *
     * <p>Validates email and document, updates password if provided and confirmed,
     * updates admin data via {@link AdminDAO}, and redirects to the admin detail page.
     * Errors are handled by forwarding to the update page with messages.</p>
     *
     * @param request  HttpServletRequest containing form data.
     * @param response HttpServletResponse used to redirect or forward on error.
     * @throws ServletException If a servlet error occurs during forwarding.
     * @throws IOException      If an I/O error occurs during forwarding or redirect.
     */
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

    /**
     * Retrieves the admin data for the given authenticated user and sets it as a request attribute.
     *
     * @param request  HttpServletRequest to store the admin data.
     * @param response HttpServletResponse, not directly used but included for context.
     * @param user     AuthenticatedUser whose admin data is to be fetched.
     * @throws NotFoundException   If the admin record is not found.
     * @throws DataException       If a data access error occurs.
     * @throws ValidationException If any validation error occurs during retrieval.
     */
    private void getData(HttpServletRequest request, HttpServletResponse response, AuthenticatedUser user) throws NotFoundException, DataException, ValidationException {
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.findById(user.id());

        request.setAttribute("admin", admin);
    }
}
