package com.example.schoolservlet.servlets.forgotPassword;

import com.example.schoolservlet.daos.AdminDAO;
import com.example.schoolservlet.utils.InputValidation;
import com.example.schoolservlet.utils.PasswordValidationEnum;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "forget-password-new-password", value = "/auth/forgot-password/new-password")
public class NewPasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        //        Atributes:
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        PasswordValidationEnum valid = null;
        HttpSession session = request.getSession(false);
        Integer userId = 0;
        UserRoleEnum role = null;

        if (session == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute("error", "É necessário revalidar a autenticidade, solicite um novo código");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        userId = (Integer) session.getAttribute("userId");
        role = (UserRoleEnum) session.getAttribute("role");

        if (userId == null || role == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "É necessário revalidar a autenticidade, solicite um código novamente");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/sendCode.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Digitar a senha é obrigatória");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
            return;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Digitar a confirmação da senha é obrigatória");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
            return;
        }

        valid = InputValidation.validatePassword(newPassword);

        if (valid != PasswordValidationEnum.RIGHT){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (valid == PasswordValidationEnum.MIN_LENGHT_NOT_REACHED){
                request.setAttribute("error","no mínimo 8 caracteres");
            } else if (valid == PasswordValidationEnum.MAX_LENGHT_EXCEEDED){
                request.setAttribute("error", "Senha deve ter no máximo 28 caracteres");
            } else if (valid == PasswordValidationEnum.MISSING_LOWERCASE){
                request.setAttribute("error", "Deve conter uma letra minúscula");
            } else if (valid == PasswordValidationEnum.MISSING_UPPERCASE){
                request.setAttribute("error", "Deve conter uma letra maiúscula");
            } else {
                request.setAttribute("error", "Deve conter um número");
            }
            request.getRequestDispatcher("WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Senhas digitadas são diferentes");
            request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
            return;
        }

        if (role == UserRoleEnum.ADMIN){
            AdminDAO adminDAO = new AdminDAO();

            if (adminDAO.updatePassword(userId, newPassword)){
                response.sendRedirect(request.getContextPath() + "/admin/auth");
                return;
            } else {
                request.setAttribute("error", "Não foi possível atualizar a senha, tente novamente mais tarde");
            }
        } else if (role == UserRoleEnum.TEACHER){

        } else if (role == UserRoleEnum.STUDENT){

        } else {

        }
        request.getRequestDispatcher("/WEB-INF/views/forgotPassword/newPassword.jsp").forward(request, response);
        return;
    }
}
