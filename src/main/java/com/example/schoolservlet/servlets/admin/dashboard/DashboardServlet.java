package com.example.schoolservlet.servlets.admin.dashboard;


import com.example.schoolservlet.utils.AccessValidation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "admin-dashboard", value = "/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        if (!AccessValidation.isAdmin(request, response)) return;

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard/dashboard.jsp")
                .forward(request, response);
    }
}
