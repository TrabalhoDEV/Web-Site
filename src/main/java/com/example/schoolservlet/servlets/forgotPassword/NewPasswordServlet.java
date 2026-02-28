package com.example.schoolservlet.servlets.forgotPassword;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.daos.StudentDAO;
import com.example.schoolservlet.daos.TeacherDAO;
import com.example.schoolservlet.exceptions.*;
import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "forget-password-new-password", value = "/auth/forgot-password/new-password")
public class NewPasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "É necessário revalidar a autenticidade, solicite um código novamente", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
       UserRoleEnum role = (UserRoleEnum) session.getAttribute("role");

        if (userId == null || role == null) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "É necessário revalidar a autenticidade, solicite um código novamente", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        //        Atributes:
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        HttpSession session = request.getSession(false);
        Integer userId = 0;
        UserRoleEnum role = null;

        if (session == null) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "É necessário revalidar a autenticidade, solicite um código novamente", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        userId = (Integer) session.getAttribute("userId");
        role = (UserRoleEnum) session.getAttribute("role");

        if (userId == null || role == null) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_NOT_FOUND, "É necessário revalidar a autenticidade, solicite um código novamente", "/WEB-INF/views/forgotPassword/sendCode.jsp");
            return;
        }

        try {
            InputValidation.validatePassword(newPassword);
            InputValidation.validatePassword(confirmPassword);
        } catch (ValidationException ve) {
            request.setAttribute("error", ve.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Senhas digitadas são diferentes");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
            return;
        }

        try {
            if (role == UserRoleEnum.ADMIN) {
                AdminDAO adminDAO = new AdminDAO();

                adminDAO.updatePassword(userId, newPassword);
                response.sendRedirect(request.getContextPath() + "/admin/auth");
                return;
            } else if (role == UserRoleEnum.TEACHER) {
                TeacherDAO teacherDAO = new TeacherDAO();

                teacherDAO.updatePassword(userId, newPassword);
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            } else if (role == UserRoleEnum.STUDENT) {
                StudentDAO studentDAO = new StudentDAO();

                studentDAO.updatePassword(userId, newPassword);
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            }
        } catch (DataException dae){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", dae.getMessage());
        } catch (NotFoundException | ValidationException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute("error", e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
    }
}
