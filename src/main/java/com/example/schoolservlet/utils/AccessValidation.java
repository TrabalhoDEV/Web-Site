package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.UnauthorizedException;
import com.example.schoolservlet.utils.Constants;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AccessValidation {
    public static boolean isAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.ADMIN) {
                throw new UnauthorizedException("administrador");
            }

            return true;
        } catch (NullPointerException npe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/pages/admin/login.jsp");
            return false;
        } catch (UnauthorizedException ue){
            ue.printStackTrace();
            ErrorHandler.forward(request, response, ue.getStatus(), ue.getMessage(), "/pages/admin/login.jsp");
            return false;
        }
    }

    public static boolean isTeacher(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.TEACHER) {
                throw new UnauthorizedException("professor");
            }

            return true;
        } catch (NullPointerException npe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "index.jsp");
            return false;
        } catch (UnauthorizedException ue){
            ue.printStackTrace();
            ErrorHandler.forward(request, response, ue.getStatus(), ue.getMessage(), "/index.jsp");
            return false;
        }
    }

    public static boolean isStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.STUDENT) {
                throw new UnauthorizedException("aluno");
            }

            return true;
        } catch (NullPointerException npe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "index.jsp");
            return false;
        } catch (UnauthorizedException ue){
            ue.printStackTrace();
            ErrorHandler.forward(request, response, ue.getStatus(), ue.getMessage(), "/index.jsp");
            return false;
        }
    }
}
