package com.example.schoolservlet.servlets.forgotPassword;

import com.example.schoolservlet.exceptions.ValidationException;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "forget-password-validate-code", value = "/auth/forgot-password/validate-code")
public class ValidateCodeServlet extends HttpServlet {

    /**
     * Handles GET requests to display the code validation page for password recovery.
     *
     * <p>Checks if a session exists and contains a valid code. If the session or code
     * is missing or expired, forwards the user to the code request page with an error message.
     * Otherwise, forwards the request to the code validation JSP page.</p>
     *
     * @param request  the HttpServletRequest containing session and parameters
     * @param response the HttpServletResponse used for forwarding or error handling
     * @throws ServletException if an error occurs during request forwarding
     * @throws IOException      if an I/O error occurs during request forwarding
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession(false);

        if (session == null){
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "Código expirado. Solicite um novo.", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        String code = (String) session.getAttribute("code");
        if (code == null || code.isEmpty()) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "Código expirado. Solicite um novo código.", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/validateCode.jsp").forward(request, response);
    }

    /**
     * Handles POST requests to validate the code entered by the user for password recovery.
     *
     * <p>Checks if a session exists and contains the correct code. If the session is missing,
     * expired, or the code is invalid, forwards the user back to the code entry page with an error.
     * On successful validation, removes the code from the session and redirects the user
     * to the page for setting a new password.</p>
     *
     * @param request  the HttpServletRequest containing session and form parameters
     * @param response the HttpServletResponse used for forwarding, error handling, or redirecting
     * @throws ServletException if an error occurs during request forwarding
     * @throws IOException      if an I/O error occurs during request forwarding or redirection
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

//        Atributes:
        String inputCode = request.getParameter("code");
        String code = null;
        HttpSession session = request.getSession(false);

        if (session == null){
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "Código expirado. Solicite um novo.", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        code = (String) session.getAttribute("code");
        if (code == null || code.isEmpty()) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "Código expirado. Solicite um novo código.", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        try{
            InputValidation.validateIsNull("código", inputCode);

            inputCode = inputCode.trim();
        } catch (ValidationException e){
            ErrorHandler.forward(request, response, e.getStatus(), e.getMessage(), "/WEB-INF/views/forgotPassword/validateCode.jsp");
            return;
        }
        if (!code.equals(inputCode)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Código incorreto, tente novamente");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/validateCode.jsp").forward(request, response);
            return;
        }

        session.removeAttribute("code");
        response.sendRedirect(request.getContextPath() + "/auth/forgot-password/new-password");
    }
}
