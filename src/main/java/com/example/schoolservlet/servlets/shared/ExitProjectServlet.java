package com.example.schoolservlet.servlets.shared;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "exit-from-profile", value = "/logout")
public class ExitProjectServlet extends HttpServlet {

    /**
     * Servlet for handling user logout.
     *
     * <p>GET: Invalidates the current session if it exists and redirects the user to the index page.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect("index.html");
    }
}
