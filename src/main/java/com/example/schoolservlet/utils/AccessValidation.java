package com.example.schoolservlet.utils;

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
                request.getRequestDispatcher("/pages/admin/login.jsp").forward(request, response);
                return false;
            }

            return true;
        } catch (NullPointerException npe) {
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/pages/admin/login.jsp")
                    .forward(request, response);
            return false;
        }
    }

    public static boolean isTeacher(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.TEACHER) {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return false;
            }

            return true;
        } catch (NullPointerException npe) {
            request.setAttribute("error","Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return false;
        }
    }

    public static boolean isStudent(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            HttpSession session = request.getSession(false);
            AuthenticatedUser user = (AuthenticatedUser) session.getAttribute("user");

            if (user.role() != UserRoleEnum.STUDENT) {
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return false;
            }

            return true;
        } catch (NullPointerException npe) {
            request.setAttribute("error", "Sessão expirada, faça login novamente");
            request.getRequestDispatcher("/index.jsp")
                    .forward(request, response);
            return false;
        }
    }
}
