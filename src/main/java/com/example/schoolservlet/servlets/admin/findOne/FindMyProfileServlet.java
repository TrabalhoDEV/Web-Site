package com.example.schoolservlet.servlets.admin.findOne;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.exceptions.DataException;
import com.example.schoolservlet.exceptions.NotFoundException;
import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.models.Admin;
import com.example.schoolservlet.utils.AccessValidation;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

/**
 * Servlet for displaying the profile of the currently authenticated admin.
 *
 * <p>This servlet handles HTTP GET requests to show the admin's own profile page.
 * It first checks that the user has admin access, retrieves the authenticated user
 * from the session, and fetches the full admin data from the database using AdminDAO.
 * Any session expiration or data retrieval errors are forwarded to appropriate pages
 * with the correct HTTP status and messages.</p>
 *
 * @see HttpServlet
 * @see AdminDAO
 * @see AccessValidation
 */
@WebServlet(name = "admin-find-profile", value = "/admin/find-one")
public class FindMyProfileServlet extends HttpServlet {
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

        try{
            getData(request, response, user);
        } catch (NotFoundException | DataException | ValidationException e){
            e.printStackTrace();
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/admin/index.jsp");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/index.jsp").forward(request, response);
    }

    private void getData(HttpServletRequest request, HttpServletResponse response, AuthenticatedUser user) throws NotFoundException, DataException, ValidationException{
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.findById(user.id());

        request.setAttribute("admin", admin);
    }
}
