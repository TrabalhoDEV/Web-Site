package com.example.schoolservlet.servlets.forgotPassword;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "forget-password-validate-code", value = "/auth/forgot-password/validate-code")
public class ValidateCodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/validateCode.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

//        Atributes:
        String inputCode = request.getParameter("code");
        String code = null;
        HttpSession session = request.getSession(false);

        if (inputCode == null || inputCode.trim().isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Digitar o código é obrigatório");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/validateCode.jsp").forward(request, response);
            return;
        }

        inputCode = inputCode.trim();

        if (session == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", "Código expirado, solicite um novo");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        code = (String) session.getAttribute("code");
        if (code == null || code.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Código expirado. Solicite um novo código.");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        if (!code.equals(inputCode)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Código incorreto, tente novamente");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        session.removeAttribute("code");
        response.sendRedirect(request.getContextPath() + "/auth/forgot-password/new-password");
    }
}
