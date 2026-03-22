package com.example.schoolservlet.utils;

import com.example.schoolservlet.exceptions.UnauthorizedException;
import com.example.schoolservlet.utils.enums.UserRoleEnum;
import com.example.schoolservlet.utils.records.AuthenticatedUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Utility class for role-based access validation using the authenticated user
 * stored in the current HTTP session.
 */
public class AccessValidation {
    /**
     * Validates whether the current session user has ADMIN role.
     *
     * <p>If the session is missing/expired or the user does not have the required role,
     * the request is forwarded to the admin login page with an appropriate status/message.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @return {@code true} if the user is authorized as admin; otherwise {@code false}
     * @throws IOException if an input/output error occurs during forwarding
     * @throws ServletException if a servlet-specific error occurs during forwarding
     */
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

    /**
     * Validates whether the current session user has TEACHER role.
     *
     * <p>If the session is missing/expired or the user does not have the required role,
     * the request is forwarded to the public login page with an appropriate status/message.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @return {@code true} if the user is authorized as teacher; otherwise {@code false}
     * @throws IOException if an input/output error occurs during forwarding
     * @throws ServletException if a servlet-specific error occurs during forwarding
     */
    public static boolean isTeacher(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.TEACHER) {
                throw new UnauthorizedException("professor");
            }

            return true;
        } catch (NullPointerException npe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/index.jsp");
            return false;
        } catch (UnauthorizedException ue){
            ue.printStackTrace();
            ErrorHandler.forward(request, response, ue.getStatus(), ue.getMessage(), "/index.jsp");
            return false;
        }
    }

    /**
     * Validates whether the current session user has STUDENT role.
     *
     * <p>If the session is missing/expired or the user does not have the required role,
     * the request is forwarded to the public login page with an appropriate status/message.
     *
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @return {@code true} if the user is authorized as student; otherwise {@code false}
     * @throws IOException if an input/output error occurs during forwarding
     * @throws ServletException if a servlet-specific error occurs during forwarding
     */
    public static boolean isStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.STUDENT) {
                throw new UnauthorizedException("aluno");
            }

            return true;
        } catch (NullPointerException npe) {
            ErrorHandler.forward(request, response, HttpServletResponse.SC_UNAUTHORIZED, "Sessão expirada, faça login novamente", "/index.jsp");
            return false;
        } catch (UnauthorizedException ue){
            ue.printStackTrace();
            ErrorHandler.forward(request, response, ue.getStatus(), ue.getMessage(), "/index.jsp");
            return false;
        }
    }
}