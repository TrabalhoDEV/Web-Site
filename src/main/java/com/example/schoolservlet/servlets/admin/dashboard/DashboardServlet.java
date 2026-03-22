package com.example.schoolservlet.servlets.admin.dashboard;


import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet responsible for handling requests to the administrative dashboard page.
 * <p>
 * This servlet processes HTTP GET requests and forwards authorized administrators
 * to the dashboard view. Access to this page is restricted and validated before
 * the request is forwarded to the corresponding JSP page.
 * </p>
 */
@WebServlet(name = "admin-dashboard", value = "/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    /**
     * Handles HTTP GET requests for the administrative dashboard.
     * <p>
     * The method verifies whether the current user has administrative privileges
     * and, if authorized, forwards the request to the dashboard view page.
     * </p>
     *
     * @param request the {@link HttpServletRequest} containing the client request
     * @param response the {@link HttpServletResponse} used to return the HTML response
     * @throws ServletException if a servlet-related error occurs
     * @throws IOException if an input or output error occurs during request forwarding
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard/dashboard.jsp")
                .forward(request, response);
    }
}
